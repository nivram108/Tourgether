package nctu.cs.cgv.itour.object;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class GoogleComment {
    public String username;
    public String rate;
    public String comment;

    public GoogleComment() {
    }

    public GoogleComment(String username, String rate, String comment) {
        this.username = username;
        this.rate = rate;
        this.comment = comment;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("username", username);
        result.put("rate", rate);
        result.put("comment", comment);
        return result;
    }
}
