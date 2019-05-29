package lekha.stanbuzz.com.lekhajokha;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class LoginActivity extends AppCompatActivity {
    private final String TAG = "lgnx";
    private EditText userPhoneView, otpCodeView, fullNameView;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private Button btnNext, btnSubmit;
    private Boolean isRegistered = true;
    private FirebaseAuth mAuth;
    private Boolean nxtFlag = false, submitFlag = false;
    private FireStoreDB db;
    private SessionMang sessionMang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //
        userPhoneView = findViewById(R.id.userphone);
        btnNext = findViewById(R.id.btnnext);
        otpCodeView = findViewById(R.id.otp);
        fullNameView = findViewById(R.id.name);
        btnSubmit = findViewById(R.id.verifybtn);
        mAuth = FirebaseAuth.getInstance();

        db = FireStoreDB.getInstance(this);
        sessionMang = SessionMang.getInstance(this);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               userPhoneView.setText("8516876554");
                if(!nxtFlag) {
                    PhoneAuthProvider.getInstance().verifyPhoneNumber("+91"+ userPhoneView.getText().toString(), 60, TimeUnit.SECONDS, LoginActivity.this, mCallbacks);
                    nxtFlag = true;
                    checkRegistration();
                }
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               otpCodeView.setText("123456");
                if (!submitFlag) {
                    if(!isRegistered && fullNameView.getText().length()<3) {
                        showToast("Enter valid Name !");
                        return;
                    }
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otpCodeView.getText().toString());
                    verifyUserOtp(credential);
                    submitFlag = true;
                }
            }
        });

        getSupportActionBar().hide();   //to hide main heading lekh jokha

    }


    // on clicking the submit in our project
    private void verifyUserOtp(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in or verify success, update UI with the signed-in user's information
                            pin("signInWithCredential:success");
//                            FirebaseUser user = task.getResult().getUser();

                            if(isRegistered) {
                                sessionMang.LogIn(userPhoneView.getText().toString());
                            } else {
                                saveAndLogin();
                            }

                        } else {
                            // Sign in failed, display a message and update the UI
                            pin("signInWithCredential:failure : "+task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                showToast("Invalid OTP");
                            }
                            submitFlag = false;
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
                    } else {
                        pin("Error getting documents: "+task.getException());
                    }
                }
            });
    }



    // verifiction in our project is next
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and make verification without
            //     user action.
            pin("onVerificationCompleted:" + credential);

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.d(TAG, "onVerificationFailed", e);

            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                showToast("Invalid Phone No.");
            } else if (e instanceof FirebaseTooManyRequestsException) {
                showToast("Something went wrong!");
            }
            nxtFlag = false;
        }




        @Override
        public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
            userPhoneView.setVisibility(View.GONE);
            otpCodeView.setVisibility(View.VISIBLE);
            btnNext.setVisibility(View.GONE);
            btnSubmit.setVisibility(View.VISIBLE);

            pin("onCodeSent:" + verificationId);
            mVerificationId = verificationId;
            mResendToken = token;
        }
    };

    private static void pin(String m) {
        Log.d("LGNX", m);
    }

    private void showToast(String msg){
        pin(msg);
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

}
