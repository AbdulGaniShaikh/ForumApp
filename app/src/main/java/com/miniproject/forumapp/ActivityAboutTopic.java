package com.miniproject.forumapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Map;

public class ActivityAboutTopic extends AppCompatActivity {

    TextView tname,name,desp,time,followers,type;
    ImageView imageView;

    MaterialToolbar toolbar;

    String userId;
    String tnames,tId;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_topic);

        tname = findViewById(R.id.name_about);
        name  = findViewById(R.id.createdby_about);
        desp = findViewById(R.id.desp_about);
        time = findViewById(R.id.createdon_about);
        followers  = findViewById(R.id.followers_about);
        type  = findViewById(R.id.category_about);

        toolbar = findViewById(R.id.toolbar_about);



        imageView  = findViewById(R.id.img_about);


        Bundle b = getIntent().getExtras();
        tId = b.getString("topicid");

        initiateFirebaseClasses();

        setUpToolbar();

        fStore.collection(Keys.TOPIC_COLLECTION)
                .document(tId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        tname.setText(value.getString(Keys.TOPICNAME));
                        name.setText(value.getString(Keys.TOPICCREATEDBY));
                        desp.setText(value.getString(Keys.TOPICDESP));
                        String t = Keys.formatDate(value.getTimestamp(Keys.TOPIC_CREATEDON).toDate());
                        time.setText(t);

                        Map<String, Object> map = (Map<String, Object>) value.get(Keys.JOINEDUSERS);

                        followers.setText(Keys.format(map.size())+" followers");
                        type.setText(value.getString(Keys.TOPICCATEGORY));

                        imageView.setImageResource(Keys.getImage(value.getString(Keys.TOPICCATEGORY)));
                    }
                });

    }

    private void initiateFirebaseClasses() {
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userId = fAuth.getCurrentUser().getUid();
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