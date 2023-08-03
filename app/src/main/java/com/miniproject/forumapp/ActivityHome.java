package com.miniproject.forumapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

public class ActivityHome extends AppCompatActivity {

    FirebaseAuth fAuth;

    MaterialToolbar toolbar;


    BottomNavigationView btmNavView;
    Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btmNavView = findViewById(R.id.home_bottom_nav);
        toolbar = findViewById(R.id.home_toolbar);

        setUpToolbar();

        fragment = new FragmentHome();
        loadFragment(fragment);

        btmNavView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.bottom_navi_home:
                        fragment = new FragmentHome();
                        loadFragment(fragment);
                        getSupportActionBar().setTitle("Home");
                        getSupportActionBar().show();
                        return true;
                    case R.id.bottom_navi_topic:
                        fragment = new FragmentTopics();
                        loadFragment(fragment);
                        getSupportActionBar().setTitle("Discover Topic");
                        getSupportActionBar().show();
                        return true;
                    case R.id.bottom_navi_youranswers:
                        fragment = new FragmentPeople();
                        loadFragment(fragment);
                        getSupportActionBar().setTitle("Discover People");
                        getSupportActionBar().show();
                        return true;
                    case R.id.bottom_navi_profile:
                        fragment = new FragmentProfile();
                        loadFragment(fragment);
                        getSupportActionBar().hide();
                        return true;
                }
                return false;
            }
        });
    }

    void loadFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.home_framelayout, fragment);
        transaction.commit();
    }

    private void setUpToolbar(){
        if (getSupportActionBar()==null){
            setSupportActionBar(toolbar);
            toolbar.setTitle("Home");
        }

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    @Override
    public void onBackPressed() {
        if (!(fragment instanceof FragmentHome)){
            fragment = new FragmentHome();
            loadFragment(fragment);
            btmNavView.setSelectedItemId(R.id.bottom_navi_home);
            return;
        }
        finish();
    }
}