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
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;

public class UserProfileActivity extends AppCompatActivity {

    private ImageView mProfileImg;
    private TextView mProfileName, mProfileStatus, mProfileCount;
    private Button mSendRequestBtn, mRejectRequestBtn;

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
        mRejectRequestBtn = (Button)findViewById(R.id.rejectRequestBtn);
        mRejectRequestBtn.setVisibility(View.INVISIBLE);
        mProfileImg = (ImageView)findViewById(R.id.profileImg);

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
                Picasso.get().load((String)snapshot.child("image").getValue()).into(mProfileImg);

                mFriendReqDB.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChild(userID)){
                            String Request_Type = snapshot.child(userID).child("request_type").getValue().toString();
                            if(Request_Type.equals("sent")){
                                mRelationState = 2;
                                mSendRequestBtn.setText("Accept Friend Request");
                                mRejectRequestBtn.setVisibility(View.VISIBLE);
                            } else if(Request_Type.equals("received")){
                                mRelationState = 1;
                                mSendRequestBtn.setText("Cancel Friend Request");
                            }
                        } else {
                            mFriendsDB.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.hasChild(userID)){
                                        mRelationState = 3;
                                        mSendRequestBtn.setText("Unfriend");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
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

        mRejectRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                                }
                            });
                        }else{
                            Toast.makeText(UserProfileActivity.this, "Failed to cancel friend request", Toast.LENGTH_SHORT);
                        }
                    }
                });
            }
        });

        mSendRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSendRequestBtn.setEnabled(false);

                switch (mRelationState){
                    case 0: // Send Request -------------------------->
                        mFriendReqDB.child(mCurrentUser.getUid()).child(userID).child("request_type").setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    mFriendReqDB.child(userID).child(mCurrentUser.getUid()).child("request_type").setValue("sent").addOnSuccessListener(new OnSuccessListener<Void>() {
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
                    case 2: // Accept Request -------------------------->
                        final String curDate = DateFormat.getDateTimeInstance().format(new Date());

                        mFriendsDB.child(mCurrentUser.getUid()).child(userID).child("date").setValue(curDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    mFriendsDB.child(userID).child(mCurrentUser.getUid()).child("date").setValue(curDate).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                    case 3:
                        mFriendsDB.child(mCurrentUser.getUid()).child(userID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    mFriendsDB.child(userID).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(UserProfileActivity.this, "Friend removed successfully", Toast.LENGTH_SHORT);

                                            mRelationState = 0;
                                            mSendRequestBtn.setText("Send Friend Request");
                                            //mSendRequestBtn.setEnabled(true);
                                        }
                                    });
                                }else{
                                    Toast.makeText(UserProfileActivity.this, "Failed to remove friend", Toast.LENGTH_SHORT);
                                }
                            }
                        });
                }
                mSendRequestBtn.setEnabled(true);
                mRejectRequestBtn.setVisibility(View.INVISIBLE);
            }
        });
    }
}