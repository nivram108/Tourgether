package nctu.cs.cgv.itour.custom;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;

import nctu.cs.cgv.itour.R;
import nctu.cs.cgv.itour.object.CheckinComment;

public class CheckinCommentItemAdapter extends RecyclerView.Adapter<CheckinCommentItemAdapter.ViewHolder> {

    private static final String TAG = "CheckinCommentItemAdapter";
    private ArrayList<CheckinComment> checkinComments;

    public CheckinCommentItemAdapter(Context context, ArrayList<CheckinComment> checkinComments) {
        this.checkinComments = checkinComments;
    }

    @NonNull
    @Override
    public CheckinCommentItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View commentItemView = inflater.inflate(R.layout.item_checkin_comment, parent, false);
        return new ViewHolder(commentItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        CheckinComment checkinComment = checkinComments.get(position);
        viewHolder.username.setText(checkinComment.username);
        viewHolder.msg.setText(checkinComment.msg);
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return checkinComments.size();
    }

    public CheckinComment getItem(int index) {
        return checkinComments.get(index);
    }

    public void addAll(Collection<CheckinComment> checkinCommentCollection) {
        checkinComments.addAll(checkinCommentCollection);
        notifyDataSetChanged();
    }

    public void add(CheckinComment checkinComment) {
        checkinComments.add(checkinComment);
        notifyDataSetChanged();
    }

    public void clear() {
        checkinComments.clear();
        notifyDataSetChanged();
    }

    public void insert(CheckinComment checkinComment, int index) {
        checkinComments.add(index, checkinComment);
        notifyItemInserted(index);
    }

    public void remove(int index) {
        checkinComments.remove(index);
        notifyItemRemoved(index);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        TextView msg;

        ViewHolder(View view) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(view);

            username = view.findViewById(R.id.tv_username);
            msg = view.findViewById(R.id.tv_msg);
        }
    }
}