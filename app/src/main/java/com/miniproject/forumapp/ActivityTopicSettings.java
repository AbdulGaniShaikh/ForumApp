package com.miniproject.forumapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
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

public class ActivityTopicSettings extends AppCompatActivity {

    MaterialAutoCompleteTextView mAutoTextV;
    TextInputEditText mDesp;
    TextInputLayout mTxtDesp,mTxtAuto;
    TextView mCreateBtn;

    String userID;
    String tId,tDesp,tCat;

    FirebaseFirestore fStore;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_settings);

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

        mDesp = findViewById(R.id.desp_tsetting);
        mAutoTextV = findViewById(R.id.spinner_tsetting);

        mTxtDesp = findViewById(R.id.desp_txt_tsetting);
        mTxtAuto = findViewById(R.id.spinner_txt_tsetting);


        mCreateBtn = findViewById(R.id.save_tsettings);

        MaterialToolbar toolbar = findViewById(R.id.toolbar_tsettings);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(),
                R.layout.item_spinner,
                getResources().getStringArray(R.array.categoriesArray));

        mAutoTextV.setAdapter(arrayAdapter);
        mAutoTextV.setSelection(0);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    private void initiateFirebaseClasses() {
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userID = fAuth.getUid();

        Bundle b = getIntent().getExtras();
        tId = b.getString("topicid");
    }


    private void getUserData() {

        fStore.collection(Keys.TOPIC_COLLECTION)
                .document(tId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        tDesp = value.getString(Keys.TOPICDESP);
                        tCat = value.getString(Keys.TOPICCATEGORY);
                        mAutoTextV.setText(tCat);
                        mDesp.setText(tDesp);
                    }
                });

    }

    private void createTopic() {
        String despStr,categoryStr;
        despStr = mDesp.getText().toString().trim();
        categoryStr = mAutoTextV.getText().toString().trim();

        if (despStr.equals("")){
            mTxtDesp.setError("This Field Cannot Be Empty");
            return;
        }
        if (categoryStr.equals("")){
            mTxtAuto.setError("This Field Cannot Be Empty");
            return;
        }

        Map<String,Object> topicdata = new HashMap<>();
        topicdata.put(Keys.TOPICDESP,despStr);
        topicdata.put(Keys.TOPICCATEGORY,categoryStr);

        if (!categoryStr.equals(tCat) || !despStr.equals(tDesp)){
            fStore.collection(Keys.TOPIC_COLLECTION)
                    .document(tId)
                    .update(topicdata)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                finish();
                            }else
                                Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void addTextChangeListner() {
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

    private void saveChanges(){

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}