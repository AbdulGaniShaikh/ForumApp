package com.miniproject.forumapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Map;

public class FragmentProfile extends Fragment {

    TextView mSettings,mLogout;
    TextView mFollowing,mFriends,mName;

    LinearLayout fLL;

    String userId,name;

    ShapeableImageView mProfile;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        initiateWithID();

        initiateFirebaseClasses();

        getUserData();

        mSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ActivitySettings.class);
                startActivity(intent);
            }
        });

        fLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),ActivityMyFriends.class);
                intent.putExtra("id",userId);
                startActivity(intent);
            }
        });

        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fAuth.getCurrentUser()!=null){
                    fAuth.signOut();
                    Intent intent = new Intent(getActivity(), ActivityAuthentication.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });

        return view;
    }

    private void initiateWithID() {
        mName = view.findViewById(R.id.username_profile);
        mProfile = view.findViewById(R.id.avatar_profile);

        mFriends = view.findViewById(R.id.friends_profile);
        mFollowing = view.findViewById(R.id.following_profile);

        fLL = view.findViewById(R.id.con_fr_profile);

        mSettings = view.findViewById(R.id.accountsetting_profile);
        mLogout = view.findViewById(R.id.logout_profile);
    }

    private void initiateFirebaseClasses() {
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userId = fAuth.getCurrentUser().getUid();
    }

    private void getUserData() {

        fStore.collection(Keys.USER_COLLECTION)
                .document(userId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        try {
                            mName.setText(name = value.get(Keys.USERNAME).toString());
                            mProfile.setImageResource(Keys.getAvatar(value.getLong(Keys.AVATAR).intValue()));

                            Map<String,Object> list = (Map<String, Object>) value.get(Keys.JOINEDFORUMS);
                            mFollowing.setText(""+list.size());

                            Map<String,Object> list1 = (Map<String, Object>) value.get(Keys.FRIENDS);
                            mFriends.setText(""+list1.size());
                        }catch (Exception e){
                            Log.d("e",error.getMessage());
                        }
                    }
                });

    }

}