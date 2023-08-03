package com.miniproject.forumapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ActivityMyFriends extends AppCompatActivity {

    RecyclerView recyclerView;
    AdapterPeople adapter;
    List<ModelPeople> list;
    TextView t;

    LinearLayout ll;

    String userId,name;
    String fId;
    String friendsMessage;

    boolean fromProfile;
    
    MaterialToolbar toolbar;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_friends);

        initiateWithID();

        initiateFirebaseClasses();

        setUpToolbar();

        getDataFromFirebase();


        setUpRecyclerView();

    }

    private void initiateWithID() {

        recyclerView = findViewById(R.id.rv_friends);
        ll = findViewById(R.id.noresult_friends);
        toolbar = findViewById(R.id.toolbar_friends);
        t = findViewById(R.id.message_friends);

        friendsMessage = " follow someone they'll appear here.";
        
        Bundle b = getIntent().getExtras();
        fId = b.getString("id");
//        fromProfile = b.getBoolean("fromprofile");

        list = new ArrayList<>();

    }

    private void initiateFirebaseClasses() {
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userId = fAuth.getCurrentUser().getUid();
    }

    private void getDataFromFirebase() {
        fStore.collection(Keys.USER_COLLECTION)
                .document(fId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        try {
                            name = value.getString(Keys.USERNAME);
                            t.setText("When "+name+friendsMessage);
                            if (!fId.equals(userId))
                                toolbar.setTitle(name+"'s Friend");
                            else
                                toolbar.setTitle("My Friends");
                            Map<String,Object> map = (Map<String, Object>) value.get(Keys.FRIENDS);
                            list.clear();

                            if (map.size()>0){
                                recyclerView.setVisibility(View.VISIBLE);
                                ll.setVisibility(View.GONE);
                            }else{
                                recyclerView.setVisibility(View.GONE);
                                ll.setVisibility(View.VISIBLE);
                                return;
                            }

                            for (Map.Entry<String,Object> entry: map.entrySet()) {
                                getUserData(entry.getKey(), (String) entry.getValue());
                            }
                        }catch (Exception e){
                            Log.d("e",error.getMessage());
                        }
                    }
                });
    }

    private void getUserData(String id, String name){
        fStore.collection(Keys.USER_COLLECTION)
                .document(id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot dc) {
                        String mail = dc.getString(Keys.EMAIL);
                        int dp = dc.getLong(Keys.AVATAR).intValue();
                        list.add(new ModelPeople(name,id,mail,dp));
                        adapter.notifyItemInserted(list.size()-1);
                    }
                });
    }

    private void setUpRecyclerView() {

        adapter = new AdapterPeople(this,list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged();

        adapter.setOnItemClickListener(new AdapterPeople.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(ActivityMyFriends.this, ActivityUserProfile.class);
                intent.putExtra("userid", list.get(position).getUserid());
                startActivity(intent);
            }
        });

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