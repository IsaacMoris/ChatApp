package com.example.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;

public class UserProfileActivity extends AppCompatActivity {

    private ImageView mProfileImg;
    private TextView mProfileName, mProfileStatus, mProfileCount;
    private Button mSendRequestBtn;

    private int mRelationState; // 0 is not friends
                                // 1 sent req
                                // 2 received req
                                // 3 is friends


    private ProgressDialog mProgressDialog;

    private DatabaseReference mUsersDB, mFriendReqDB, mFriendsDB;
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        final String userID = getIntent().getStringExtra("userID");

        mUsersDB = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        mFriendReqDB = FirebaseDatabase.getInstance().getReference().child("Friend_Request");
        mFriendsDB = FirebaseDatabase.getInstance().getReference().child("Friends");

        mProfileImg = (ImageView)findViewById(R.id.profileImg);
        mProfileName = (TextView)findViewById(R.id.profileName);
        mProfileStatus = (TextView)findViewById(R.id.profileStatus);
        mProfileCount = (TextView)findViewById(R.id.totalFriends);
        mSendRequestBtn = (Button)findViewById(R.id.sendRequestBtn);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Loading User Data");
        mProgressDialog.setMessage("Please wait until loading finishes");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        mUsersDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue().toString();
                String status = snapshot.child("status").getValue().toString();

                mProfileName.setText(name);
                mProfileStatus.setText(status);

                mFriendReqDB.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChild(userID)){
                            String Request_Type = snapshot.child(userID).child("Request_Type").getValue().toString();
                            if(Request_Type.equals("sent")){
                                mRelationState = 1;
                                mSendRequestBtn.setText("Cancel Friend Request");
                            } else if(Request_Type.equals("received")){
                                mRelationState = 2;
                                mSendRequestBtn.setText("Accept Friend Request");
                            }
                        }

                        mProgressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mSendRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSendRequestBtn.setEnabled(false);

                switch (mRelationState){
                    case 0: // Send Request -------------------------->
                        mFriendReqDB.child(mCurrentUser.getUid()).child(userID).child("Request_Type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    mFriendReqDB.child(userID).child(mCurrentUser.getUid()).child("Request_Type").setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(UserProfileActivity.this, "Friend request sent successfully", Toast.LENGTH_SHORT);

                                            mRelationState = 1;
                                            mSendRequestBtn.setText("Cancel Friend Request");
                                            //mSendRequestBtn.setEnabled(true);
                                        }
                                    });
                                }else{
                                    Toast.makeText(UserProfileActivity.this, "Failed to send friend request", Toast.LENGTH_SHORT);
                                }
                            }
                        });
                        break;
                    case 1: // Cancel Request -------------------------->
                        mFriendReqDB.child(mCurrentUser.getUid()).child(userID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    mFriendReqDB.child(userID).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(UserProfileActivity.this, "Friend request cancelled successfully", Toast.LENGTH_SHORT);

                                            mRelationState = 0;
                                            mSendRequestBtn.setText("Send Friend Request");
                                            //mSendRequestBtn.setEnabled(true);
                                        }
                                    });
                                }else{
                                    Toast.makeText(UserProfileActivity.this, "Failed to cancel friend request", Toast.LENGTH_SHORT);
                                }
                            }
                        });
                        break;
                    case 2: // Accept/Reject Request -------------------------->
                        final String curDate = DateFormat.getDateTimeInstance().format(new Date());

                        mFriendsDB.child(mCurrentUser.getUid()).child(userID).setValue(curDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    mFriendsDB.child(userID).child(mCurrentUser.getUid()).setValue(curDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //Remove The Request
                                            mFriendReqDB.child(mCurrentUser.getUid()).child(userID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        mFriendReqDB.child(userID).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Toast.makeText(UserProfileActivity.this, "Friend request accepted successfully", Toast.LENGTH_SHORT);

                                                                mRelationState = 3;
                                                                mSendRequestBtn.setText("Unfriend");
                                                                //mSendRequestBtn.setEnabled(true);
                                                            }
                                                        });
                                                    }else{
                                                        Toast.makeText(UserProfileActivity.this, "Failed to accept friend request", Toast.LENGTH_SHORT);
                                                    }
                                                }
                                            });
                                        }
                                    });
                                }else{
                                    Toast.makeText(UserProfileActivity.this, "Failed to accept friend request", Toast.LENGTH_SHORT);
                                }
                            }
                        });
                        break;
                }
                mSendRequestBtn.setEnabled(true);
            }
        });
    }
}