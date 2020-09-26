package com.example.project;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import fragment_package.FriendsFragment;

public class activity_chat extends AppCompatActivity {
    static String mChatuser,mchatUserName,mCurrentUserId;
    Toolbar mChatToolBar;
    private DatabaseReference mRootRef;
    TextView Title_view,Last_seen_view;
    EditText chat_message_view;
    ImageButton send_btn;
    CircleImageView profileImage;
    List<Message> messageList;
    RecyclerView ViewMessageList;
    LinearLayoutManager linearLayoutManager;
    MessageAdabter messageAdabter;
    FirebaseAuth firebaseAuth;
    ImageButton back_button;
    String sourceActivity;

    public static String getmChatuser() {
        return mChatuser;
    }

    public static void setmChatuser(String mChatuser) {
        activity_chat.mChatuser = mChatuser;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //mChatToolBar= findViewById(R.id.Custom_bar);
        Intent intent=getIntent();


       messageList=new ArrayList<>();
        ViewMessageList=(RecyclerView) findViewById(R.id.messages_list);
        linearLayoutManager=new LinearLayoutManager(this);
        ViewMessageList.setHasFixedSize(true);
        ViewMessageList.setLayoutManager(linearLayoutManager);


        mChatuser=intent.getStringExtra("user_id");
        firebaseAuth=FirebaseAuth.getInstance();
        mCurrentUserId=firebaseAuth.getUid();
        sourceActivity=intent.getStringExtra("source");

        // writing and sending messages
        chat_message_view=(EditText) findViewById(R.id.chat_message_view);
        send_btn=(ImageButton)findViewById(R.id.chat_send_btn);

        //custom bar stuff
        Title_view=(TextView) findViewById(R.id.custom_bar_title);
        Last_seen_view=(TextView) findViewById(R.id.custom_bar_seen);
        profileImage=(CircleImageView) findViewById(R.id.custom_bar_image);
        back_button=findViewById(R.id.back_button);

        mRootRef = FirebaseDatabase.getInstance().getReference();
        messageAdabter=new MessageAdabter(messageList);
        ViewMessageList.setAdapter(messageAdabter);
        //LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        loadMessages();
        if(messageList.size()==0){
            mRootRef.child("Chat").child(mChatuser).child(mCurrentUserId).child("seen").setValue(false);
            mRootRef.child("Chat").child(mChatuser).child(mCurrentUserId).child("timestamp").setValue(-1);

        }
        //last seen
        mRootRef.child("Chat").child(mCurrentUserId).child(mChatuser).child("seen").setValue(true);
        mRootRef.child("Chat").child(mCurrentUserId).child(mChatuser).child("timestamp").setValue(ServerValue.TIMESTAMP);

        mRootRef.child("Chat").child(mChatuser).child(mCurrentUserId).child("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                long time=snapshot.getValue(long.class);
                if(time==-1){
                    Last_seen_view.setText("Have not opened this chat before");
                }else
                Last_seen_view.setText(time_ago.getTimeAgo(time));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

loadUserData(mChatuser);
       send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               sendMessage();
                //userNameText.setText("");
            }
        });
       /*back_button.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent intent;
               if(sourceActivity.equals("FriendsFragment"))
               intent=new Intent(activity_chat.this, FriendsFragment.class);
               else   intent=new Intent(activity_chat.this,ChatFragment.class);
               startActivity(intent);
           }
       });*/

    }

    private void loadMessages() {
        mRootRef.child("messages").child(mCurrentUserId).child(mChatuser).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message m=snapshot.getValue(Message.class);
                if(m==null)return;

                messageList.add(m);
                messageAdabter.notifyDataSetChanged();
                ViewMessageList.smoothScrollToPosition(ViewMessageList.getAdapter().getItemCount()-1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendMessage() {


        String message = chat_message_view.getText().toString();

        if(!TextUtils.isEmpty(message)){

            String current_user_ref = "messages/" + mCurrentUserId + "/" + mChatuser;
            String chat_user_ref = "messages/" + mChatuser + "/" + mCurrentUserId;

            DatabaseReference user_message_push = mRootRef.child("messages")
                    .child(mCurrentUserId).child(mChatuser).push();

            String push_id = user_message_push.getKey();

            Map<String,Object> messageMap = new HashMap<>();
            messageMap.put("message", message);
            messageMap.put("seen", false);
            messageMap.put("type", "text");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", mCurrentUserId);

            Map<String,Object> messageUserMap = new HashMap();
            messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
            messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

            chat_message_view.setText("");


            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    if(databaseError != null){

                        Log.d("CHAT_LOG", databaseError.getMessage().toString());

                    }

                }
            });


        }

    }
    public void loadUserData(String userid){
      mRootRef.child("Users").child(userid).addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot snapshot) {
              String image=snapshot.child("image").getValue(String.class);
              String name=snapshot.child("name").getValue(String.class);
              if(!image.equals("Empty"))
              Picasso.get().load(image).into(profileImage);
              Title_view.setText(name);

          }

          @Override
          public void onCancelled(@NonNull DatabaseError error) {

          }
      });
    }
}
