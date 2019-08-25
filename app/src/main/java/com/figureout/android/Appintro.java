package com.figureout.android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class Appintro extends AppIntro {

     SessionMang sessionMang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionMang = SessionMang.getInstance(this);
        addSlide(AppIntroFragment.newInstance("Welcome to Figure Out App ", "A simple app for tracking daily expense of a individual or of a group ",
                R.drawable.ic_launcher, ContextCompat.getColor(getApplicationContext(), R.color.orange)));

        addSlide(AppIntroFragment.newInstance("What is Figure out and how to use ?", "Create a group, add money individually and get the report when you want ",
                R.drawable.ic_budget, ContextCompat.getColor(getApplicationContext(), R.color.darkBlue)));

        addSlide(AppIntroFragment.newInstance("Let's Start !", "Hope you will enjoy the app !",
                R.drawable.rocket, ContextCompat.getColor(getApplicationContext(), R.color.btnAliceGreen)));
        setFadeAnimation();

        if(sessionMang.isIntroSeen()) {
            startActivity(new Intent(getApplicationContext(), Home.class));
            finish();
        }
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        startActivity(new Intent(getApplicationContext(), Home.class));
        finish();
        // Do something when users tap on Skip button.
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        startActivity(new Intent(getApplicationContext(), Home.class));
        sessionMang.setIntroComplete();
        finish();
        // Do something when users tap on Done button.
    }
}
