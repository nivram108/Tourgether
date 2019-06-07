package nctu.cs.cgv.itour.custom;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nctu.cs.cgv.itour.R;
import nctu.cs.cgv.itour.object.Checkin;
import nctu.cs.cgv.itour.object.GlideApp;
import nctu.cs.cgv.itour.object.SpotList;

import static nctu.cs.cgv.itour.MyApplication.VERSION_ALL_FEATURE;
import static nctu.cs.cgv.itour.MyApplication.VERSION_ONLY_GOOGLE_COMMENT;
import static nctu.cs.cgv.itour.MyApplication.VERSION_ONLY_SELF_CHECKIN;
import static nctu.cs.cgv.itour.MyApplication.VERSION_OPTION;
import static nctu.cs.cgv.itour.MyApplication.dirPath;
import static nctu.cs.cgv.itour.MyApplication.fileDownloadURL;
import static nctu.cs.cgv.itour.MyApplication.mapTag;
import static nctu.cs.cgv.itour.MyApplication.spotList;

/**
 * Created by lobZter on 2017/8/18.
 * display checkin content
 */

public class CheckinItemAdapter extends RecyclerView.Adapter<CheckinItemAdapter.ViewHolder> {

    private static final String TAG = "CheckinItemAdapter";
    public ArrayList<Checkin> checkins;
    private Context context;
    private Fragment parentFragment;

    public CheckinItemAdapter(Context context, ArrayList<Checkin> checkins, Fragment parentFragment) {
        this.checkins = checkins;
        this.context = context;
        this.parentFragment = parentFragment;
    }

    @NonNull
    @Override
    public CheckinItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View checkinCardView = inflater.inflate(R.layout.item_checkin_card, parent, false);

        return new ViewHolder(checkinCardView);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckinItemAdapter.ViewHolder viewHolder, int position) {
        if (VERSION_OPTION == VERSION_ALL_FEATURE) {
            Checkin checkin = checkins.get(position);
            viewHolder.username.setText(checkin.username);
            viewHolder.location.setText(checkin.location);
            viewHolder.description.setText(checkin.description);

            int likeNum = checkin.likeNum;
            if (checkin.like != null && checkin.like.size() > 0) {
                likeNum += checkin.like.size();
            }
            viewHolder.like.setText(String.valueOf(likeNum));
            setPhoto(viewHolder, checkin.photo);
        } else if (VERSION_OPTION == VERSION_ONLY_GOOGLE_COMMENT) {
            Checkin checkin = checkins.get(position);
            viewHolder.username.setText(checkin.location);
            viewHolder.location.setVisibility(View.GONE);
            viewHolder.description.setVisibility(View.GONE);
            viewHolder.like.setVisibility(View.GONE);
            viewHolder.heart.setVisibility(View.GONE);
            viewHolder.atSign.setVisibility(View.GONE);
            setPhoto(viewHolder, checkin.photo);
        }
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return checkins.size();
    }

    public Checkin getItem(int index) {
        return checkins.get(index);
    }

    public void addAll(Collection<Checkin> checkinList) {
        checkins.addAll(checkinList);
        notifyDataSetChanged();
    }

    public void add(Checkin checkin) {
        checkins.add(checkin);
        notifyDataSetChanged();
    }

    public void clear() {
        checkins.clear();
        notifyDataSetChanged();
    }

    public void insert(Checkin checkin, int index) {
        checkins.add(index, checkin);
        notifyItemInserted(index);
    }

    public void remove(int index) {
        checkins.remove(index);
        notifyItemRemoved(index);
    }

    public void setSpotChecking() {
        checkins.clear();
        if (spotList == null) spotList = new SpotList(new File(dirPath + "/" + mapTag + "_spot_list.txt"));

        ArrayList<String> array = new ArrayList<>();
        array.addAll(spotList.getFullSpotsName());

        Log.d("GCV", "array size : " + array.size());
        for (String spotName:array) {
            Log.d("GCV", "load");
            Checkin checkin = new Checkin();
            checkin.setSpot(spotName);
            checkins.add(checkin);
        }
        notifyDataSetChanged();

    }
    private void setPhoto(final ViewHolder viewHolder, final String filename) {
        if(parentFragment.getActivity() == null) return;
        if (filename.equals("")) {
            viewHolder.photo.setVisibility(View.GONE);
            return;
        } else {
            viewHolder.photo.setVisibility(View.VISIBLE);
        }

        if (VERSION_OPTION == VERSION_ALL_FEATURE) {
            Glide.with(parentFragment.getActivity())
                    .load(fileDownloadURL + "?filename=" + filename)
                    .apply(new RequestOptions().placeholder(R.drawable.ic_broken_image_black_48dp))
                    .into(viewHolder.photo);
        } else if (VERSION_OPTION == VERSION_ONLY_GOOGLE_COMMENT ) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("image").child(filename + ".png");
            GlideApp.with(parentFragment.getActivity())
                    .load(storageReference)
                    .apply(new RequestOptions().placeholder(R.drawable.ic_broken_image_black_48dp))
                    .into(viewHolder.photo);
        }

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView photo;
        TextView username;
        TextView location;
        TextView like;
        TextView description;
        TextView atSign;
        ImageView heart;
        ViewHolder(View view) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(view);

            photo = view.findViewById(R.id.photo);
            username = view.findViewById(R.id.tv_username);
            location = view.findViewById(R.id.tv_location);
            like = view.findViewById(R.id.tv_like);
            description = view.findViewById(R.id.tv_description);
            atSign = view.findViewById(R.id.at_sign);
            heart = view.findViewById(R.id.heart);
        }
    }
}
