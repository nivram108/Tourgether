package nctu.cs.cgv.itour.custom;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.Collection;

import nctu.cs.cgv.itour.R;
import nctu.cs.cgv.itour.object.SystemNotification;

import static nctu.cs.cgv.itour.MyApplication.fileDownloadURL;

/**
 * Created by lobZter on 2017/7/10.
 */

public class NotificationItemAdapter extends RecyclerView.Adapter<NotificationItemAdapter.ViewHolder> {

    private static final String TAG = "NotificationItemAdapter";
    private ArrayList<SystemNotification> systemNotifications;
    private Context context;

    public NotificationItemAdapter(Context context, ArrayList<SystemNotification> systemNotifications) {
        this.systemNotifications = systemNotifications;
        this.context = context;
    }

    private Context getContext() {
        return context;
    }

    @Override
    public NotificationItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.item_news, parent, false);

        NotificationItemAdapter.ViewHolder viewHolder = new NotificationItemAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NotificationItemAdapter.ViewHolder viewHolder, int position) {
        SystemNotification systemNotification = systemNotifications.get(position);

        viewHolder.title.setText(systemNotification.title);
        viewHolder.msg.setText(systemNotification.msg);
        setPhoto(viewHolder, systemNotification.photo);
    }

    private void setPhoto(final NotificationItemAdapter.ViewHolder viewHolder, final String filename) {

        if (filename.equals("")) {
            viewHolder.photo.setVisibility(View.GONE);
            return;
        } else {
            viewHolder.photo.setVisibility(View.VISIBLE);
        }

        Glide.with(context)
                .load(fileDownloadURL + "?filename=" + filename)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_broken_image_black_48dp)
                        .centerCrop())
                .into(viewHolder.photo);
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return systemNotifications.size();
    }

    public SystemNotification getItem(int index) {
        return systemNotifications.get(index);
    }

    public void addAll(Collection<SystemNotification> systemNotificationList) {
        systemNotifications.addAll(systemNotificationList);
        notifyDataSetChanged();
    }

    public void add(SystemNotification systemNotification) {
        systemNotifications.add(systemNotification);
        notifyDataSetChanged();
    }

    public void clear() {
        systemNotifications.clear();
        notifyDataSetChanged();
    }

    public void insert(SystemNotification systemNotification, int index) {
        systemNotifications.add(index, systemNotification);
        notifyItemInserted(index);
    }

    public void remove(int index) {
        systemNotifications.remove(index);
        notifyItemRemoved(index);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView msg;
        ImageView photo;
        LinearLayout itemLayout;

        public ViewHolder(View view) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(view);

            title = view.findViewById(R.id.tv_title);
            msg = view.findViewById(R.id.tv_msg);
            photo = view.findViewById(R.id.photo);
            itemLayout = view.findViewById(R.id.layout_item);
        }
    }
}