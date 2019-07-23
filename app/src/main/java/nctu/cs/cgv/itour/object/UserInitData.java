package nctu.cs.cgv.itour.object;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class UserInitData {


    public String email;
    public String togoList;

    public UserInitData() {
    }

    public UserInitData(String email,
                               String togoList) {
        this.email = email;
        this.togoList = togoList;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("email", email);
        result.put("togoList", togoList);
        return result;
    }
}

