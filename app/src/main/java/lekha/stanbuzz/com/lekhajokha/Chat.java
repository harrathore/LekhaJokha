package lekha.stanbuzz.com.lekhajokha;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.content.res.AppCompatResources;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import com.google.firebase.firestore.Query;

public class Chat extends AppCompatActivity {
    private ImageView  imgBack, menulist;
    private EditText amtInp, msgInp;
    private ImageView btnSwitch, btnSendAmt, btnReturn;
    private View amtBox, msgBox;
    private FireStoreDB db;
    private Boolean flagSwitch = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        getSupportActionBar().hide();

        db = FireStoreDB.getInstance(this);

        imgBack = findViewById(R.id.btnBack);
        menulist = findViewById(R.id.menulist);
        amtInp = findViewById(R.id.amtInput);
        msgInp = findViewById(R.id.msgInput);
        btnSwitch = findViewById(R.id.btnSwitch);
        btnSendAmt = findViewById(R.id.btnSendAmt);
        amtBox = findViewById(R.id.amtBox);
        msgBox = findViewById(R.id.msgBox);
        btnReturn = findViewById(R.id.btnReturn);

        init();
    }

    private void sendMsg() {

    }

    private void sendAmt() {

    }

    private void init() {
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        menulist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flagSwitch) {
                    msgBox.setVisibility(View.GONE);
                    amtBox.setVisibility(View.VISIBLE);
                } else {
                    sendMsg();
                }
            }
        });

        btnSendAmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendAmt();
            }
        });

        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amtBox.setVisibility(View.GONE);
                msgBox.setVisibility(View.VISIBLE);
            }
        });

        msgInp.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>0) {
                    flagSwitch = false;
                    btnSwitch.setImageDrawable(AppCompatResources.getDrawable(getBaseContext(), R.drawable.ic_send));
                } else {
                    flagSwitch = true;
                    btnSwitch.setImageDrawable(AppCompatResources.getDrawable(getBaseContext(), R.drawable.ic_rupees));
                }
            }
        });


        Query query = db.getDb().collection(FireStoreDB.col_group).document(getIntent().getStringExtra("gid")).collection(FireStoreDB.col_msg).orderBy("date", Query.Direction.ASCENDING);
        RecycleManager recyclerManager = new RecycleManager(this);
        recyclerManager.setChatRecycler(query, R.id.msgRecycle);
    }
}
