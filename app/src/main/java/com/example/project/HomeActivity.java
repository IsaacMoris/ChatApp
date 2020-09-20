package com.example.project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.google.android.material.tabs.TabLayout;

import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    private  FirebaseAuth mFireBaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private Toolbar homeBar;
    private ViewPager homeViewPager;
    private FragmentAdapter fragmentAdapter;
    private TabLayout homeTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        homeBar = findViewById(R.id.homeBar);
        setSupportActionBar(homeBar);
        getSupportActionBar().setTitle("Teen Titans");

        fragmentAdapter = new FragmentAdapter(getSupportFragmentManager());
        homeViewPager = findViewById(R.id.homeViewPager);
        homeViewPager.setAdapter(fragmentAdapter);

        homeTabLayout = findViewById(R.id.homeTabLayout);
        homeTabLayout.setupWithViewPager(homeViewPager);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Intent nextActivity;

        if(item.getItemId() == R.id.allUsersMenuOption){
            goToActivity(new AllUsersActivity());
        }

        else if(item.getItemId() == R.id.settingsMenuOption){
            goToActivity(new SettingsActivity());
        }

        else if(item.getItemId() == R.id.logOutMenuOption){
            FirebaseAuth.getInstance().signOut();
            goToActivity(new LoginActivity());
            finish();
        }
        return true;
    }

    private void goToActivity(AppCompatActivity destinationActivity){
        Intent nextActivity = new Intent(HomeActivity.this, destinationActivity.getClass());
        startActivity(nextActivity);

    }
}