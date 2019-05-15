package nctu.cs.cgv.itour.object;

public class NotificationType {
    public static final String TYPE_COMMENT_NOTIFICATION = "commentNotification";
    public static final String TYPE_LIKE_NOTIFICATION = "likeNotification";
    public static final String TYPE_SYSTEM_NOTIFICATION = "systemNotification";

    public String type;
    public String key;
    public boolean isChecked;
    public NotificationType(String type, String key) {
        this.type = type;
        this.key = key;
        isChecked = false;
    }
    public void setChecked() {
        isChecked = true;
    }
    public void setNotChecked() {
        isChecked = false;
    }
    public NotificationType(){}
}
