package nctu.cs.cgv.itour.object;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class SpotData {
    public String category;
    public int id;
    public float lat;
    public float lng;
    public int order;
    public long timestamp;

    public SpotData() {
    }

    public SpotData(String category,
                            int id,
                            float lat,
                            float lng,
                            int order,
                            long timestamp) {
        this.category = category;
        this.id = id;
        this.lat = lat;
        this.lng = lng;
        this.order = order;
        this.timestamp = timestamp;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("category", category);
        result.put("id", id);
        result.put("lat", lat);
        result.put("lng", lng);
        result.put("order", order);
        result.put("timestamp", timestamp);
        return result;
    }
}
