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
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ActivityQuestions extends AppCompatActivity {

    RecyclerView recyclerView;
    AdapterQuestions adapter;
    List<ModelQuestion> list;

    String userId,name,email;
    String tId,tName;

    int avatar;

    MaterialToolbar toolbar;
    RelativeLayout noQues;

    AlertDialog.Builder builder;
    AlertDialog dialog;

    TextInputEditText mQuest;
    TextInputLayout mQuestTxt;
    TextView submitBtn;

    AlertDialog.Builder ansBuilder;
    AlertDialog ansDialog;

    TextInputEditText mAns;
    TextInputLayout mAnsTxt;
    TextView submitAnsBtn,qAns;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        initiateWithID();

        initiateFirebaseClasses();

        setUpToolbar();

        setUpRV();

        getQuestionsData();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab.setClickable(false);
                questionFormDisplay();
            }
        });
    }

    private void initiateWithID() {

        recyclerView = findViewById(R.id.question_recyclerview);
        noQues = findViewById(R.id.noquestions);

        noQues.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        toolbar = findViewById(R.id.toolbar_questions);

        fab = findViewById(R.id.fab_questions);

        list = new ArrayList<>();

        Bundle b = getIntent().getExtras();
        tId = b.getString("topicid");
        tName = b.getString("name","No Data");

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
    }

    private void getQuestionsData() {

        fStore.collection(Keys.TOPIC_COLLECTION)
                .document(tId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        list.clear();
                        Map<String, Object> map = (Map<String, Object>) value.get(Keys.QUESTION);
                        if (map.size()>0){
                            recyclerView.setVisibility(View.VISIBLE);
                            noQues.setVisibility(View.GONE);
                            for (Map.Entry<String,Object> entry : map.entrySet()){
                                getQuestion(entry.getKey(),entry.getValue().toString());
                            }
                        }else{
                            recyclerView.setVisibility(View.GONE);
                            noQues.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    private void getQuestion(String id,String q){
        fStore.collection(Keys.QUESTION_COLLECTION)
                .document(id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot dc) {
                        String time;
                        time = Keys.formatDate(dc.getTimestamp(Keys.ASKEDON).toDate());
                        list.add(new ModelQuestion(
                                dc.getString(Keys.ASKEDBYNAME),
                                q,
                                dc.getString(Keys.ASKEDBYMAIL),
                                time,
                                dc.getLong(Keys.ASKEDBYDP).intValue(),
                                id
                        ));
                        adapter.notifyItemInserted(list.size()-1);
                    }
                });
    }

    private void setUpRV(){
        adapter = new AdapterQuestions(getApplicationContext(),list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new AdapterQuestions.OnItemClickListener() {
            @Override
            public void onViewClick(int position) {
                ModelQuestion q = list.get(position);

                Intent intent = new Intent(ActivityQuestions.this,ActivityAnswers.class);
                intent.putExtra("id",q.getId());
                intent.putExtra("ques",q.getQuestion());
                startActivity(intent);
            }

            @Override
            public void onAnswer(int position) {
                answerFormDisplay(position);
            }
        });
    }

    private void questionFormDisplay() {
        builder = new AlertDialog.Builder(ActivityQuestions.this);
        View popForm = getLayoutInflater().inflate(R.layout.item_floatingquestion, null);

        mQuest = popForm.findViewById(R.id.ques_FQ);
        mQuestTxt = popForm.findViewById(R.id.ques_txt_FQ);

        submitBtn = popForm.findViewById(R.id.submit_FQ);

        builder.setView(popForm);
        dialog = builder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String question = mQuest.getText().toString();

                if (question.equals("")){
                    mQuestTxt.setError("Enter the Question First");
                    return;
                }
                mQuestTxt.setError(null);

                Map<String, Object> map = new HashMap<>();
                map.put(Keys.QUESTION,question);
                map.put(Keys.ASKEDBYDP,avatar);
                map.put(Keys.ASKEDBYNAME,name);
                map.put(Keys.ASKEDBYMAIL,email);
                map.put(Keys.ASKEDBY,userId);
                map.put(Keys.ASKEDON,new Timestamp(new Date()));
                map.put(Keys.QUESTIONTOPIC,tId);
                map.put(Keys.QUESTIONSANSWER,new HashMap<String,Object>());

                fStore.collection(Keys.QUESTION_COLLECTION)
                        .add(map)
                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()){
                                    addToTopic(task.getResult().getId(),question) ;
                                }else{
                                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                fab.setClickable(true);
            }
        });
    }

    private void addToTopic(String id,String q){

        Map<String, Object> map = new HashMap<>();

        map.put(Keys.QUESTION+"."+id,q);

        fStore.collection(Keys.TOPIC_COLLECTION)
                .document(tId)
                .update(map);
        dialog.dismiss();
    }

    private void answerFormDisplay(int i) {
        ansBuilder = new AlertDialog.Builder(ActivityQuestions.this);
        View popForm = getLayoutInflater().inflate(R.layout.item_floatinganswer, null);

        mAns = popForm.findViewById(R.id.ans_FA);
        mAnsTxt = popForm.findViewById(R.id.ans_txt_FA);
        qAns = popForm.findViewById(R.id.question_FA);
        submitAnsBtn = popForm.findViewById(R.id.submit_FA);

        qAns.setText(list.get(i).getQuestion());

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
                                    addToQuestion(task.getResult().getId(),answer,i) ;
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

    private void addToQuestion(String id,String q,int i){

        Map<String, Object> map = new HashMap<>();

        map.put(Keys.QUESTIONSANSWER+"."+id,q);

        fStore.collection(Keys.QUESTION_COLLECTION)
                .document(list.get(i).getId())
                .update(map);
        ansDialog.dismiss();
    }

    private void setUpToolbar(){
        if (getSupportActionBar()==null){
            setSupportActionBar(toolbar);
            toolbar.setTitle(tName);
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