package nctu.cs.cgv.itour.object;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class FirebaseLogData {

    public static final String LOG_APP_INTERACTION_CHECKIN_ADD 	= "App Interaction Checkin Add";
    public static final String LOG_APP_INTERACTION_CHECKIN_OPEN 	= "App Interaction Checkin Open";
    public static final String LOG_APP_INTERACTION_CHECKIN_COLLECT_ADD	= "App Interaction Checkin Collect Add";
    public static final String LOG_APP_INTERACTION_CHECKIN_COLLECT_REMOVE = "App Interaction Checkin Collect Remove";
    public static final String LOG_APP_INTERACTION_CHECKIN_LIKE 	= "App Interaction Checkin Like";
    public static final String LOG_APP_INTERACTION_CHECKIN_COMMENT	= "App Interaction Checkin Comment";
    public static final String LOG_APP_INTERACTION_CHECKIN_LOCATE 	= "App Interaction Checkin Locate";
    public static final String LOG_APP_INTERACTION_CHECKIN_NAVIGATE = "App Interaction Checkin Navigate";
    public static final String LOG_APP_INTERACTION_TOGO_OPEN 		= "App Interaction Togo Open";
    public static final String LOG_APP_INTERACTION_TOGO_ADD 		= "App Interaction Togo Add";
    public static final String LOG_APP_INTERACTION_TOGO_REMOVE 		= "App Interaction Togo Remove";
    public static final String LOG_APP_INTERACTION_TOGO_NAVIGATE 	= "App Interaction Togo Navigate";
    public static final String LOG_APP_INTERACTION_REPORT_CHECKIN 	= "App Interaction Report Checkin";
    public static final String LOG_APP_INTERACTION_REPORT_TOGO		= "App Interaction Report Togo";
    public static final String LOG_APP_INTERACTION_REPORT_ANYWHERE 	= "App Interaction Report Anywhere";
    public static final String LOG_APP_INTERACTION_SUMMARY			= "App Interaction Summary";
    public static final String LOG_APP_INTERACTION_NEWS_HOT_CHECKIN = "App Interaction News Hot Checkin";
    public static final String LOG_APP_INTERACTION_NEWS_HOT_SPOT	= "App Interaction News Hot Spot";
    public static final String LOG_APP_INTERACTION_NEWS_LIKE 		= "App Interaction News Like";
    public static final String LOG_APP_INTERACTION_NEWS_COMMENT		= "App Interaction News Comment";
    public static final String LOG_NOTIFICATION_CLICKED_HOT_CHECKIN = "Notification Clicked Hot Checkin";
    public static final String LOG_NOTIFICATION_CLICKED_HOT_SPOT	= "Notification Clicked Hot Spot";
    public static final String LOG_NOTIFICATION_CLICKED_LIKE 		= "Notification Clicked Like";
    public static final String LOG_NOTIFICATION_CLICKED_COMMENT		= "Notification Clicked Comment";
    public static final String LOG_NOTIFICATION_REMOVE_HOT_CHECKIN 	= "Notification Remove Hot Checkin";
    public static final String LOG_NOTIFICATION_REMOVE_HOT_SPOT		= "Notification Remove Hot Spot";
    public static final String LOG_NOTIFICATION_REMOVE_LIKE 		= "Notification Remove Like";
    public static final String LOG_NOTIFICATION_REMOVE_COMMENT		= "Notification Remove Comment";

    public static final String LOG_NOTE_IS_SELF_CHECKIN = " Is Self Checkin";
    public static final String LOG_NOTE_IS_NOT_SELF_CHECKIN = " Is Not Self Checkin";
    public static final String LOG_NOTE_IS_COLLECTED_CHECKIN = " Is Collected Checkin";
    public static final String LOG_NOTE_IS_OTHER_CHECKIN = " Is Other Checkin";


    public String uid;
    public String tag;
    public String msg;
    public String note;
    public String name;
    public long timestamp;

    public FirebaseLogData() {
    }

    public FirebaseLogData(String uid,
                           String tag,
                           String msg,
                           String note,
                           String name,
                           long timestamp) {
        this.uid = uid;
        this.tag = tag;
        this.msg = msg;
        this.note = note;
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
        this.note = "";
        this.name = name;
        this.timestamp = System.currentTimeMillis() / 1000;
    }

    public FirebaseLogData(String uid,
                           String tag,
                           String msg,
                           String note,
                           String name) {
        this.uid = uid;
        this.tag = tag;
        this.msg = msg;
        this.note = note;
        this.name = name;
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
        result.put("timestamp", timestamp);
        return result;
    }
}