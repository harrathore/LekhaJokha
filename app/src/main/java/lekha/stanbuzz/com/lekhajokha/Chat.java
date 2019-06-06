package lekha.stanbuzz.com.lekhajokha;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Chat extends AppCompatActivity {
    private ImageView  imgBack, menulist;
    private EditText amtInp, msgInp, noteInp;
    private TextView headTitle;
    private ImageView btnSwitch, btnSendAmt, btnReturn;
    private View amtBox, msgBox;
    private ScrollView scrollVw;
    private FireStoreDB db;
    private Boolean flagSwitch = true;
    private SessionMang sessionMang;
    private String sid = null;
    private DocumentReference grpRef, userRef, sessRef;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        getSupportActionBar().hide();

        db = FireStoreDB.getInstance(this);
        sessionMang = SessionMang.getInstance(this);

        imgBack = findViewById(R.id.btnBack);
        menulist = findViewById(R.id.menulist);
        amtInp = findViewById(R.id.amtInput);
        msgInp = findViewById(R.id.msgInput);
        noteInp = findViewById(R.id.amtNote);
        btnSwitch = findViewById(R.id.btnSwitch);
        btnSendAmt = findViewById(R.id.btnSendAmt);
        amtBox = findViewById(R.id.amtBox);
        msgBox = findViewById(R.id.msgBox);
        headTitle = findViewById(R.id.headTitle);
        scrollVw = findViewById(R.id.scrollVw);
        btnReturn = findViewById(R.id.btnReturn);
        recyclerView = findViewById(R.id.msgRecycle);

        grpRef = db.getDb().collection(FireStoreDB.col_group).document(getIntent().getStringExtra("gid"));
        userRef = db.getDb().collection(FireStoreDB.col_user).document(sessionMang.getUserId());
        headTitle.setText(getIntent().getStringExtra("title"));

        init();
    }

    private void sendMsg() {
        String msg = msgInp.getText().toString();
        pin("Trying send msg...");
        pin(msg);
        if(sid!=null && msg.length()>0 && msg!=" ") {
            Map<String, Object> data = new HashMap<>();

            data.put("msg", msgInp.getText().toString());
            data.put("type", "MSG");
            data.put("date", new Date());
            data.put("name", sessionMang.getUserName());
            data.put("userId", userRef);
            data.put("sid", sessRef);

            grpRef.collection(FireStoreDB.col_msg).document().set(data);
            msgInp.setText("");

            pin("Msg sent");
        }
    }

    private void sendAmt() {
        Long amt = Long.valueOf(amtInp.getText().toString());
        if(sid!=null && amt>0) {
            Map<String, Object> data = new HashMap<>();

            data.put("amt", amt);
            data.put("msg", noteInp.getText().toString());
            data.put("type", "TRANS");
            data.put("date", new Date());
            data.put("name", sessionMang.getUserName());
            data.put("userId", userRef);
            data.put("sid", sessRef);

            grpRef.collection(FireStoreDB.col_msg).document().set(data);
            amtInp.setText("");
            noteInp.setText("");

            pin("Amt sent");
        }

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


        Query query = grpRef.collection(FireStoreDB.col_msg).orderBy("date", Query.Direction.ASCENDING);
        RecycleManager recyclerManager = new RecycleManager(this);
        recyclerManager.setChatRecycler(query, R.id.msgRecycle);

        grpRef.collection(FireStoreDB.col_sess).orderBy("started_on").limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    sid = document.getId();
                    sessRef = grpRef.collection(FireStoreDB.col_sess).document(sid);
                }
            }
            }
        });
    }

    private void pin(String msg) {
        Log.d("CHATX", msg);
    }
}
