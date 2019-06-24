package nctu.cs.cgv.itour.custom;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import nctu.cs.cgv.itour.R;
import nctu.cs.cgv.itour.fragment.NewsFragment;
import nctu.cs.cgv.itour.object.CommentNotification;
import nctu.cs.cgv.itour.object.GlideApp;
import nctu.cs.cgv.itour.object.LikeNotification;
import nctu.cs.cgv.itour.object.NotificationType;
import nctu.cs.cgv.itour.object.SystemNotification;
import static nctu.cs.cgv.itour.activity.MainActivity.systemNotificationIsClickedMap;
import static nctu.cs.cgv.itour.activity.MainActivity.commentNotificationIsClickedMap;
import static nctu.cs.cgv.itour.activity.MainActivity.likeNotificationIsClickedMap;
import static nctu.cs.cgv.itour.MyApplication.fileDownloadURL;
import static nctu.cs.cgv.itour.MyApplication.mapTag;

/**
 * Created by lobZter on 2017/7/10.
 */

public class NewsItemAdapter extends RecyclerView.Adapter<NewsItemAdapter.ViewHolder> {

    private static final int DISPLAY_MSG_LENGTH_MAX = 30;
    private static final int DISPLAY_TITLE_NAME_LENGTH_MAX = 9;
    private static final String TAG = "NewsItemAdapter";
    private ArrayList<NotificationType> notificationTypes;



    private Context context;
    private NewsFragment newsFragment;

    public NewsItemAdapter(Context context, NewsFragment newsFragment) {
        this.notificationTypes = new ArrayList<NotificationType>();
        this.context = context;
        this.newsFragment = newsFragment;
    }

    private Context getContext() {
        return context;
    }

    @Override
    public NewsItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.item_news, parent, false);

        NewsItemAdapter.ViewHolder viewHolder = new NewsItemAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NewsItemAdapter.ViewHolder viewHolder, int position) {
        NotificationType notificationType = notificationTypes.get(position);
//        Log.d("NIVRAM", "Show noti:(" + notificationType.type + ", " + notificationType.key + ")");

        if (notificationType.type == NotificationType.TYPE_SYSTEM_NOTIFICATION) {
            SystemNotification systemNotification = newsFragment.systemNotificationMap.get(notificationType.key);
            viewHolder.title.setText(systemNotification.title);
            viewHolder.msg.setText("");
            if (systemNotification.uid.equals("")) {
                setSpotPhoto(viewHolder, systemNotification.location);
            } else {
                setCheckinPhoto(viewHolder, systemNotification.photo);
            }

        } else if (notificationType.type == NotificationType.TYPE_COMMENT_NOTIFICATION) {
            CommentNotification commentNotification = newsFragment.commentNotificationMap.get(notificationType.key);
            viewHolder.title.setText(getStringWithLength(
                    commentNotification.commentUserName, DISPLAY_TITLE_NAME_LENGTH_MAX) + "回應了你的貼文。");
            viewHolder.msg.setText("");
        setCheckinPhoto(viewHolder, commentNotification.commentedCheckinKey + ".jpg");

        } else if (notificationType.type == NotificationType.TYPE_LIKE_NOTIFICATION) {
            LikeNotification likeNotification = newsFragment.likeNotificationMap.get(notificationType.key);
            viewHolder.title.setText(getStringWithLength(
                    likeNotification.likeUserName, DISPLAY_TITLE_NAME_LENGTH_MAX) + "說你的貼文讚:");

            viewHolder.msg.setText("「" + getStringWithLength(
                    likeNotification.likedCheckinDescription, DISPLAY_MSG_LENGTH_MAX) + "」");
        setCheckinPhoto(viewHolder, likeNotification.likedCheckinKey + ".jpg");
        }

        if (notificationType.isChecked == false) {
            viewHolder.view.setBackgroundResource(R.color.notification_not_checked);
        } else {
            viewHolder.view.setBackgroundResource(R.color.md_white_1000);
//            Log.d("NIVRAM", "SET TO TRUE");

        }

    }
    private void setSpotPhoto(final NewsItemAdapter.ViewHolder viewHolder, final String filename) {
//        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Image").child(filename + ".png");
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("image").child(filename + ".png");
        if (filename.equals("")) {
            viewHolder.photo.setVisibility(View.GONE);
            return;
        }

        GlideApp.with(getContext())
                .load(storageReference)
                .apply(new RequestOptions().placeholder(R.drawable.ic_broken_image_black_48dp))
                .into(viewHolder.photo);
    }

    private void setCheckinPhoto(final NewsItemAdapter.ViewHolder viewHolder, final String filename) {

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
        return notificationTypes.size();
    }

    public NotificationType getItem(int index) {
        return notificationTypes.get(index);
    }

