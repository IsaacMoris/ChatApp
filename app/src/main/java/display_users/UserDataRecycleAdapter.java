package display_users;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.project.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserDataRecycleAdapter extends RecyclerView.Adapter<UserDataRecycleAdapter.UserDataViewHolder>  {

    private List<UserDataModel> userData;
    private LayoutInflater Inflater;
    private RecyclerViewListener listener;
    private RecyclerLongClickListener longClickListener;

    // data is passed into the constructor
    public UserDataRecycleAdapter(Context context, List<UserDataModel> data) {
        this.Inflater = LayoutInflater.from(context);
        this.userData = data;
    }

    @NonNull
    @Override
    public UserDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = Inflater.inflate(R.layout.single_user_layout, parent, false);
        return new UserDataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserDataViewHolder holder, int position) {
        holder.name.setText(userData.get(position).getName());
        holder.status.setText(userData.get(position).getStatus());
        if(!userData.get(position).getImage().equals("default"))
        {
            String image = userData.get(position).getImage();
            Picasso.get().load(image).into(holder.profileImage);
        }
    }

    @Override
    public int getItemCount() {
        return userData.size();
    }

    public void setListener(RecyclerViewListener listener){
        this.listener = listener;
    }
    public interface RecyclerViewListener{
        void onClick(View v, int position);
    }

    public void setLongClickListener(RecyclerLongClickListener longClickListener){this.longClickListener = longClickListener;}
    public interface RecyclerLongClickListener{
        void onLongClick(View v, int position);
    }


    
    public class UserDataViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        private CircleImageView profileImage;
        private TextView name, status;

        public UserDataViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.singleUserImage);
            name = itemView.findViewById(R.id.singleUserName);
            status = itemView.findViewById(R.id.singleUserStatus);
            itemView.setOnClickListener(this);
       //     itemView.setOnLongClickListener(this);
        }

        public void setProfileImage(CircleImageView profileImage) {
            this.profileImage = profileImage;
        }

        public void setName(TextView name) {
            this.name = name;
        }

        public void setStatus(TextView status) {
            this.status = status;
        }

        @Override
        public void onClick(View v) {
            listener.onClick(v, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            longClickListener.onLongClick(v, getAdapterPosition());
            return true;
        }
    }

}
