package fragment_package;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.project.R;
import com.example.project.activity_chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import display_users.UserDataModel;
import display_users.UserDataRecycleAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FriendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView friendsViewList;
    private DatabaseReference FriendsDatabase, userDatabase;
    private FirebaseAuth mAuth;
    private View mainView;
    private String userID;
    private List<UserDataModel> usersDataList;
    private UserDataRecycleAdapter myAdapter;
    private UserDataRecycleAdapter.RecyclerViewListener itemListener;

    public FriendsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FriendsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendsFragment newInstance(String param1, String param2) {
        FriendsFragment fragment = new FriendsFragment();
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
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_friends, container, false);

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        friendsViewList = mainView.findViewById(R.id.friendsViewList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        friendsViewList.setLayoutManager(layoutManager);

        FriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(userID);
        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        usersDataList = new ArrayList<>();

        myAdapter= new UserDataRecycleAdapter(getContext(), usersDataList);
        itemListener = new UserDataRecycleAdapter.RecyclerViewListener() {
            @Override
            public void onClick(int position) { onItemViewClick(position); }
        };
        myAdapter.setListener(itemListener);

        FriendsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot friendSnapshot) {
                usersDataList.clear();

                for(final DataSnapshot dataSnapshot: friendSnapshot.getChildren()){
                    userDatabase.child(dataSnapshot.getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            UserDataModel user = snapshot.getValue(UserDataModel.class);
                            user.setID(snapshot.getKey());
                            user.setStatus(dataSnapshot.child("date").getValue().toString());
                            usersDataList.add(user);
                            friendsViewList.setAdapter(myAdapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        return mainView;
    }

    private void onItemViewClick(int position) {
        Intent chatActivity = new Intent(getContext(), activity_chat.class);
        chatActivity.putExtra("user_id", usersDataList.get(position).getID());
        startActivity(chatActivity);
    }
}