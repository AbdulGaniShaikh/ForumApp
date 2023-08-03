package com.miniproject.forumapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ActivitySettings extends AppCompatActivity {

    EditText editText;
    TextView saveBtn;
    ImageButton left,right;
    ShapeableImageView dp;

    String userId,name;
    int i;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);



        editText = findViewById(R.id.name_settings);
        saveBtn = findViewById(R.id.save_settings);
        left = findViewById(R.id.left_settings);
        right = findViewById(R.id.right_settings);
        dp = findViewById(R.id.avatar_settings);

        initiateFirebaseClasses();

        fStore.collection(Keys.USER_COLLECTION)
                .document(userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot dc) {
                        i = dc.getLong(Keys.AVATAR).intValue();
                        name = dc.getString(Keys.USERNAME);

                        dp.setImageResource(Keys.getAvatar(i));
                        editText.setText(name);

                    }
                });

        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 if (i==0)
                    i=15;
                 else
                     i--;

                dp.setImageResource(Keys.getAvatar(i));
            }
        });
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(i>=15)
                    i=0;
                else
                    i++;

                dp.setImageResource(Keys.getAvatar(i));
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Map<String ,Object> map = new HashMap<>();

                map.put(Keys.USERNAME,editText.getText().toString().trim());
                map.put(Keys.AVATAR,i);

                fStore.collection(Keys.USER_COLLECTION)
                        .document(userId)
                        .update(map)
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
        });

    }

    private void initiateFirebaseClasses() {
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userId = fAuth.getCurrentUser().getUid();
    }


}