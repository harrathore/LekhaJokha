package lekha.stanbuzz.com.lekhajokha;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Home extends AppCompatActivity {
    private SessionMang sessionMang;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sessionMang = SessionMang.getInstance(this);
        sessionMang.loginOnlyAllowed();
        getSupportActionBar().hide();   //to hide main heading lekh jokha
    }
}
