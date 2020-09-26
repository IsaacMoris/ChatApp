package fragment_package;

import android.content.Intent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

import display_users.UserDataRecycleAdapter;

public abstract class FragmentControl extends Fragment {

    protected FirebaseAuth mAuth;
    protected String userID;
    protected View mainView;
    protected UserDataRecycleAdapter myAdapter;
    protected UserDataRecycleAdapter.RecyclerViewListener itemListener;
    protected UserDataRecycleAdapter.RecyclerLongClickListener longClickListener;
    protected int clickedItemPosition;

    protected abstract void Initialize();
    protected abstract void retrieveData();
    protected void onItemClick(int position){};
    protected void onItemLongClick(View v , int position){}
    protected void goToActivity(String ID, AppCompatActivity Destination){
        Intent newActivity = new Intent(getContext(), Destination.getClass());
        newActivity.putExtra("user_id", ID);
        startActivity(newActivity);
    }
}
