package com.miniproject.forumapp;

import android.content.Intent;
import android.os.Bundle;

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
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.security.Key;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class FragmentHome extends Fragment {

    RecyclerView recyclerView;
    AdapterHome adapter;
    List<ModelHome> list;

    EditText searchBar;

    String userId;

    View view;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        initiateWithID();

        initiateFirebaseClasses();

        getDataFromFirebase();

        setUpRecyclerView();

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return view;
    }

    private void initiateWithID() {

        recyclerView = view.findViewById(R.id.recyclerview_home);
        searchBar = view.findViewById(R.id.search_home);

        list = new ArrayList<>();

    }

    private void initiateFirebaseClasses() {
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userId = fAuth.getCurrentUser().getUid();
    }

    private void getDataFromFirebase() {
        fStore.collection(Keys.USER_COLLECTION)
                .document(userId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        try {
                            Map<String,Object> map = (Map<String, Object>) value.get(Keys.JOINEDFORUMS);
                            list.clear();
                            for (Map.Entry<String,Object> entry: map.entrySet()) {
                                getForumData(entry.getKey(), (String) entry.getValue());
                            }
                        }catch (Exception e){
                            Log.d("e",error.getMessage());
                        }
                    }
                });
    }

    private void getForumData(String id, String name){
        fStore.collection(Keys.TOPIC_COLLECTION)
                .document(id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot dc) {
                        String desp = (String) dc.get(Keys.TOPICDESP);
                        String category = dc.getString(Keys.TOPICCATEGORY);
                        Map<String, Object> map = (Map<String, Object>) dc.get(Keys.JOINEDUSERS);
                        list.add(new ModelHome(name,desp, dc.getId(),category,map.size()));
                        adapter.notifyItemInserted(list.size()-1);
                    }
                });
    }

    private void setUpRecyclerView() {

        adapter = new AdapterHome(getContext(),list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new AdapterHome.OnItemClickListener() {
            @Override
            public void onViewClick(List<ModelHome> l, int position) {
                Intent intent = new Intent(getContext(),ActivityTopic.class);
                intent.putExtra("topicid",l.get(position).getId());
                startActivity(intent);
            }
        });

    }



}