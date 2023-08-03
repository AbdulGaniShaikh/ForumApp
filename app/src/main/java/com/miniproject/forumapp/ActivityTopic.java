package com.miniproject.forumapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import me.fahmisdk6.avatarview.AvatarView;

public class ActivityTopic extends AppCompatActivity {

    TextView mName,mDesp,mFollowers,followBtn,editTopic;
    TextView viewQuestions,viewRequests,leaveTopic,about,viewFollowers;
    ImageView mImage;

    AvatarView mDP;

    MaterialToolbar toolbar;

    int status;

    String userId,name,admin;
    String tId,tName;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);

        initiateWithID();

        initiateFirebaseClasses();

        setUpToolbar();

        getData();

        followBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (status==Keys.NO_STATUS){
                    sendRequest();
                }else if(status==Keys.STATUS_REQUESTED){
                    cancelFriendRequest();
                }else if (status==Keys.STATUS_FRIEND){
                    unFollow();
                }
            }
        });

        viewQuestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityTopic.this,ActivityQuestions.class);
                intent.putExtra("topicid",tId);
                intent.putExtra("name",tName);
                startActivity(intent);
            }
        });

        editTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityTopic.this,ActivityTopicSettings.class);
                intent.putExtra("topicid",tId);
                intent.putExtra("name",tName);
                startActivity(intent);
            }
        });

        viewRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityTopic.this,ActivityRequests.class);
                intent.putExtra("topicid",tId);
                intent.putExtra("name",tName);
                startActivity(intent);
            }
        });

        leaveTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unFollow();
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityTopic.this,ActivityAboutTopic.class);
                intent.putExtra("topicid",tId);
                intent.putExtra("name",tName);
                startActivity(intent);
            }
        });

        viewFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityTopic.this,ActivityMyFriends.class);
                intent.putExtra("id",tId);
                intent.putExtra("from",Keys.TOPIC_INTENT);
                startActivity(intent);
            }
        });

    }

    private void initiateWithID() {

        mName = findViewById(R.id.name_topic);
        mDesp = findViewById(R.id.desp_topic);
        mFollowers = findViewById(R.id.followers_topic);
        followBtn = findViewById(R.id.follow_btn_topic);
        editTopic = findViewById(R.id.edittopic_topic);
        mDP = findViewById(R.id.avatar_topic);

        mImage = findViewById(R.id.image_topic);

        viewQuestions = findViewById(R.id.questions_topic);
        viewRequests = findViewById(R.id.requests_topic);
        leaveTopic= findViewById(R.id.leave_topic);
        viewFollowers= findViewById(R.id.viewfollowers_topic);
        about= findViewById(R.id.about_topic);

        toolbar = findViewById(R.id.topic_toolbar);

        Bundle b = getIntent().getExtras();
        tId = b.get("topicid").toString();
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
                    }
                });
    }

    private void getData(){
        fStore.collection(Keys.TOPIC_COLLECTION)
                .document(tId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        tName = value.getString(Keys.TOPICNAME);
                        mName.setText(tName);
                        mDP.bind(tName,null);
                        mDesp.setText(value.getString(Keys.TOPICDESP));

                        mImage.setImageResource(Keys.getImage(value.getString(Keys.TOPICCATEGORY)));

                        Map<String, Object> map1 = (Map<String, Object>) value.get(Keys.REQUESTEDUSERS);
                        if (map1.size()>0){
                            viewRequests.setText("View Requests ("+map1.size()+")");
                        }else
                            viewRequests.setText("View Requests");

                        Map<String,Object> map = (Map<String, Object>) value.get(Keys.JOINEDUSERS);
                        mFollowers.setText(Keys.format(map.size())+" followers");
                        admin = value.getString(Keys.TOPICCREATEDBYID);

                        map = (Map<String, Object>) value.get(Keys.JOINEDUSERS);
                        Map<String, Object> req = (Map<String, Object>) value.get(Keys.REQUESTEDUSERS);
                        if (map.containsKey(userId))
                            status = Keys.STATUS_FRIEND;
                        else if (req.containsKey(userId))
                            status = Keys.STATUS_REQUESTED;
                        else
                            status = Keys.NO_STATUS;



                        setStatus();
                    }
                });
    }

    private void setStatus(){
        if(status == Keys.STATUS_FRIEND){
            viewQuestions.setVisibility(View.VISIBLE);
            leaveTopic.setVisibility(View.VISIBLE);
            followBtn.setText("Unfollow");
        }else if (status==Keys.STATUS_REQUESTED){
            viewQuestions.setVisibility(View.GONE);
            leaveTopic.setVisibility(View.GONE);
            followBtn.setText("Cancel Request");
        }else if(status==Keys.NO_STATUS){
            viewQuestions.setVisibility(View.GONE);
            leaveTopic.setVisibility(View.GONE);
            followBtn.setText("Follow");
        }

        if (admin.equals(userId)) {
            viewRequests.setVisibility(View.VISIBLE);
            editTopic.setVisibility(View.VISIBLE);
            leaveTopic.setVisibility(View.GONE);
            followBtn.setVisibility(View.GONE);
        }
        else {
            viewRequests.setVisibility(View.GONE);
            editTopic.setVisibility(View.GONE);
        }
    }

    private void sendRequest(){
        Map<String ,Object> map = new HashMap<>();
        map.put(Keys.REQUESTEDUSERS+"."+userId,name);

        fStore.collection(Keys.TOPIC_COLLECTION)
                .document(tId)
                .update(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        map.clear();
        map.put(
                Keys.REQUESTEDFORUMS+"."+tId,
                tName
        );
        fStore.collection(Keys.USER_COLLECTION)
                .document(userId)
                .update(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        status = Keys.STATUS_FRIEND;
    }
    private void cancelFriendRequest(){
        Map<String,Object> map = new HashMap<>();
        map.put(Keys.REQUESTEDFORUMS+"."+tId, FieldValue.delete());

        fStore.collection(Keys.USER_COLLECTION)
                .document(userId)
                .update(map);

        map.clear();
        map.put(Keys.REQUESTEDUSERS+"."+userId, FieldValue.delete());

        fStore.collection(Keys.TOPIC_COLLECTION)
                .document(tId)
                .update(map);
        status = Keys.NO_STATUS;
    }
    private void unFollow(){
        Map<String,Object> map = new HashMap<>();
        map.put(Keys.JOINEDFORUMS+"."+tId, FieldValue.delete());

        fStore.collection(Keys.USER_COLLECTION)
                .document(userId)
                .update(map);

        map.clear();
        map.put(Keys.JOINEDUSERS+"."+userId, FieldValue.delete());

        fStore.collection(Keys.TOPIC_COLLECTION)
                .document(tId)
                .update(map);
        status = Keys.NO_STATUS;
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