//    public void addAll(Collection<CommentNotification> commentNotificationList) {
//        notificationTypes.addAll(commentNotificationList);
//        notifyDataSetChanged();
//    }

    public void add(String systemNotificationKey, String pushKey) {

        NotificationType notificationType = new NotificationType(NotificationType.TYPE_SYSTEM_NOTIFICATION, systemNotificationKey, pushKey);
        int index = getKeyPosition(notificationType);

        if (index != -1) {
            // contain notification, remove
            notificationTypes.remove(index);
        }
        notificationType.setNotChecked();
        insert(notificationType, 0);
        notifyDataSetChanged();
    }

    public void add(CommentNotification commentNotification, String pushKey) {

        NotificationType notificationType = new NotificationType(NotificationType.TYPE_COMMENT_NOTIFICATION, commentNotification.commentedCheckinKey, pushKey);
        int index = getKeyPosition(notificationType);

        if (index != -1) {
            // contain notification, remove
            notificationTypes.remove(index);
        }

        notificationType.setNotChecked();
        insert(notificationType, 0);
        notifyDataSetChanged();
    }

    public void add(LikeNotification likeNotification, String pushKey) {

        NotificationType notificationType = new NotificationType(NotificationType.TYPE_LIKE_NOTIFICATION, likeNotification.likedCheckinKey, pushKey);
        int index = getKeyPosition(notificationType);

        if (index != -1) {
            // contain notification, remove
            notificationTypes.remove(index);
        }
        notificationType.setNotChecked();
        insert(notificationType, 0);
        notifyDataSetChanged();
    }

    public void clear() {
        notificationTypes.clear();
        notifyDataSetChanged();
    }

    public void insert(NotificationType notificationType, int index) {

        if (notificationType.type == NotificationType.TYPE_SYSTEM_NOTIFICATION) {
            if (systemNotificationIsClickedMap.containsKey(notificationType.pushKey) && systemNotificationIsClickedMap.get(notificationType.pushKey)) notificationType.isChecked = true;
        } else if (notificationType.type == NotificationType.TYPE_COMMENT_NOTIFICATION) {
            if (commentNotificationIsClickedMap.containsKey(notificationType.pushKey) && commentNotificationIsClickedMap.get(notificationType.pushKey)) notificationType.isChecked = true;
        } else if (notificationType.type == NotificationType.TYPE_LIKE_NOTIFICATION) {
            if (likeNotificationIsClickedMap.containsKey(notificationType.pushKey) && likeNotificationIsClickedMap.get(notificationType.pushKey)) {
//                Log.d("NIVRAMMM", "catch!");
                notificationType.isChecked = true;
            }
        }
        notificationTypes.add(index, notificationType);
        notifyDataSetChanged();
        notifyItemInserted(index);
    }

    public void remove(int index) {
        notificationTypes.remove(index);
        notifyItemRemoved(index);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView msg;
        ImageView photo;
        LinearLayout itemLayout;
        View view;

        public ViewHolder(View view) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(view);
            this.view = view;
            title = view.findViewById(R.id.tv_title);
            msg = view.findViewById(R.id.tv_msg);
            photo = view.findViewById(R.id.photo);
            itemLayout = view.findViewById(R.id.layout_item);
        }
    }
    private int getKeyPosition(NotificationType notificationType) {
        for (int i = 0; i < notificationTypes.size(); i++) {
            if (notificationTypes.get(i).key.equals(notificationType.key) &&
                    notificationTypes.get(i).type.equals(notificationType.type))
                return i;
        }
        return -1;
    }


    String getStringWithLength(String s, int length) {

        if (s.getBytes().length <= length) {
            return s;
        }
        int index = 0, byteCount = 0;

        while(byteCount < length) {
//            Log.d("NIVRAM", "LOOP, " + byteCount);
            if(s.substring(index, index + 1).getBytes().length > 1) {
                // is chinese char
                byteCount = byteCount + 2;
                index = index + 1;
            } else {
                byteCount = byteCount + 1;
                index = index + 1;
            }
            if(index == s.length()) break;

        }
        return s.substring(0, index) + "...";
    }

    boolean notificationisChecked(NotificationType notificationType) {

        return false;
    }

    public void setClicked(String notificationType, String pushKey) {

    }

    public void updateIsChecked(String tag, String pushKey) {
        for (NotificationType notificationType: notificationTypes) {
            if (notificationType.type.equals(tag) && notificationType.pushKey.equals(pushKey)) {
                notificationType.isChecked = true;
                notifyDataSetChanged();
                return;
            }
        }
    }
}