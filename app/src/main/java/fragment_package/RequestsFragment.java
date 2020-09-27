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
import com.example.project.UserProfileActivity;
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
 * Use the {@link RequestsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RequestsFragment extends FragmentControl {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView friendRequestList;
    private DatabaseReference friendRequestsDatabase, userDatabase;
    private List<UserDataModel> requestsDataList;

    public RequestsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RequestsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RequestsFragment newInstance(String param1, String param2) {
        RequestsFragment fragment = new RequestsFragment();
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
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_requests, container, false);
        Initialize();
        retrieveData();
        return mainView;


    }

    @Override
    protected void Initialize() {
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        friendRequestList = mainView.findViewById(R.id.friendRequestList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        friendRequestList.setLayoutManager(layoutManager);

        friendRequestsDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_Request").child(userID);
        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        requestsDataList = new ArrayList<>();

        myAdapter= new UserDataRecycleAdapter(getContext(), requestsDataList);
        itemListener = new UserDataRecycleAdapter.RecyclerViewListener() {
            @Override
            public void onClick(View v, int position) { onItemClick(v, position); }
        };
        myAdapter.setListener(itemListener);
    }

    @Override
    protected void retrieveData() {
        friendRequestsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                requestsDataList.clear();
                myAdapter.notifyDataSetChanged();

                for(final DataSnapshot dataSnapshot: snapshot.getChildren()){
                    userDatabase.child(dataSnapshot.getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            UserDataModel user = snapshot.getValue(UserDataModel.class);
                            user.setID(snapshot.getKey());
                            if(dataSnapshot.child("request_type").getValue().equals("received")){
                                user.setStatus("Waiting for "+user.getName()+" to Confirm");
                                requestsDataList.add(user);
                            }
                            else{
                                user.setStatus(user.getName()+" is Waiting For You to Confirm");
                                requestsDataList.add(0,user);
                            }
                            friendRequestList.setAdapter(myAdapter);
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
        goToActivity(requestsDataList.get(position).getID(), new UserProfileActivity());
    }
}