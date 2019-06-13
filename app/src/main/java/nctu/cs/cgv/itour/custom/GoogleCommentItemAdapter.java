package nctu.cs.cgv.itour.custom;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;

import nctu.cs.cgv.itour.R;
import nctu.cs.cgv.itour.object.GoogleComment;

public class GoogleCommentItemAdapter extends RecyclerView.Adapter<GoogleCommentItemAdapter.ViewHolder> {

    private static final String TAG = "GoogleCommentItemAdapter";
    private ArrayList<GoogleComment> comments;

    public GoogleCommentItemAdapter(Context context, ArrayList<GoogleComment> comments) {
        this.comments = comments;
    }

    @NonNull
    @Override
    public GoogleCommentItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View commentItemView = inflater.inflate(R.layout.item_google_comment, parent, false);
        return new ViewHolder(commentItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        GoogleComment comment = comments.get(position);
        viewHolder.username.setText(comment.username);
        viewHolder.msg.setText(comment.comment);
        viewHolder.rate.setText(comment.rate);
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return comments.size();
    }

    public GoogleComment getItem(int index) {
        return comments.get(index);
    }

    public void addAll(Collection<GoogleComment> commentCollection) {
        comments.addAll(commentCollection);
        notifyDataSetChanged();
    }

    public void add(GoogleComment comment) {
        Log.d("GCV", "add comment!");
        comments.add(comment);
        notifyDataSetChanged();
    }

    public void clear() {
        comments.clear();
        notifyDataSetChanged();
    }

    public void insert(GoogleComment comment, int index) {
        comments.add(index, comment);
        notifyItemInserted(index);
    }

    public void remove(int index) {
        comments.remove(index);
        notifyItemRemoved(index);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        TextView msg;
        TextView rate;
        ViewHolder(View view) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(view);

            username = view.findViewById(R.id.tv_google_username);
            msg = view.findViewById(R.id.tv_google_msg);
            rate = view.findViewById(R.id.tv_google_rate);
        }
    }
}