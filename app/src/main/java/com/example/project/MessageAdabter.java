package com.example.project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;



public class MessageAdabter extends RecyclerView.Adapter<MessageAdabter.MessageViewHolder>{


    public List<Message> mMessageList;
    private DatabaseReference mUserDatabase;
    int chat_item_left=0,chat_item_right=1;

    public MessageAdabter(List<Message> mMessageList) {

        this.mMessageList = mMessageList;

    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v;
        if(viewType==chat_item_left)
            v= LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_left ,parent, false);
        else
            v= LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_right ,parent, false);

        return new MessageViewHolder(v);

    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText;
        public CircleImageView profileImage;
        public TextView displayName;
        public ImageView messageImage;

        public MessageViewHolder(View view) {
            super(view);

            messageText = (TextView) view.findViewById(R.id.show_message);
            profileImage = (CircleImageView) view.findViewById(R.id.profileimage);
            //displayName = (TextView) view.findViewById(R.id.name_text_layout);messageImage = (CircleImageView) view.findViewById(R.id.message_image_layout);

        }
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i) {

        Message c = mMessageList.get(i);

        String from_user = c.getFrom();
        String message_type = c.getType();


        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(from_user);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //String name = dataSnapshot.child("userName").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                //  viewHolder.displayName.setText(name);
                if(!image.equals("Empty"))


               Picasso.get().load(image).into(viewHolder.profileImage);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if(message_type.equals("text")) {

            viewHolder.messageText.setText(c.getMessage());
            //viewHolder.messageImage.setVisibility(View.INVISIBLE);


        } else {

            viewHolder.messageText.setVisibility(View.INVISIBLE);
         //   Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/chat-app-c30a9.appspot.com/o/profile_images%2FZsvk9pDqcLaMHABn4NerLGy5RaW2jpg?alt=media&token=31057fd9-a046-43b8-9c9f-e249927b4010").into(profileImage);


        }

    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }
    @Override
    public int getItemViewType(int position){
        if(mMessageList.get(position).getFrom().equals(activity_chat.mCurrentUserId)){
            return chat_item_right;

        }else return chat_item_left;
    }




}
