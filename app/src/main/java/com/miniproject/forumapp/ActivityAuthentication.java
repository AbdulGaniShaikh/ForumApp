package com.miniproject.forumapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class ActivityAuthentication extends AppCompatActivity {

    BottomNavigationView btmNavView;
    MaterialToolbar toolbar;
    Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        btmNavView = findViewById(R.id.auth_bottom_nav);
        toolbar = findViewById(R.id.auth_toolbar);
        setSupportActionBar(toolbar);

        fragment = new FragmentLogin();
        loadFragment(fragment);

        btmNavView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_login:
                        fragment = new FragmentLogin();
                        toolbar.setTitle("Login");
                        loadFragment(fragment);
                        return true;
                    case R.id.menu_signup:
                        fragment = new FragmentSignup();
                        toolbar.setTitle("Sign Up");
                        loadFragment(fragment);
                        return true;
                }
                return false;
            }
        });

    }

    void loadFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.auth_framelayout, fragment).commit();
    }

    @Override
    public void onBackPressed() {
        if (!(fragment instanceof FragmentLogin)){
            fragment = new FragmentLogin();
            loadFragment(fragment);
            btmNavView.setSelectedItemId(R.id.menu_login);
            return;
        }
        finish();
    }
}