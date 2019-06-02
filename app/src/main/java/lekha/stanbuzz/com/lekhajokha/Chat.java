package lekha.stanbuzz.com.lekhajokha;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.Query;

public class Chat extends AppCompatActivity {
    private ImageView  imgBack, menulist, pay;
    private TextView grpname, sendmsgreply;
    private FireStoreDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        getSupportActionBar().hide();

        db = FireStoreDB.getInstance(this);

        imgBack = findViewById(R.id.btnBack);
        grpname = findViewById(R.id.grpname);
        menulist = findViewById(R.id.menulist);
        pay = findViewById(R.id.imgpay);
        sendmsgreply = findViewById(R.id.sendmsgreply);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        menulist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();                                                     //we have to create menu list here
            }
        });

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        sendmsg.setText(getIntent().getStringExtra("title"));

        init();
    }

    private void init() {
        Query query = db.getDb().collection(FireStoreDB.col_group).document(getIntent().getStringExtra("gid")).collection(FireStoreDB.col_msg).orderBy("date", Query.Direction.ASCENDING);
        RecycleManager recyclerManager = new RecycleManager(this);
        recyclerManager.setChatRecycler(query, R.id.msgRecycle);
    }
}
