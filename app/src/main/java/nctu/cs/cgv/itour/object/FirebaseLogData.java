package nctu.cs.cgv.itour.object;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class FirebaseLogData {

    public static final String LOG_CHECKIN_ADD 	= "Checkin Add";
    public static final String LOG_CHECKIN_OPEN 	= "Checkin Open";
    public static final String LOG_CHECKIN_CLOSE 	= "Checkin Close";
    public static final String LOG_CHECKIN_COLLECT_ADD	= "Checkin Collect Add";
    public static final String LOG_CHECKIN_COLLECT_REMOVE = "Checkin Collect Remove";
    public static final String LOG_CHECKIN_LIKE 	= "Checkin Like";
    public static final String LOG_CHECKIN_UNLIKE 	= "Checkin Unlike";
    public static final String LOG_CHECKIN_COMMENT	= "Checkin Comment";
    public static final String LOG_CHECKIN_LOCATE 	= "Checkin Locate";
    public static final String LOG_CHECKIN_NAVIGATE = "Checkin Navigate";

    public static final String LOG_TOGO_OPEN 		= "Togo Open";
    public static final String LOG_TOGO_CLOSE 		= "Togo Close";
    public static final String LOG_TOGO_ADD 		= "Togo Add";
    public static final String LOG_TOGO_REMOVE 		= "Togo Remove";
    public static final String LOG_TOGO_LOCATE 	= "Togo Locate";
    public static final String LOG_TOGO_NAVIGATE 	= "Togo Navigate";
    public static final String LOG_REPORT_CHECKIN 	= "Report Checkin";
    public static final String LOG_REPORT_TOGO		= "Report Togo";
    public static final String LOG_REPORT_ANYWHERE 	= "Report Anywhere";
    public static final String LOG_SUMMARY			= "Summary";
    public static final String LOG_NEWS_CLICKED_HOT_CHECKIN = "News Clicked Hot Checkin";
    public static final String LOG_NEWS_CLICKED_HOT_SPOT	= "News Clicked Hot Spot";
    public static final String LOG_NEWS_CLICKED_LIKE 		= "News Clicked Like";
    public static final String LOG_NEWS_CLICKED_COMMENT		= "News Clicked Comment";
    public static final String LOG_NEWS_CLICKED_COLLECT		= "News Clicked Collect";
    public static final String LOG_NOTIFICATION_CLICKED_HOT_CHECKIN = "Notification Clicked Hot Checkin";
    public static final String LOG_NOTIFICATION_CLICKED_HOT_SPOT	= "Notification Clicked Hot Spot";
    public static final String LOG_NOTIFICATION_CLICKED_LIKE 		= "Notification Clicked Like";
    public static final String LOG_NOTIFICATION_CLICKED_COMMENT		= "Notification Clicked Comment";
    public static final String LOG_NOTIFICATION_REMOVE_HOT_CHECKIN 	= "Notification Remove Hot Checkin";
    public static final String LOG_NOTIFICATION_REMOVE_HOT_SPOT		= "Notification Remove Hot Spot";
    public static final String LOG_NOTIFICATION_REMOVE_LIKE 		= "Notification Remove Like";
    public static final String LOG_NOTIFICATION_REMOVE_COMMENT		= "Notification Remove Comment";
    public static final String LOG_SEARCH_LOCATION = "Search Location";

    public static final String LOG_NOTE_IS_SELF_CHECKIN = " Is Self Checkin";
    public static final String LOG_NOTE_IS_NOT_SELF_CHECKIN = " Is Not Self Checkin";
    public static final String LOG_NOTE_IS_COLLECTED_CHECKIN = " Is Collected Checkin";
    public static final String LOG_NOTE_IS_OTHER_CHECKIN = " Is Other Checkin";

    public static final String LOG_NOTE_IS_COLLECTED_TOGO = " Is Collected Togo";
    public static final String LOG_NOTE_IS_NOT_COLLECTED_TOGO = " Is Not Collected Togo";

    public static final String LOG_NOTIFICATION_HOT_CHECKIN 	= "Notification Hot Checkin";
    public static final String LOG_NOTIFICATION_HOT_SPOT		= "Notification Hot Spot";
    public static final String LOG_NOTIFICATION_LIKE 		= "Notification Like";
    public static final String LOG_NOTIFICATION_COMMENT		= "Notification Comment";


    public String uid;
    public String tag;
    public String msg;
    public String note;
    public String name;
    public String lat;
    public String lng;
    public long timestamp;

    public FirebaseLogData() {
    }

    public FirebaseLogData(String uid,
                           String tag,
                           String msg,
                           String note,
                           String name,
                           String lat,
                           String lng) {
        this.uid = uid;
        this.tag = tag;
        this.msg = msg;
        this.note = note;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.timestamp = System.currentTimeMillis() / 1000;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("tag", tag);
        result.put("msg", msg);
        result.put("note", note);
        result.put("name", name);
        result.put("lat", lat);
        result.put("lng", lng);
        result.put("timestamp", timestamp);
        return result;
    }
}