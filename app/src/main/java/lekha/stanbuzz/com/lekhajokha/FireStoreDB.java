package lekha.stanbuzz.com.lekhajokha;

import android.app.Activity;
import android.util.Log;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class FireStoreDB {
    private static FireStoreDB single_instance = null;
    private FirebaseFirestore db;
    private Activity activity;
    public static final String col_user = "users", col_group = "groups", col_msg = "message",
            user_name = "name",
            user_phone = "phone";

    private  FireStoreDB(Activity a) {
        activity = a;
        db = FirebaseFirestore.getInstance();
    }

    public static String PrettyTime(Date past) {
        try {
            Date now = new Date();
            long agoSec = TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime()),
                    agoMin = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime()),
                    agoHour = TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime()),
                    agoDay = TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());

            if(agoSec<60) {
                return agoSec + " sec ago";
            } else if(agoMin<60) {
                return agoMin + " min ago";
            } else if(agoHour<24) {
                return agoHour + " hours ago";
            } else {
                return agoDay + " days ago";
            }
        }
        catch (Exception j){
            j.printStackTrace();
        }
        return "";
    }

    public static FireStoreDB getInstance(Activity a) {
        if (single_instance == null) {
            single_instance = new FireStoreDB(a);
        }
        return single_instance;
    }

    public FirebaseFirestore getDb() {
        return db;
    }

    private static void pin(String m) {
        Log.d("DBX", m);
    }
}