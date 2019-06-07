package nctu.cs.cgv.itour.object;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class GoogleComment {
    public String msg;
    public String rate;
    public String username;

    public GoogleComment() {
    }

    public GoogleComment(String msg, String rate, String username) {
        this.msg = msg;
        this.rate = rate;
        this.username = username;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("msg", msg);
        result.put("rate", rate);
        result.put("username", username);
        return result;
    }
}
