package nctu.cs.cgv.itour.object;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class LikeNotification {


    public String likeUid;
    public String likeUserName;
    public String likedUid;
    public String likedCheckinKey;
    public String likedCheckinDescription;
    public long timestamp;

    public LikeNotification() {
    }

    public LikeNotification(String likeUid,
                                String likeUserName,
                                String likedUid,
                                String likedCheckinKey,
                                String likedCheckinDescription,
                                long timestamp) {
        this.likeUid = likeUid;
        this.likeUserName = likeUserName;
        this.likedUid = likedUid;
        this.likedCheckinKey = likedCheckinKey;
        this.likedCheckinDescription = likedCheckinDescription;
        this.timestamp = timestamp;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("likeUid", likeUid);
        result.put("likeUserName", likeUserName);
        result.put("likedUid", likedUid);
        result.put("likedCheckinKey", likedCheckinKey);
        result.put("likedCheckinDescription", likedCheckinDescription);
        result.put("timestamp", timestamp);
        return result;
    }
}
