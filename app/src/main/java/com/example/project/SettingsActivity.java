package com.example.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingsActivity extends AppCompatActivity {

    private DatabaseReference userDatabase ;
    private FirebaseUser currentUser ;

    private TextView uName;
    private TextView uStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        uName = (TextView) findViewById(R.id.settings_display_name);
        uStatus = (TextView)findViewById(R.id.settings_status);

        currentUser= FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = currentUser.getUid();

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        userDatabase =database.getReference().child("Users").child(current_uid);



        userDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String name =dataSnapshot.child("name").getValue().toString();
                   String status =(String) dataSnapshot.child("status").getValue();
             //   Log.d("TAG", "Name: " + name);
                   uName.setText(name);
                    uStatus.setText(status);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}