package nctu.cs.cgv.itour.object;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class TogoPlannedData {
    public String locationName;
    public boolean isVisited;
    public TogoPlannedData(String locationName,
                           boolean isVisited) {
        this.locationName = locationName;
        this.isVisited = isVisited;
    }
    public TogoPlannedData(){};
    public TogoPlannedData(String locationName) {
        this.locationName = locationName;
        this.isVisited = false;
    }
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("locationName", locationName);
        result.put("isVisited", isVisited);
        return result;
    }
}
