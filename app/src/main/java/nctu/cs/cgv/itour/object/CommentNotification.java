package nctu.cs.cgv.itour.object;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lobZter on 2017/12/25.
 */

public class CommentNotification {


    public String commentUid;
    public String commentUserName;
    public String commentedUid;
    public String commentedCheckinKey;
    public String commentedCheckinDescription;
    public long timestamp;

    public CommentNotification() {
    }

    public CommentNotification(String commentUid,
                        String commentUserName,
                        String commentedUid,
                        String commentedCheckinKey,
                        String commentedCheckinDescription,
                        long timestamp) {
        this.commentUid = commentUid;
        this.commentUserName = commentUserName;
        this.commentedUid = commentedUid;
        this.commentedCheckinKey = commentedCheckinKey;
        this.commentedCheckinDescription = commentedCheckinDescription;
        this.timestamp = timestamp;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("commentUid", commentUid);
        result.put("commentUserName", commentUserName);
        result.put("commentedUid", commentedUid);
        result.put("commentedCheckinKey", commentedCheckinKey);
        result.put("commentedCheckinDescription", commentedCheckinDescription);
        result.put("timestamp", timestamp);
        return result;
    }
}
