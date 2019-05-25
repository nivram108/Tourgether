package nctu.cs.cgv.itour.object;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class TogoCollectedData {
    public String collectedCheckinKey;
    public boolean isVisited;
    public TogoCollectedData(String collectedCheckinKey,
                           boolean isVisited) {
        this.collectedCheckinKey = collectedCheckinKey;
        this.isVisited = isVisited;
    }
    public TogoCollectedData(){};
    public TogoCollectedData(String collectedCheckinKey) {
        this.collectedCheckinKey = collectedCheckinKey;
        this.isVisited = false;
    }
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("collectedCheckinKey", collectedCheckinKey);
        result.put("isVisited", isVisited);
        return result;
    }
}
