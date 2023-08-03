package com.miniproject.forumapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FragmentTopics extends Fragment{

    View view;
    FloatingActionButton fab;

    RecyclerView recyclerView;
    AdapterHome adapter;
    List<ModelHome> list;
    List<String> uList;

    String userId;

    TextView searchBar;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_topics, container, false);

        initiateWithID();

        initiateFirebaseClasses();

        setUpRV();

        getForumData();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),ActivityCreateTopic.class));
            }
        });

        searchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),ActivitySearch.class);
                intent.putExtra("from",Keys.TOPIC_INTENT);
                startActivity(intent);
            }
        });

        return view;
    }

    private void initiateWithID() {
        searchBar = view.findViewById(R.id.search_topics);
        fab = view.findViewById(R.id.topics_fab);

        recyclerView = view.findViewById(R.id.rv_topics);
    }

    private void initiateFirebaseClasses() {
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userId = fAuth.getCurrentUser().getUid();

        list = new ArrayList<>();
        uList = new ArrayList<>();

        fStore.collection(Keys.USER_COLLECTION)
                .document(userId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        Map<String, Object> map = (Map<String, Object>) value.get(Keys.JOINEDFORUMS);
                        for (Map.Entry<String, Object> m : map.entrySet()){
                            uList.add(m.getKey());
                        }
                    }
                });
    }

    private void setUpRV(){
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

    private void getForumData(){
        fStore.collection(Keys.TOPIC_COLLECTION)
                .limit(8)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        List<DocumentSnapshot> l = value.getDocuments();
                        list.clear();
                        for(DocumentSnapshot dc : l){
                            if (!uList.contains(dc.getId())){
                                String desp = dc.getString(Keys.TOPICDESP);
                                String category = dc.getString(Keys.TOPICCATEGORY);
                                String name = dc.getString(Keys.TOPICNAME);
                                Map<String, Object> map = (Map<String, Object>) dc.get(Keys.JOINEDUSERS);
                                list.add(new ModelHome(name,desp, dc.getId(),category,map.size()));
                                adapter.notifyItemInserted(list.size()-1);
                            }

                        }

                    }
                });
    }

}