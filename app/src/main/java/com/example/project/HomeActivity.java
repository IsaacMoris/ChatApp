package com.example.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.google.android.material.tabs.TabLayout;

import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;

import fragment_package.FragmentAdapter;

public class HomeActivity extends AppCompatActivity {

    private  FirebaseAuth mFireBaseAuth;
    private Toolbar homeBar;
    private ViewPager2 homeViewPager;
    private FragmentStateAdapter fragmentAdapter;
    private TabLayout homeTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mFireBaseAuth = FirebaseAuth.getInstance();
        if(mFireBaseAuth.getCurrentUser() == null){
            startActivity(new Intent(HomeActivity.this, StartUp.class));
            finish();
        }

        homeBar = findViewById(R.id.homeBar);
        setSupportActionBar(homeBar);
        getSupportActionBar().setTitle("Teen Titans");

        fragmentAdapter = new FragmentAdapter(this);
        homeViewPager = findViewById(R.id.homeViewPager);
        homeViewPager.setAdapter(fragmentAdapter);

        homeTabLayout = findViewById(R.id.homeTabLayout);

        new TabLayoutMediator(homeTabLayout, homeViewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        switch (position){
                            case 0:
                                tab.setText("Chat");
                                break;
                            case 1:
                                tab.setText("Friends");
                                break;
                            case 2:
                                tab.setText("Requests");
                                break;
                            default:
                                tab.setText("");
                                break;
                        }
                    }
                }).attach();
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
    @Override
    public void onBackPressed()
    {
        android.os.Process.killProcess(android.os.Process.myPid());

    }
}