package lekha.stanbuzz.com.lekhajokha;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.firestore.Query;

public class Home extends AppCompatActivity {
    private SessionMang sessionMang;
    private FireStoreDB db;
    private View btnNewGrp;
    private String currentUserName;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        db = FireStoreDB.getInstance(this);

        btnNewGrp = findViewById(R.id.btnNewGrp);

        sessionMang = SessionMang.getInstance(this);

        sessionMang.loginOnlyAllowed();

        getSupportActionBar().hide();   //to hide main heading lekh jokha

        btnNewGrp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check the SDK version and whether the permission is already granted or not.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 1);
                    //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
                } else {
                    Intent newGroup = new Intent(Home.this, NewGroup.class);
                    startActivity(newGroup);
                }
            }
        });

        Query query = db.getDb().collection(FireStoreDB.col_group);
        RecycleManager recyclerManager = new RecycleManager(this);
        recyclerManager.setGroupRecycler(query, R.id.groupRecycle);
    }
}