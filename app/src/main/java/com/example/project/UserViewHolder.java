package com.example.project;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserViewHolder extends RecyclerView.ViewHolder {
    CircleImageView profileImage;
    TextView name, status;

    public UserViewHolder(@NonNull View itemView) {
        super(itemView);

        profileImage = itemView.findViewById(R.id.singleUserImage);
        name = itemView.findViewById(R.id.singleUserName);
        status = itemView.findViewById(R.id.singleUserStatus);
    }
}
