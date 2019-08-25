package com.figureout.android;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.msg91.sendotp.library.SendOtpVerification;
import com.msg91.sendotp.library.Verification;
import com.msg91.sendotp.library.VerificationListener;

public class OtpVerification implements VerificationListener {
    private Activity activity;
    private ProgressBar progressBar;
    private Verification mVerification;

    OtpVerification(Activity a, ProgressBar p) {
        activity = a;
        progressBar = p;
    }

    void startOtpVerf(String phone) {
        if(phone.length()!=10) {
            makeToast("Invalid Phone No.");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mVerification = SendOtpVerification.createSmsVerification(SendOtpVerification
                .config("+91"+ phone)
                .context(activity)
                .senderId("FIGOUT")
                .message("Welcome to FigureOut, your verification OTP is ##OTP##")
                .httpsConnection(false)
                .autoVerification(true)
                .build(), OtpVerification.this);

        mVerification.initiate();
    }

    public void resendOTP() {
        progressBar.setVisibility(View.VISIBLE);
        mVerification.resend("text");
    }

    public void verifyOTP(String otp) {
        progressBar.setVisibility(View.VISIBLE);
        mVerification.verify(otp);
    }

    @Override
    public void onInitiated(String response) {
        //OTP successfully resent/sent.
        pin("Initialized!" + response);
        makeToast("OTP Sent !");
    }

    @Override
    public void onInitiationFailed(Exception paramException) {
        pin("Verification initialization failed: ");
        pin(paramException.toString());
        //sending otp failed.
        makeToast("Couldn't Send OTP !");
    }

    @Override
    public void onVerified(String response) {
        //OTP verified successfully.
        pin("Verified!\n" + response);
        makeToast("Phone verified");
    }

    @Override
    public void onVerificationFailed(Exception paramException) {
        pin("Verification failed: "+paramException.toString());
        makeToast("OTP Failed, Try Again !");
    }

    private void makeToast(String msg) {
        progressBar.setVisibility(View.INVISIBLE);
        Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
    }

    private void pin(String m) {
        Log.d("OTPX", m);
    }
}