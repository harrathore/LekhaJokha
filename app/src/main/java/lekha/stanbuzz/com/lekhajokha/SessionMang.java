package lekha.stanbuzz.com.lekhajokha;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import static android.content.Context.MODE_PRIVATE;

public class SessionMang {
    private static SessionMang single_instance = null;
    public final String USERSESSION="user_session", LOGIN="login";
    private Context context;
    private Activity activity;
    private SharedPreferences session;
    private Intent loginIntent, homeIntent;
    private SharedPreferences.Editor sessionEditor;

    private SessionMang(Activity a) {
        activity = a;
        context = activity.getBaseContext();
        homeIntent = new Intent(context, Home.class);
        loginIntent = new Intent(context, LoginActivity.class);
        session = context.getSharedPreferences(USERSESSION,MODE_PRIVATE);
        sessionEditor = session.edit();
    }

    public static SessionMang getInstance(Activity a) {
        if (single_instance == null) {
            single_instance = new SessionMang(a);
        }
        return single_instance;
    }

    public void loginOnlyAllowed() {
        if(!session.contains(LOGIN)){
            pin("Login Please");

            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            context.startActivity(loginIntent);
            activity.finish();
        }
    }

    public void LogIn(String phone) {
        pin("Logggg");
        if(!session.contains(LOGIN)) {
            sessionEditor.putString(LOGIN, phone);
            sessionEditor.commit();
            pin("login session added");
        }

        pin("logged in , redirecting home");
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(homeIntent);
        activity.finish();
    }

    public void LogOut() {
        if(session.contains(LOGIN)) {
            sessionEditor.clear().apply();
            pin("Removed Session");
        }
        pin("Logged out");

        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(loginIntent);
        activity.finish();
    }

    public String getUserId() {
        if(session.contains(LOGIN)) {
            return session.getString(LOGIN, "null");
        }
        return "null";
    }

    private void pin(String m) {
        Log.d("SESSX", m);
    }

}