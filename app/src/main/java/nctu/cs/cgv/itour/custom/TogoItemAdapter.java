package nctu.cs.cgv.itour.custom;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nctu.cs.cgv.itour.R;
import nctu.cs.cgv.itour.object.TogoData;

import static nctu.cs.cgv.itour.MyApplication.mapTag;

public class TogoItemAdapter extends RecyclerView.Adapter<TogoItemAdapter.ViewHolder>{
    public List<TogoData> togoDataList;
    @Override
    public TogoItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View checkinCardView = inflater.inflate(R.layout.item_togo, parent, false);

        return new TogoItemAdapter.ViewHolder(checkinCardView);
    }

    public TogoItemAdapter(List<TogoData> togoDataList) {

        this.togoDataList = new ArrayList<>();
        for ( TogoData togoData: togoDataList) {
            addTogo(togoData);
        }
    }

    @Override
    public void onBindViewHolder(TogoItemAdapter.ViewHolder holder, int position) {
        TogoData togoData = this.togoDataList.get(position);
        holder.locationName.setText(togoData.locationName);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView locationName;

        ViewHolder(View view) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(view);
            locationName = view.findViewById(R.id.tv_location_name);
        }

    }

    @Override
    public int getItemCount() {
        return togoDataList.size();
    }

    public void queryTogoList() {

//        togoDataList.clear();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Query commentNotificationQuery = FirebaseDatabase.getInstance().getReference().child("togo_list").child(mapTag).child(uid);
        commentNotificationQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                TogoData togoData = dataSnapshot.getValue(TogoData.class);
                Log.d("NIVRAMM", "get togo firebase :" + togoData.locationName);

                togoDataList.add(0, togoData);
                notifyItemInserted(0);
                notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void removeTogo(TogoData togoData) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("togo_list").child(mapTag).child(uid).child(togoData.locationName);
        databaseReference.removeValue();
    }

    public void addTogo(TogoData togoData) {
        notifyItemInserted(0);
        notifyDataSetChanged();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        final String pushKey = togoData.locationName;

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/togo_list/" + mapTag + "/" + uid + "/" + pushKey, togoData.toMap());
        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates,
                new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, final DatabaseReference databaseReference) {
                        //commentMsg.setText("");
                    }
                });
    }
}
