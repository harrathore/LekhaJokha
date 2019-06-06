package lekha.stanbuzz.com.lekhajokha;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewGroup extends AppCompatActivity {
    private RecyclerView rvContacts;
    private ImageView imgBack, btnCnf;
    private TextView headTitle;
    private Map<String, String> members;
    private FireStoreDB db;
    private AlertDialog.Builder alert;
    private SessionMang sessionMang;
    private String grpTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);

        getSupportActionBar().hide();

        members = new HashMap<>();
        db = FireStoreDB.getInstance(this);
        sessionMang = SessionMang.getInstance(this);

        rvContacts = findViewById(R.id.contactRecycle);
        imgBack = findViewById(R.id.btnBack);
        headTitle = findViewById(R.id.headTitle);
        btnCnf = findViewById(R.id.btnCnf);
        alert = new AlertDialog.Builder(this);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        btnCnf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.show();
            }
        });

        getAllContacts();
        initDialog();
    }

    private void initDialog() {
        alert.setTitle("Enter Group Title");
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(40, 20, 40, 20);
        final EditText input = new EditText(this);
        input.setLayoutParams(lp);
        input.setGravity(android.view.Gravity.TOP|android.view.Gravity.LEFT);
        input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        input.setLines(1);
        input.setMaxLines(1);
        container.addView(input, lp);
        alert.setView(container);

        alert.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //What ever you want to do with the value
                grpTitle = input.getText().toString();
                if(grpTitle!="") {
                    addGroup(grpTitle);
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
            }
        });
    }

    private void addGroup(String groupTitle) {
        Map<String, Object> newGroup = new HashMap<>();
        newGroup.put("expire", false);
        newGroup.put("session_count", 1);
        newGroup.put("started_on", new Date());
        newGroup.put("title", groupTitle);

        // Adding to self
        members.put(sessionMang.getUserId(), sessionMang.getUserId());
        newGroup.put("member", new ArrayList<String>(members.values()));

        db.getDb().collection(FireStoreDB.col_group).add(newGroup).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    pin("Group created successfully!");
                    createSession(documentReference);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pin("Error writing document : "+e);
                }
            });

    }

    private void createSession(final DocumentReference groupRef) {
        Map<String, Object> newSess = new HashMap<>();
        newSess.put("closed", false);
        newSess.put("started_on", new Date());

        groupRef.collection(FireStoreDB.col_sess).add(newSess).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    pin("Session created!");
                    Toast.makeText(getBaseContext(), "Group created Successfully !", Toast.LENGTH_LONG).show();

                    Intent chat = new Intent(getBaseContext(), Chat.class);
                    chat.putExtra("title", grpTitle);
                    chat.putExtra("gid", groupRef.getId());
                    startActivity(chat);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pin("Error writing document : "+e);
                }
            });
    }

    private void getAllContacts() {
        List<DataHolder.ContactHolder> contactVOList = new ArrayList();
        DataHolder.ContactHolder contactVO;

        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {

                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                if (hasPhoneNumber > 0) {
                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                    contactVO = new DataHolder.ContactHolder();
                    contactVO.setContactName(name);

                    Cursor phoneCursor = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id},
                            null);
                    if (phoneCursor.moveToNext()) {
                        String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        contactVO.setContactNumber(phoneNumber);
                    }

                    phoneCursor.close();

                    Cursor emailCursor = contentResolver.query(
                            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (emailCursor.moveToNext()) {
                        String emailId = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    }
                    contactVOList.add(contactVO);
                }
            }

            DataAdaptor.AllContactsAdapter contactAdapter = new DataAdaptor.AllContactsAdapter(contactVOList, getApplicationContext());
            rvContacts.setLayoutManager(new LinearLayoutManager(this));

            contactAdapter.setMemberChangeListener(new DataAdaptor.AllContactsAdapter.MemberChangeListener() {
                @Override
                public void onMemberChange(String phone, ImageView imageView) {
                    if(members.containsKey(phone)) {
                        members.remove(phone);
                        imageView.setImageResource(R.drawable.ic_user);
                    } else {
                        members.put(phone, phone);
                        imageView.setImageResource(R.drawable.ic_user_active);
                    }

                    if(members.size()>0) {
                        headTitle.setText("Select Members ("+members.size()+")");
                        btnCnf.setVisibility(View.VISIBLE);
                    } else {
                        headTitle.setText("Select Members");
                        btnCnf.setVisibility(View.GONE);
                    }
                }
            });
            rvContacts.setAdapter(contactAdapter);
        }
    }

    private void pin(String m) {
        Log.d("NWGRX", m);
    }
}
