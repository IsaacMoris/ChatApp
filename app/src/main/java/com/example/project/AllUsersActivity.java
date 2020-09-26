package com.example.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import display_users.UserDataModel;
import display_users.UserDataRecycleAdapter;

public class AllUsersActivity extends AppCompatActivity {

    private Toolbar usersBar;
    private RecyclerView RecyclerUsersList;
    private DatabaseReference usersDatabase, friendsDatabase;
    private FirebaseAuth mAuth;
    private String userId;
    private List<UserDataModel> usersDataList;
    private UserDataRecycleAdapter myAdapter;
    private UserDataRecycleAdapter.RecyclerViewListener itemListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);

        usersBar = findViewById(R.id.allUsersBar);
        setSupportActionBar(usersBar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerUsersList = findViewById(R.id.allUsersList);
        RecyclerUsersList.setHasFixedSize(true);
        RecyclerUsersList.setLayoutManager(new LinearLayoutManager(this));

        usersDataList = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        usersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        friendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(userId);

        myAdapter= new UserDataRecycleAdapter(getApplicationContext(), usersDataList);
        itemListener = new UserDataRecycleAdapter.RecyclerViewListener() {
            @Override
            public void onClick(View v, int position) { onItemViewClick(position); }
        };
        myAdapter.setListener(itemListener);
        

        usersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersDataList.clear();
                for(final DataSnapshot dataSnapshot : snapshot.getChildren()){
                    final UserDataModel user = dataSnapshot.getValue(UserDataModel.class);
                    if(!dataSnapshot.getKey().equals(userId)){
                            friendsDatabase.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(!snapshot.hasChild(dataSnapshot.getKey())){
                                        user.setID(dataSnapshot.getKey());
                                        Log.i("User",String.valueOf(user.name));
                                        usersDataList.add(user);
                                        RecyclerUsersList.setAdapter(myAdapter);
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {}
                            });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void onItemViewClick(int position){
        Intent userProfile = new Intent(AllUsersActivity.this, UserProfileActivity.class);
        userProfile.putExtra("user_id", usersDataList.get(position).getID());
        startActivity(userProfile);
    }
}