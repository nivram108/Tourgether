package nctu.cs.cgv.itour.object;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class FirebaseLogData {

    public String uid;
    public String tag;
    public String msg;
    public String name;
    public long timestamp;

    public FirebaseLogData() {
    }

    public FirebaseLogData(String uid,
                           String tag,
                           String msg,
                           String name,
                           long timestamp) {
        this.uid = uid;
        this.tag = tag;
        this.msg = msg;
        this.name = name;
        this.timestamp = timestamp;
    }
    public FirebaseLogData(String uid,
                           String tag,
                           String msg,
                           String name) {
        this.uid = uid;
        this.tag = tag;
        this.msg = msg;
        this.name = name;
        this.timestamp = System.currentTimeMillis() / 1000;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("tag", tag);
        result.put("msg", msg);
        result.put("name", name);
        result.put("timestamp", timestamp);
        return result;
    }
}