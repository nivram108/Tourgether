package nctu.cs.cgv.itour.object;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

import static nctu.cs.cgv.itour.MyApplication.VERSION_ONLY_GOOGLE_COMMENT;
import static nctu.cs.cgv.itour.MyApplication.spotList;
import static nctu.cs.cgv.itour.activity.MainActivity.getSpotDescription;

/**
 * Created by lobZter on 2017/7/4.
 */

public class Checkin {

    public String lng;
    public String lat;
    public String location;
    public String category;
    public String description;
    public String photo;
    public String uid;
    public String username;
    public long timestamp;
    public Map<String, Boolean> like = new HashMap<>();
    public Map<String, CheckinComment> comment = new HashMap<>();
    // for admin function
    public String targetUid;
    public Map<String, Boolean> popularTargetUid;
    public Boolean fakeFlag;
    public int likeNum;

    public String key;

    public Checkin() {
    }

    public void setSpot(String spotName) {
        SpotNode spotNode = spotList.fullNodeMap.get(spotName);

        this.lng = spotNode.lng;
        this.lat = spotNode.lat;
        this.location = spotName;
        this.description = getSpotDescription(spotName);
        this.category = VERSION_ONLY_GOOGLE_COMMENT;
        this.photo = spotName;
        this.uid = "";
        this.username = spotName;
        this.timestamp = timestamp;
        // for admin function
        this.targetUid = "all";
        this.popularTargetUid = new HashMap<>();
        this.popularTargetUid.put("all", false);
        this.fakeFlag = false;
        this.likeNum = 0;
        this.key = spotName;
    }
    public Checkin(String lat,
                   String lng,
                   String location,
                   String category,
                   String description,
                   String photo,
                   String uid,
                   String username,
                   long timestamp) {

        this.lng = lng;
        this.lat = lat;
        this.location = location;
        this.description = description;
        this.category = category;
        this.photo = photo;
        this.uid = uid;
        this.username = username;
        this.timestamp = timestamp;
        // for admin function
        this.targetUid = "all";
        this.popularTargetUid = new HashMap<>();
        this.popularTargetUid.put("all", false);
        this.fakeFlag = false;
        this.likeNum = 0;
    }

    public Checkin(Checkin checkin) {
        this.lng = checkin.lng;
        this.lat = checkin.lat;
        this.location = checkin.location;
        this.description = checkin.description;
        this.category = checkin.category;
        this.photo = checkin.photo;
        this.uid = checkin.uid;
        this.username = checkin.username;
        this.timestamp = checkin.timestamp;
        this.key = checkin.key;
        this.targetUid = "all";
        this.popularTargetUid = new HashMap<>();
        this.popularTargetUid.put("all",false);
        this.fakeFlag = false;
        this.likeNum = 0;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("lng", lng);
        result.put("lat", lat);
        result.put("location", location);
        result.put("description", description);
        result.put("category", category);
        result.put("photo", photo);
        result.put("rate", uid);
        result.put("username", username);
        result.put("timestamp", timestamp);
        result.put("targetUid", targetUid);
        result.put("popularTargetUid", popularTargetUid);
        result.put("fakeFlag", fakeFlag);
        result.put("likeNum", likeNum);
        return result;
    }
}
