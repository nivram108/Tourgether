package nctu.cs.cgv.itour.object;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class CollectNotification {


    public String collectUid;
    public String collectUserName;
    public String collectedUid;
    public String collectedCheckinKey;
    public String collectedCheckinDescription;
    public long timestamp;

    public CollectNotification() {
    }

    public CollectNotification(String collectUid,
                               String collectUserName,
                               String collectedUid,
                               String collectedCheckinKey,
                               String collectedCheckinDescription,
                               long timestamp) {
        this.collectUid = collectUid;
        this.collectUserName = collectUserName;
        this.collectedUid = collectedUid;
        this.collectedCheckinKey = collectedCheckinKey;
        this.collectedCheckinDescription = collectedCheckinDescription;
        this.timestamp = timestamp;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("collectUid", collectUid);
        result.put("collectUserName", collectUserName);
        result.put("collectedUid", collectedUid);
        result.put("collectedCheckinKey", collectedCheckinKey);
        result.put("collectedCheckinDescription", collectedCheckinDescription);
        result.put("timestamp", timestamp);
        return result;
    }
}

