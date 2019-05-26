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
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    private final String TAG = "lgnx";
    private EditText userphone, otpcode, fullname;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private Button buttonnext, buttonverify;
    private FirebaseAuth mAuth;
    private Boolean nxtFlag = false, verifyFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //
        userphone = findViewById(R.id.userphone);
        buttonnext = findViewById(R.id.btnnext);
        otpcode = findViewById(R.id.otp);
        fullname = findViewById(R.id.name);
        buttonverify = findViewById(R.id.verifybtn);
        mAuth = FirebaseAuth.getInstance();

        buttonnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!nxtFlag) {
                    PhoneAuthProvider.getInstance().verifyPhoneNumber("+91"+userphone.getText().toString(), 60, TimeUnit.SECONDS, LoginActivity.this, mCallbacks);
                    nxtFlag = true;
                }
            }
        });

        buttonverify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!verifyFlag) {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otpcode.getText().toString());
                    signInWithPhoneAuthCredential(credential);
                    verifyFlag = true;
                }
            }
        });

        getSupportActionBar().hide();   //to hide main heading lekh jokha

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();

                            // to move into next page
                            Intent home = new Intent(LoginActivity.this, Home.class);
                            startActivity(home);

                        } else
                            {
                            // Sign in failed, display a message and update the UI
                            Log.d(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                showToast("Invalid OTP");
                            }
                            verifyFlag = false;
                        }
                    }
                });
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            Log.d(TAG, "onVerificationCompleted:" + credential);

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
            userphone.setVisibility(View.GONE);
            otpcode.setVisibility(View.VISIBLE);
            fullname.setVisibility(View.VISIBLE);
            buttonnext.setVisibility(View.GONE);
            buttonverify.setVisibility(View.VISIBLE);

            Log.d(TAG, "onCodeSent:" + verificationId);

            mVerificationId = verificationId;
            mResendToken = token;
        }
    };

    private void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

}
