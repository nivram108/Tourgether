package nctu.cs.cgv.itour.object;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import static nctu.cs.cgv.itour.MyApplication.mapTag;

public class FirebaseLogManager {

    private void appInteractionLog(String tag, String msg) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final String pushKey = databaseReference.child("app_interaction").child(mapTag).child(uid).push().getKey();
        final String name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        FirebaseLogData firebaseLogData = new FirebaseLogData(uid, tag, msg, name);
        Map<String, Object> logValue = firebaseLogData.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/app_interaction/" + mapTag + "/" + uid + "/" + pushKey, logValue);
        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates,
                new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, final DatabaseReference databaseReference) {
                        //likeMsg.setText("");
                        Log.e("NIVRAM", "ERROR GG");
                    }
                });
    }
}
