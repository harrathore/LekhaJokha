package com.figureout.android;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Report extends AppCompatActivity {
    private ImageView img_cancel;
    private TextView total, avg;
    private FireStoreDB db;
    private AlertDialog.Builder builder;
    private DocumentReference sessRef;
    private Button btnEndSess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        img_cancel = findViewById(R.id.img_cancel);
        total = findViewById(R.id.totalamt);
        avg = findViewById(R.id.avgamt);
        btnEndSess = findViewById(R.id.endsession);
        db = FireStoreDB.getInstance(this);

        img_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        btnEndSess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.show();
            }
        });

        getSupportActionBar().hide();
        init();
    }

    private void endSessionHere() {
        final Map<String, Object> endSession = new HashMap<>();
        endSession.put("closed", true);
        endSession.put("ended_on", new Date());

        sessRef.set(endSession, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                showToast("Current Session ended, A new session will Start.");
                btnEndSess.setVisibility(View.GONE);
                Intent home = new Intent(Report.this, Home.class);
                startActivity(home);
            }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                showToast("Unable to end Session");
                pin("Unable to end Session : "+e);
                }
            });
    }

    private void init() {
        db.getDb().collection(FireStoreDB.col_group).document(getIntent().getStringExtra("gid")).collection(FireStoreDB.col_sess).orderBy("started_on", Query.Direction.DESCENDING).limit(1).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    pin("sid failed : "+e);
                    return;
                }
                for (QueryDocumentSnapshot doc : value) {
                    sessRef = db.getDb().collection(FireStoreDB.col_group).document(getIntent().getStringExtra("gid")).collection(FireStoreDB.col_sess).document(doc.getId());
                    sessRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable final DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                            if (snapshot != null && snapshot.exists()) {
                                total.setText("₹ "+snapshot.getData().get("total"));

                                sessRef.collection(FireStoreDB.col_mem).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            try {
                                                Long avgAmt = Math.abs((Long)snapshot.getData().get("total")/task.getResult().size());
                                                avg.setText("₹ "+avgAmt);

                                                Query query = sessRef.collection(FireStoreDB.col_mem).orderBy("invested", Query.Direction.DESCENDING);
                                                RecycleManager recyclerManager = new RecycleManager(Report.this);
                                                recyclerManager.setReportRecycler(query, R.id.memRecycle, avgAmt);
                                            } catch (Exception e) {}
                                        }
                                    }
                                });

                            } else {
                                pin("Current data: null");
                            }
                        }
                    });
                }

            }
        });

        builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Session End");
        builder.setMessage("Please make sure that you all have taken or paid your extra or due money from one another for this session.");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {
                 endSessionHere();
             }
        });
        
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {
             }
        });
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    private void pin(String msg) {
        Log.d("RPTX", msg);
    }
}
