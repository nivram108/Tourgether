package nctu.cs.cgv.itour.object;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import static nctu.cs.cgv.itour.MyApplication.mapTag;

public class NotificationType {
    public static final String TYPE_SYSTEM_NOTIFICATION = "systemNotification";
    public static final String TYPE_COMMENT_NOTIFICATION = "commentNotification";
    public static final String TYPE_LIKE_NOTIFICATION = "likeNotification";
    public static final String TYPE_COLLECT_NOTIFICATION = "collectNotification";

    public String type;
    public String key;
    public String pushKey;
    public boolean isChecked;
    public NotificationType(String type, String key, String pushKey) {
        this.type = type;
        this.key = key;
        this.pushKey = pushKey;
        isChecked = false;
    }
    public NotificationType(String type, String key, String pushKey, boolean isChecked) {
        this.type = type;
        this.key = key;
        this.pushKey = pushKey;
        this.isChecked = isChecked;
    }
    public void setChecked() {
        isChecked = true;
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("users").child(uid).child("clicked_notification").child(this.type).child(mapTag).child(pushKey).setValue(true);

    }
    public void setNotChecked() {
        isChecked = false;
    }
    public NotificationType(){}
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("type", type);
        result.put("key", key);
        result.put("pushKey", pushKey);
        result.put("isChecked", isChecked);
        return result;
    }
}
