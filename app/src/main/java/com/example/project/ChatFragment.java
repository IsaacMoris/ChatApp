package com.example.project;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView chatFriendsList;
    private DatabaseReference chatFriendsDatabase, userDatabase;
    private FirebaseAuth mAuth;
    private View mainView;
    private String userID;

    public ChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_chat, container, false);

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        chatFriendsList = mainView.findViewById(R.id.chatViewList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        chatFriendsList.setLayoutManager(layoutManager);

        chatFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("messages").child(userID);
        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        // Inflate the layout for this fragment
        return mainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(chatFriendsDatabase, User.class).build();

        FirebaseRecyclerAdapter<User, UserViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<User, UserViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final UserViewHolder holder, int position, @NonNull User model) {

                        final String friendsIDs = getRef(position).getKey();
                        userDatabase.child(friendsIDs).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                final String name = snapshot.child("name").getValue().toString();
                                final String status = snapshot.child("status").getValue().toString();

                                if(snapshot.child("image").getValue()!="default")
                                {
                                    String image = snapshot.child("image").getValue().toString();
                                    Picasso.get().load(image).into(holder.profileImage);
                                }
                                    holder.name.setText(name);
                                    holder.status.setText("");
                                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent friendChat = new Intent(getActivity(), activity_chat.class);
                                                friendChat.putExtra("user_id", friendsIDs);
                                                startActivity(friendChat);
                                            }
                                        });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_user_layout, parent, false);
                        return  new UserViewHolder(view);
                    }

                };
        chatFriendsList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }
}