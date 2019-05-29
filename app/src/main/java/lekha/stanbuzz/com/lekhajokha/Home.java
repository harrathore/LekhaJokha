package lekha.stanbuzz.com.lekhajokha;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.firestore.Query;

public class Home extends AppCompatActivity {
    private SessionMang sessionMang;
    private FireStoreDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        db = FireStoreDB.getInstance(this);

        sessionMang = SessionMang.getInstance(this);
        sessionMang.loginOnlyAllowed();
        getSupportActionBar().hide();   //to hide main heading lekh jokha

        Query query = db.getDb().collection(FireStoreDB.col_group);
        RecycleManager recyclerManager = new RecycleManager(this);
        recyclerManager.setGroupRecycler(query, R.id.groupRecycle);
    }
}