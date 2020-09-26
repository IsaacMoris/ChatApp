package fragment_package;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class FragmentAdapter extends FragmentStateAdapter {
    public FragmentAdapter(@NonNull FragmentActivity fa) {
        super(fa);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Log.i("Data", "Fragment no: " + position);
        switch (position){
            case 0:
                return  new ChatFragment();
            case 1:
                return new FriendsFragment();
            case 2:
                return  new RequestsFragment();
            default:
                return null;
        }
    }

    /*@Override
    public CharSequence getPageTitle(int position){
        switch (position){
            case 0:
                return "Chat";
            case 1:
                return "Friends";
            case 2:
                return "Requests";
            default:
                return null;
        }
    }*/

    @Override
    public int getItemCount() {
        return 3;
    }
}
