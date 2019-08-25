package com.figureout.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import static android.content.Context.MODE_PRIVATE;

public class SessionMang {
    private static SessionMang single_instance = null;
    public final String USERSESSION="user_session", LOGIN="login", NAME="name", INTRO_SEEN="intro_seen";
    private Context context;
    private Activity activity;
    private SharedPreferences session;
    private Intent loginIntent, homeIntent;
    private SharedPreferences.Editor sessionEditor;
    private FireStoreDB db;

    private SessionMang(Activity a) {
        activity = a;
        context = activity.getBaseContext();
        homeIntent = new Intent(context, Home.class);
        loginIntent = new Intent(context, LoginActivity.class);
        session = context.getSharedPreferences(USERSESSION,MODE_PRIVATE);
        sessionEditor = session.edit();
        db = FireStoreDB.getInstance(activity);
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

            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(loginIntent);
            activity.finish();
        }
    }

    public void LogIn(final String phone) {
        pin("trying LogIn...");
        db.getDb().collection(FireStoreDB.col_user).document(phone).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        pin("Logged");
                        if(!session.contains(LOGIN)) {
                            sessionEditor.putString(LOGIN, phone);
                            sessionEditor.putString(NAME, document.getData().get("name").toString());
                            sessionEditor.commit();
                            pin("login session added");
                        }

                        pin("logged in , redirecting home");
                        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(homeIntent);
                        activity.finish();
                    }
                }
            }
        });
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

    public String getUserName() {
        if(session.contains(LOGIN) && session.contains(NAME)) {
            return session.getString(NAME, "null");
        }
        return "null";
    }

    public Boolean isIntroSeen() {
        return session.contains(INTRO_SEEN);
    }

    public void setIntroComplete() {
        if(!session.contains(INTRO_SEEN)) {
            sessionEditor.putBoolean(INTRO_SEEN, true);
            sessionEditor.commit();
            pin("login session added");
        }
    }

    private void pin(String m) {
        Log.d("SESSX", m);
    }

}