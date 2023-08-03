package com.miniproject.forumapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.security.Key;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ActivitySearch extends AppCompatActivity {

    AdapterPeople adapterSearch;
    List<ModelPeople> listSearch,filter;
    RecyclerView rvSearch;

    LinearLayout ll;

    int from;

    EditText searchBar;
    MaterialToolbar toolbar;

    String userId,name;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initiateWithID();

        initiateFirebaseClasses();

        setUpToolbar();

        setUpSearchRV();

        getSearchData();


        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filter.clear();
                for (ModelPeople p : listSearch){

                    String s = p.getName().toLowerCase(Locale.ROOT);
                    String s1 = charSequence.toString().toLowerCase(Locale.ROOT);

                    if (s.contains(s1) && !p.getUserid().equals(userId)){
                        filter.add(p);
                    }
                }

                if (filter.size()>0){
                    rvSearch.setVisibility(View.VISIBLE);
                    ll.setVisibility(View.GONE);
                }else{
                    rvSearch.setVisibility(View.GONE);
                    ll.setVisibility(View.VISIBLE);
                }
                adapterSearch.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void initiateWithID() {
        rvSearch = findViewById(R.id.rv_search);
        searchBar = findViewById(R.id.edt_search);
        searchBar.requestFocus();
        ll = findViewById(R.id.noresult_search);

        toolbar = findViewById(R.id.search_toolbar);
        listSearch = new ArrayList<>();
        filter = new ArrayList<>();

        rvSearch.setVisibility(View.GONE);
        ll.setVisibility(View.VISIBLE);

        Bundle b = getIntent().getExtras();
        from = b.getInt("from",Keys.USER_INTENT);
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

    private void setUpSearchRV(){

        adapterSearch = new AdapterPeople(this,filter);
        rvSearch.setLayoutManager(new LinearLayoutManager(this));
        rvSearch.setAdapter(adapterSearch);

        adapterSearch.setOnItemClickListener(new AdapterPeople.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                if (from==Keys.TOPIC_INTENT) {
                    Intent intent = new Intent(ActivitySearch.this, ActivityTopic.class);
                    intent.putExtra("topicid", filter.get(position).getUserid());
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(ActivitySearch.this, ActivityUserProfile.class);
                    intent.putExtra("userid", filter.get(position).getUserid());
                    startActivity(intent);
                }
            }
        });
    }

    private void getSearchData(){
        if (from==Keys.TOPIC_INTENT) {
            fStore.collection(Keys.TOPIC_COLLECTION)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<DocumentSnapshot> l = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot dc : l) {
                                listSearch.add(new ModelPeople(
                                        dc.getString(Keys.TOPICNAME),
                                        dc.getId(),
                                        dc.getString(Keys.TOPICDESP),
                                        0
                                ));
                            }
                        }
                    });
        }else{
            fStore.collection(Keys.USER_COLLECTION)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<DocumentSnapshot> l = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot dc : l) {
                                listSearch.add(new ModelPeople(
                                        dc.getString(Keys.USERNAME),
                                        dc.getId(),
                                        dc.getString(Keys.EMAIL),
                                        dc.getLong(Keys.AVATAR).intValue()
                                ));
                            }
                        }
                    });
        }
    }

    private void setUpToolbar(){
        if (getSupportActionBar()==null){
            setSupportActionBar(toolbar);
            if (from==Keys.TOPIC_INTENT)
                toolbar.setTitle("Search Topic");
            else
                toolbar.setTitle("Search People");
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