package com.miniproject.forumapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentPeople extends Fragment {

    RecyclerView rvReq,rvSugg;

    AdapterFriendReq adapterFriendReq;
    List<ModelFriendReq> listFriendReq;

    AdapterSuggestion adapterSuggestion;
    List<ModelPeople> listSuggestion;

    List<String> friends;

    TextView searchBar;
    LinearLayout noReq;

    String userId,name;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_people, container, false);

        initiateWithID();

        initiateFirebaseClasses();

        getRequests();

        setUpReqRV();
        
        setUpSuggRV();

        getSuggestions();
        
        searchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),ActivitySearch.class);
                intent.putExtra("from",Keys.USER_INTENT);
                startActivity(intent);
            }
        });
        
        return view;
    }

    private void initiateWithID() {

        rvReq = view.findViewById(R.id.rvreq_people);
        rvSugg = view.findViewById(R.id.rvsugg_people);

        searchBar = view.findViewById(R.id.search_people);
        noReq = view.findViewById(R.id.noreq_people);

        noReq.setVisibility(View.VISIBLE);
        rvReq.setVisibility(View.GONE);

        listFriendReq = new ArrayList<>();
        listSuggestion = new ArrayList<>();
        friends = new ArrayList<>();

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
                        Map<String, Object> map = (Map<String, Object>) dc.get(Keys.FRIENDS);
                        for (Map.Entry<String, Object> v : map.entrySet()){
                            friends.add(v.getKey());
                        }
                    }
                });
    }

    private void getRequests() {
        fStore.collection(Keys.USER_COLLECTION)
                .document(userId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        try {
                            Map<String,Object> map = (Map<String, Object>) value.get(Keys.FRIENDREQUESTS);
                            listFriendReq.clear();
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
                            Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
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
                        listFriendReq.add(new ModelFriendReq(name,key,i));
                        adapterFriendReq.notifyItemInserted(listFriendReq.size()-1);
                    }
                });
    }
    
    private void getSuggestions(){
        fStore.collection(Keys.USER_COLLECTION)
                .limit(5)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        listSuggestion.clear();
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for(DocumentSnapshot dc : list){
                            if(!dc.getId().equals(userId) && !friends.contains(dc.getId())) {
                                listSuggestion.add(new ModelPeople(
                                        dc.getString(Keys.USERNAME),
                                        dc.getId(),
                                        dc.getString(Keys.EMAIL),
                                        dc.getLong(Keys.AVATAR).intValue()
                                        ));
                                adapterSuggestion.notifyItemInserted(list.size() - 1);
                            }
                        }
                    }
                });
    }

    private void setUpReqRV() {

        adapterFriendReq = new AdapterFriendReq(getContext(),listFriendReq);
        rvReq.setLayoutManager(new LinearLayoutManager(getContext()));
        rvReq.setAdapter(adapterFriendReq);

        adapterFriendReq.setOnItemClickListener(new AdapterFriendReq.OnItemClickListener() {
            @Override
            public void onAcceptClick(int position) {
                ModelFriendReq pp = listFriendReq.get(position);
                Map<String,Object> map = new HashMap<>();

                map.put(Keys.FRIENDS+"."+pp.getUserId(),pp.getName());
                map.put(Keys.FRIENDREQUESTS+"."+pp.getUserId(), FieldValue.delete());

                fStore.collection(Keys.USER_COLLECTION)
                        .document(userId)
                        .update(map);

                map.clear();
                map.put(Keys.FRIENDS+"."+userId,name);
                map.put(Keys.MYREQUESTS+"."+userId, FieldValue.delete());

                fStore.collection(Keys.USER_COLLECTION)
                        .document(pp.getUserId())
                        .update(map);
            }

            @Override
            public void onDeleteClick(int position) {
                ModelFriendReq pp = listFriendReq.get(position);
                Map<String,Object> map = new HashMap<>();
                map.put(Keys.FRIENDREQUESTS+"."+pp.getUserId(), FieldValue.delete());

                fStore.collection(Keys.USER_COLLECTION)
                        .document(userId)
                        .update(map);

                map.clear();
                map.put(Keys.MYREQUESTS+"."+userId, FieldValue.delete());

                fStore.collection(Keys.USER_COLLECTION)
                        .document(pp.getUserId())
                        .update(map);
            }
        });

    }

    private void setUpSuggRV() {

        adapterSuggestion = new AdapterSuggestion(getContext(),listSuggestion);
        rvSugg.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        rvSugg.setAdapter(adapterSuggestion);

        adapterSuggestion.setOnItemClickListener(new AdapterSuggestion.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(getContext(), ActivityUserProfile.class);
                intent.putExtra("userid",listSuggestion.get(position).getUserid());
                startActivity(intent);
            }
        });

    }
    
}