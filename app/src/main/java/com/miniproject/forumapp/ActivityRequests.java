package com.miniproject.forumapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityRequests extends AppCompatActivity {

    RecyclerView rvReq;

    AdapterFriendReq adapterReq;
    List<ModelFriendReq> listReq;

    LinearLayout noReq;

    String userId,name;
    String tId,tName;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        initiateWithID();

        initiateFirebaseClasses();

        getRequests();

        setUpReqRV();

    }

    private void initiateWithID() {

        rvReq = findViewById(R.id.rv_requests);
        noReq = findViewById(R.id.noreq_requests);

        noReq.setVisibility(View.VISIBLE);
        rvReq.setVisibility(View.GONE);

        listReq = new ArrayList<>();

        Bundle b = getIntent().getExtras();
        tId = b.getString("topicid");
        tName = b.getString("name");

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

    private void getRequests() {
        fStore.collection(Keys.TOPIC_COLLECTION)
                .document(tId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        try {
                            Map<String,Object> map = (Map<String, Object>) value.get(Keys.REQUESTEDUSERS);
                            listReq.clear();
                            if(map.size()==0){
                                noReq.setVisibility(View.VISIBLE);
                                rvReq.setVisibility(View.GONE);
                            }else {
                                noReq.setVisibility(View.GONE);
                                rvReq.setVisibility(View.VISIBLE);
                                for (Map.Entry<String, Object> entry : map.entrySet())
                                    getUserData(entry.getKey(), (String) entry.getValue());
                            }
                        }catch (Exception e){
                            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void getUserData(String key,String name){
        fStore.collection(Keys.USER_COLLECTION)
                .document(key)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        int i = documentSnapshot.getLong(Keys.AVATAR).intValue();
                        listReq.add(new ModelFriendReq(name,key,i));
                        adapterReq.notifyItemInserted(listReq.size()-1);
                    }
                });
    }

    private void setUpReqRV(){

        adapterReq = new AdapterFriendReq(getApplicationContext(),listReq);
        rvReq.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rvReq.setAdapter(adapterReq);

        adapterReq.setOnItemClickListener(new AdapterFriendReq.OnItemClickListener() {
            @Override
            public void onAcceptClick(int position) {
                ModelFriendReq pp = listReq.get(position);
                Map<String,Object> map = new HashMap<>();

                map.put(Keys.JOINEDUSERS+"."+pp.getUserId(),pp.getName());
                map.put(Keys.REQUESTEDUSERS+"."+pp.getUserId(), FieldValue.delete());

                fStore.collection(Keys.TOPIC_COLLECTION)
                        .document(tId)
                        .update(map);

                map.clear();
                map.put(Keys.JOINEDFORUMS+"."+tId,tName);
                map.put(Keys.REQUESTEDFORUMS+"."+tId, FieldValue.delete());

                fStore.collection(Keys.USER_COLLECTION)
                        .document(pp.getUserId())
                        .update(map);

                adapterReq.notifyDataSetChanged();
            }

            @Override
            public void onDeleteClick(int position) {
                ModelFriendReq pp = listReq.get(position);
                Map<String,Object> map = new HashMap<>();
                map.put(Keys.REQUESTEDUSERS+"."+pp.getUserId(), FieldValue.delete());

                fStore.collection(Keys.TOPIC_COLLECTION)
                        .document(tId)
                        .update(map);

                map.clear();
                map.put(Keys.REQUESTEDFORUMS+"."+tId, FieldValue.delete());

                fStore.collection(Keys.USER_COLLECTION)
                        .document(pp.getUserId())
                        .update(map);

                adapterReq.notifyDataSetChanged();
            }
        });

    }
    
}