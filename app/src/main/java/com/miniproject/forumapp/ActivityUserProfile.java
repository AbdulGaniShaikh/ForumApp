package com.miniproject.forumapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.security.Key;
import java.util.HashMap;
import java.util.Map;

public class ActivityUserProfile extends AppCompatActivity {

    TextView mName,mMail,mFollowing,mFriends;
    TextView addFriendBtn/*,messageBtn*/;

    LinearLayout fLL,ftLL;

    String userId,name;
    String fUserId,fName;
    int status;

    ShapeableImageView mProfile;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        initiateWithID();

        initiateFirebaseClasses();

        getDataFromFirebase();
        
        addFriendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (status==Keys.NO_STATUS){
                    sendFriendRequest();
                }else if(status==Keys.STATUS_REQUESTED){
                    cancelFriendRequest();
                }else if (status==Keys.STATUS_FRIEND){
                    unFriend();
                }
            }
        });

        fLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityUserProfile.this,ActivityMyFriends.class);
                intent.putExtra("id",fUserId);
                startActivity(intent);
            }
        });

    }

    private void initiateWithID() {

        mName = findViewById(R.id.username_user_profile);
        mMail = findViewById(R.id.mail_user_profile);
        mFollowing = findViewById(R.id.topic_user_profile);
        mFriends= findViewById(R.id.friends_user_profile);
        addFriendBtn = findViewById(R.id.follow_btn_user_profile);

        fLL = findViewById(R.id.friends_con_user_profile);
//        messageBtn = findViewById(R.id.message_user_profile);

        mProfile = findViewById(R.id.avatar_user_profile);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

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

    private void getDataFromFirebase() {

        Bundle extra = getIntent().getExtras();
        fUserId = extra.getString("userid");

        fStore.collection(Keys.USER_COLLECTION)
                .document(fUserId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        fName = value.getString(Keys.USERNAME);
                        mName.setText(fName);
                        mMail.setText(value.getString(Keys.EMAIL));

                        mProfile.setImageResource(Keys.getAvatar(value.getLong(Keys.AVATAR).intValue()));

                        Map<String, Object> map = (Map<String, Object>) value.get(Keys.FRIENDS);
                        mFriends.setText(map.size()+"");

                        Map<String, Object> req = (Map<String, Object>) value.get(Keys.FRIENDREQUESTS);
                        if (map.containsKey(userId))
                            status = Keys.STATUS_FRIEND;
                        else if (req.containsKey(userId))
                            status = Keys.STATUS_REQUESTED;
                        else
                            status = Keys.NO_STATUS;

                        map = (Map<String, Object>) value.get(Keys.JOINEDFORUMS);
                        mFollowing.setText(map.size()+"");

                        setStatus();

                    }
                });

    }

    private void setStatus(){
        if(status == Keys.STATUS_FRIEND){
//            messageBtn.setVisibility(View.VISIBLE);
            addFriendBtn.setText("Unfriend");
        }else if (status==Keys.STATUS_REQUESTED){
//            messageBtn.setVisibility(View.GONE);
            addFriendBtn.setText("Cancel Request");
        }else if(status==Keys.NO_STATUS){
//            messageBtn.setVisibility(View.GONE);
            addFriendBtn.setText("Add Friend");
        }
    }

    private void sendFriendRequest(){
        Map<String ,Object> map = new HashMap<>();
        map.put(Keys.FRIENDREQUESTS+"."+userId,name);

        fStore.collection(Keys.USER_COLLECTION)
                .document(fUserId)
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
                Keys.MYREQUESTS+"."+fUserId,
                fName
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
        map.put(Keys.MYREQUESTS+"."+fUserId, FieldValue.delete());

        fStore.collection(Keys.USER_COLLECTION)
                .document(userId)
                .update(map);

        map.clear();
        map.put(Keys.FRIENDREQUESTS+"."+userId, FieldValue.delete());

        fStore.collection(Keys.USER_COLLECTION)
                .document(fUserId)
                .update(map);
        status = Keys.NO_STATUS;
    }
    private void unFriend(){
        Map<String,Object> map = new HashMap<>();
        map.put(Keys.FRIENDS+"."+fUserId, FieldValue.delete());

        fStore.collection(Keys.USER_COLLECTION)
                .document(userId)
                .update(map);

        map.clear();
        map.put(Keys.FRIENDS+"."+userId, FieldValue.delete());

        fStore.collection(Keys.USER_COLLECTION)
                .document(fUserId)
                .update(map);
        status = Keys.NO_STATUS;
    }


}