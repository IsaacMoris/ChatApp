package fragment_package;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.project.Message;
import com.example.project.R;
import com.example.project.activity_chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import display_users.UserDataModel;
import display_users.UserDataRecycleAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends FragmentControl {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView chatFriendsList;
    private DatabaseReference chatDatabase, userDatabase;
    private List<UserDataModel> usersDataList;

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
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_chat, container, false);
        // Inflate the layout for this fragment
        Initialize();
        retrieveData();
        return mainView;
    }

    @Override
    protected void Initialize() {
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        chatFriendsList = mainView.findViewById(R.id.chatViewList);
        chatFriendsList.setLayoutManager(new LinearLayoutManager(getActivity()));

        chatDatabase = FirebaseDatabase.getInstance().getReference().child("messages").child(userID);
        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        usersDataList = new ArrayList<>();

        myAdapter= new UserDataRecycleAdapter(getContext(), usersDataList);
        itemListener = new UserDataRecycleAdapter.RecyclerViewListener() {
            @Override
            public void onClick(View v, int position) { onItemClick(v, position); }
        };
        myAdapter.setListener(itemListener);
    }

    @Override
    protected void retrieveData() {
        chatDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Clearing List for not repeating
                usersDataList.clear();

                for(final DataSnapshot dataSnapshot: snapshot.getChildren()){
                    userDatabase.child(dataSnapshot.getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            usersDataList.clear();
                            //Get User Data
                            final UserDataModel user = snapshot.getValue(UserDataModel.class);
                            user.setID(snapshot.getKey());

                            //Get Last Message between this user and its contacts
                            DatabaseReference reference = dataSnapshot.getRef();
                            Query lastMessage = reference.orderByKey().limitToLast(1);
                            lastMessage.addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                                    Message message = snapshot.getValue(Message.class);
                                    user.setDate(String.valueOf(message.getTime()) );

                                    if(message.getFrom().equals(userID))
                                        user.setStatus("You: "+ message.getMessage());
                                    else
                                        user.setStatus(message.getMessage());

                                    usersDataList.add(user);
                                    //sort User based on chat time
                                    Collections.sort(usersDataList);
                                    Collections.reverse(usersDataList);

                                    chatFriendsList.setAdapter(myAdapter);
                                }
                                @Override
                                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
                                @Override
                                public void onChildRemoved(@NonNull DataSnapshot snapshot) {}
                                @Override
                                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {}
                            });
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    @Override
    protected void onItemClick(View v, int position){
        goToActivity(usersDataList.get(position).getID(), new activity_chat());
    }
}