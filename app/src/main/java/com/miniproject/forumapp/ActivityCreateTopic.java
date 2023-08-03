package com.miniproject.forumapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ActivityCreateTopic extends AppCompatActivity {

    CoordinatorLayout coordinatorLayout;

    MaterialAutoCompleteTextView mAutoTextV/*,mVisibility*/;
    TextInputEditText mTopicname,mDesp;
    TextInputLayout mTxtTopicname,mTxtDesp,mTxtAuto/*,mTxtVisibility*/;
    AppCompatButton mCreateBtn;

    String userStr,userID,privateStr,publicStr;

    FirebaseFirestore fStore;
    FirebaseAuth fAuth;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_topic);

        initiateWithID();

        initiateFirebaseClasses();

        getUserData();

        addTextChangeListner();

        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createTopic();
            }
        });



    }

    private void initiateWithID() {

        coordinatorLayout = findViewById(R.id.activity_create_topic);

        mTopicname = findViewById(R.id.create_topic_name);
        mDesp = findViewById(R.id.create_topic_desp);
        mAutoTextV = findViewById(R.id.create_topic_autoTextV);

        mTxtTopicname = findViewById(R.id.create_topic_txt_name);
        mTxtDesp = findViewById(R.id.create_topic_txt_desp);
        mTxtAuto = findViewById(R.id.create_topic_Txt_autoText);


        mCreateBtn = findViewById(R.id.create_topic_create);

        MaterialToolbar toolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(),
                R.layout.item_spinner,
                getResources().getStringArray(R.array.categoriesArray));
        ArrayAdapter visibilityAdapter = new ArrayAdapter(getApplicationContext(),
                R.layout.item_spinner,
                getResources().getStringArray(R.array.visibiltyArray));

        mAutoTextV.setAdapter(arrayAdapter);
        mAutoTextV.setSelection(0);

        publicStr = "Public: Anyone can see what you or other followers post on this forum, including questions or answers or Topics followers.";
        privateStr = "Private: Only the followers you approve can see what you or other followers post on this forum, including questions or answers or Topics followers.";

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    private void initiateFirebaseClasses() {
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
    }


    private void getUserData() {

        userID = fAuth.getUid();

        fStore.collection(Keys.USER_COLLECTION)
                .document(userID)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        userStr = value.getString(Keys.USERNAME);
                    }
                });

    }

    private void createTopic() {
        String topicStr,despStr,categoryStr;
        topicStr = mTopicname.getText().toString().trim();
        despStr = mDesp.getText().toString().trim();
        categoryStr = mAutoTextV.getText().toString().trim();

        if (topicStr.equals("")){
            mTxtTopicname.setError("This Field Cannot Be Empty");
            return;
        }
        if (despStr.equals("")){
            mTxtDesp.setError("This Field Cannot Be Empty");
            return;
        }
        if (categoryStr.equals("")){
            mTxtAuto.setError("This Field Cannot Be Empty");
            return;
        }

        progressDialog = new ProgressDialog(ActivityCreateTopic.this, R.style.Base_Theme_AppCompat_Light_Dialog_Alert);
        progressDialog.setTitle("Creating Topic");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Map<String,Object> topicdata = new HashMap<>();
        topicdata.put(Keys.TOPICNAME,topicStr);
        topicdata.put(Keys.TOPICDESP,despStr);
        topicdata.put(Keys.TOPICCATEGORY,categoryStr);
        topicdata.put(Keys.TOPICCREATEDBYID,userID);
        topicdata.put(Keys.TOPICCREATEDBY,userStr);

        topicdata.put(Keys.TOPIC_CREATEDON, new Timestamp(new Date()));
        topicdata.put(Keys.REQUESTEDUSERS,new HashMap<String,Object>());
        topicdata.put(Keys.QUESTION,new HashMap<String,Object>());

        Map<String,Object> joinedusers = new HashMap<>();
        joinedusers.put(userID,userStr);
        topicdata.put(Keys.JOINEDUSERS,joinedusers);

        fStore.collection(Keys.TOPIC_COLLECTION)
                .add(topicdata)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            addDatatoUser(topicStr,task.getResult().getId());
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });
    }

    private void addDatatoUser(String topicStr,String topicUID){

        Map<String,Object> map = new HashMap<>();
        map.put(Keys.JOINEDFORUMS+"."+topicUID,topicStr);
        map.put(Keys.CREATEDFORUMS+"."+topicUID,topicStr);
        
        fStore.collection(Keys.USER_COLLECTION)
                .document(userID)
                .update(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),"User Data Added", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else
                            Toast.makeText(getApplicationContext(),task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                
    }

    private void addTextChangeListner() {
        mTopicname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mTopicname.getText().toString().trim().length()>0){
                    mTxtTopicname.setError(null);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mDesp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mDesp.getText().toString().trim().length()>0){
                    mTxtDesp.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}