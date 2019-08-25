package com.figureout.android;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private final String TAG = "lgnx";
    private EditText userPhoneView, otpCodeView, fullNameView;
    private Button btnNext, btnSubmit;
    private Boolean isRegistered = true, autoVerify = false;
    private MyOtpVerfication myOtpVerfication;
    private ProgressBar progressBar;
    private Boolean nxtFlag = false, submitFlag = false;
    private FireStoreDB db;
    private SessionMang sessionMang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressBar = FireStoreDB.getProgressBar((ProgressBar) findViewById(R.id.spin_kit));
        myOtpVerfication = new MyOtpVerfication(this, progressBar);

        userPhoneView = findViewById(R.id.userphone);
        btnNext = findViewById(R.id.btnnext);
        otpCodeView = findViewById(R.id.otp);
        fullNameView = findViewById(R.id.name);
        btnSubmit = findViewById(R.id.verifybtn);

        db = FireStoreDB.getInstance(this);
        sessionMang = SessionMang.getInstance(this);

        getSupportActionBar().hide();   //to hide main heading lekh jokha

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!nxtFlag) {
                    nxtFlag = true;
                    checkRegistration();
                }
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!submitFlag) {
                    if (!isRegistered && fullNameView.getText().length() < 3) {
                        showToast("Enter valid Name !");
                        return;
                    }
                    myOtpVerfication.verifyOTP(otpCodeView.getText().toString());
                    submitFlag = true;
                }
            }
        });

    }

    private void saveAndLogin() {
        Map<String, Object> newUser = new HashMap<>();
        newUser.put(FireStoreDB.user_name, fullNameView.getText().toString());
        newUser.put(FireStoreDB.user_phone, userPhoneView.getText().toString());

        db.getDb().collection(FireStoreDB.col_user).document(userPhoneView.getText().toString())
                .set(newUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        sessionMang.LogIn(userPhoneView.getText().toString());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast("Unable to Create account");
                        pin("Error writing document : "+e);
                    }
                });
    }

    private void checkRegistration() {
        db.getDb().collection(FireStoreDB.col_user).whereEqualTo(FireStoreDB.user_phone, userPhoneView.getText().toString()).get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        if(task.getResult().isEmpty()) {
                            isRegistered = false;
                            fullNameView.setVisibility(View.VISIBLE);
                        }
                        myOtpVerfication.startOtpVerf(userPhoneView.getText().toString());
                    } else {
                        pin("Error getting documents: "+task.getException());
                    }
                }
            });
    }

    class MyOtpVerfication extends OtpVerification {

        MyOtpVerfication(Activity a, ProgressBar p) {
            super(a, p);
        }

        @Override
        public void onInitiated(String response) {
            super.onInitiated(response);
            btnNext.setVisibility(View.GONE);
            userPhoneView.setVisibility(View.GONE);
            btnSubmit.setVisibility(View.VISIBLE);
            otpCodeView.setVisibility(View.VISIBLE);
            otpCodeView.requestFocus();
        }

        @Override
        public void onVerificationFailed(Exception paramException) {
            super.onVerificationFailed(paramException);
            otpCodeView.setText("");
            otpCodeView.requestFocus();
        }

        @Override
        public void onVerified(String response) {
            super.onVerified(response);

            if(isRegistered) {
                sessionMang.LogIn(userPhoneView.getText().toString());
            } else {
                saveAndLogin();
            }
        }
    }

    private static void pin(String m) {
        Log.d("LGNX", m);
    }

    private void showToast(String msg){
        pin(msg);
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}
