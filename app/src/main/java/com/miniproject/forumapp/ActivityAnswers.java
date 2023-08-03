package com.miniproject.forumapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityAnswers extends AppCompatActivity {

    RecyclerView recyclerView;
    AdapterAnswer adapter;
    List<ModelAnswer> list;

    String userId,name,email;
    String qId,question;

    int avatar;

    MaterialToolbar toolbar;
    RelativeLayout noAns;

    AlertDialog.Builder ansBuilder;
    AlertDialog ansDialog;

    TextInputEditText mAns;
    TextInputLayout mAnsTxt;
    TextView submitAnsBtn,qAns;

    TextView qNameT,qMailT,qTimeT,qQuestionT,addAnsBtn;
    ShapeableImageView qDP;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answers);

        initiateWithID();

        initiateFirebaseClasses();

        setUpToolbar();

        setUpRV();

        getAnswersData();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab.setClickable(false);
                answerFormDisplay();
            }
        });

        addAnsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab.setClickable(false);
                answerFormDisplay();
            }
        });
        
    }

    private void initiateWithID() {

        qNameT = findViewById(R.id.name_answers);
        qDP = findViewById(R.id.avatar_answers);
        qMailT = findViewById(R.id.mail_answers);
        qTimeT = findViewById(R.id.timestamp_answers);
        qQuestionT = findViewById(R.id.question_answers);
        addAnsBtn = findViewById(R.id.addans_answers);

        recyclerView = findViewById(R.id.answer_recyclerview);
        noAns = findViewById(R.id.noanswers);

        noAns.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        toolbar = findViewById(R.id.toolbar_answers);
        fab = findViewById(R.id.fab_answers);
        list = new ArrayList<>();

        Bundle b = getIntent().getExtras();
        qId = b.getString("id");
        question = b.getString("ques","No Data");
    }

    private void initiateFirebaseClasses() {
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userId = fAuth.getCurrentUser().getUid();
        fStore.collection(Keys.USER_COLLECTION)
                .document(userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot dc) {
                        name = dc.getString(Keys.USERNAME);
                        email = dc.getString(Keys.EMAIL);
                        avatar = dc.getLong(Keys.AVATAR).intValue();
                    }
                });

        fStore.collection(Keys.QUESTION_COLLECTION)
                .document(qId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot dc) {




                    }
                });
    }

    private void getAnswersData() {

        fStore.collection(Keys.QUESTION_COLLECTION)
                .document(qId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        list.clear();

                        qNameT.setText(value.getString(Keys.ASKEDBYNAME));
                        qDP.setImageResource(Keys.getAvatar(value.getLong(Keys.ASKEDBYDP).intValue()));
                        qMailT.setText(value.getString(Keys.ASKEDBYMAIL));
                        qTimeT.setText(Keys.formatDate(value.getTimestamp(Keys.ASKEDON).toDate()));
                        qQuestionT.setText(question);

                        Map<String, Object> map = (Map<String, Object>) value.get(Keys.QUESTIONSANSWER);
                        if (map.size()>0){
                            recyclerView.setVisibility(View.VISIBLE);
                            noAns.setVisibility(View.GONE);
                        }else{
                            recyclerView.setVisibility(View.GONE);
                            noAns.setVisibility(View.VISIBLE);
                        }
                        for (Map.Entry<String,Object> entry : map.entrySet()){
                            getAnswer(entry.getKey(),entry.getValue().toString());
                        }
                    }
                });
    }

    private void getAnswer(String id,String q){
        fStore.collection(Keys.ANSWER_COLLECTION)
                .document(id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot dc) {
                        String time;
                        time = Keys.formatDate(dc.getTimestamp(Keys.ANSWEREDON).toDate());
                        list.add(new ModelAnswer(
                                dc.getString(Keys.ANSWEREDBYNAME),
                                q,
                                dc.getString(Keys.ANSWEREDBYMAIL),
                                time,
                                id,
                                dc.getLong(Keys.ANSWEREDBYDP).intValue()
                        ));
                        adapter.notifyItemInserted(list.size()-1);
                    }
                });
    }

    private void setUpRV(){
        adapter = new AdapterAnswer(getApplicationContext(),list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);
    }

    private void answerFormDisplay() {
        ansBuilder = new AlertDialog.Builder(ActivityAnswers.this);
        View popForm = getLayoutInflater().inflate(R.layout.item_floatinganswer, null);

        mAns = popForm.findViewById(R.id.ans_FA);
        mAnsTxt = popForm.findViewById(R.id.ans_txt_FA);
        qAns = popForm.findViewById(R.id.question_FA);
        submitAnsBtn = popForm.findViewById(R.id.submit_FA);

        qAns.setText(question);

        ansBuilder.setView(popForm);
        ansDialog = ansBuilder.create();

        ansDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ansDialog.show();

        submitAnsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String answer = mAns.getText().toString();

                if (answer.equals("")){
                    mAnsTxt.setError("Enter the Answer First");
                    return;
                }
                mAnsTxt.setError(null);

                Map<String, Object> map = new HashMap<>();
                map.put(Keys.ANSWER,answer);
                map.put(Keys.ANSWEREDBYDP,avatar);
                map.put(Keys.ANSWEREDBYNAME,name);
                map.put(Keys.ANSWEREDBYMAIL,email);
                map.put(Keys.ANSWEREDBY,userId);
                map.put(Keys.ANSWEREDON,new Timestamp(new Date()));

                fStore.collection(Keys.ANSWER_COLLECTION)
                        .add(map)
                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()){
                                    addToQuestion(task.getResult().getId(),answer) ;
                                }else{
                                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
        ansDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                fab.setClickable(true);
            }
        });
    }

    private void addToQuestion(String id,String q){

        Map<String, Object> map = new HashMap<>();

        map.put(Keys.QUESTIONSANSWER+"."+id,q);

        fStore.collection(Keys.QUESTION_COLLECTION)
                .document(qId)
                .update(map);
        ansDialog.dismiss();
    }

    private void setUpToolbar(){
        if (getSupportActionBar()==null){
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

        }

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}