package com.example.project;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private static final int GalleryPick = 1;
    private DatabaseReference muserDatabase;
    private FirebaseUser currentUser;
    private TextView uName;
    private TextView uStatus;
    private CircleImageView uImage;

    private BottomSheetDialog DialogEdit;
    private StorageReference ImageStorage;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        this.setTitle("Profile");

        InitializeAttributes();
        getinfo();

        inActionListner();

    }

    private void InitializeAttributes()   // Initialize Settings Attributes
    {
        uName = findViewById(R.id.profile_uname);
        uStatus = findViewById(R.id.profile_ustatus);
        uImage = findViewById(R.id.profile_uimage);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = currentUser.getUid();
        muserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        ImageStorage = FirebaseStorage.getInstance().getReference();

    }

    private void inActionListner() {
        findViewById(R.id.profile_lnedit_name).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomEditName();
            }
        });

        findViewById(R.id.profile_lnedit_status).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomEditStatus();
            }
        });

        findViewById(R.id.profile_uimage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), GalleryPick);
             /*   CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(ProfileActivity.this);*/
            }
        });
    }

    private void getinfo()   // Get user info from FireBase
    {
        muserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String status = (String) dataSnapshot.child("status").getValue();
                String image = (String) dataSnapshot.child("image").getValue();
                uName.setText(name);
                uStatus.setText(status);
                Picasso.get().load(image).into(uImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showBottomEditName() {
        View view = getLayoutInflater().inflate(R.layout.bottomsheet_editname, null);

        view.findViewById(R.id.bottom_editname_cancel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogEdit.dismiss();
            }
        });

        final EditText uname = view.findViewById(R.id.bottom_editname_username);
        view.findViewById(R.id.bottom_editname_save_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(uname.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Name can't be empty", Toast.LENGTH_SHORT).show();
                } else {
                    updateName(uname.getText().toString());
                    DialogEdit.dismiss();
                }
            }
        });

        DialogEdit = new BottomSheetDialog(this);
        DialogEdit.setContentView(view);

        DialogEdit.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                DialogEdit = null;
            }
        });
        DialogEdit.show();
    }

    private void showBottomEditStatus() {
        View view = getLayoutInflater().inflate(R.layout.bottomsheet_editstatus, null);

        view.findViewById(R.id.bottom_editstatus_cancel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogEdit.dismiss();
            }
        });

        final EditText ustatus = view.findViewById(R.id.bottom_editstatus_userstatus);
        view.findViewById(R.id.bottom_editstatus_save_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(ustatus.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Status can't be empty", Toast.LENGTH_SHORT).show();
                } else {
                    updateStatus(ustatus.getText().toString());
                    DialogEdit.dismiss();
                }
            }
        });

        DialogEdit = new BottomSheetDialog(this);
        DialogEdit.setContentView(view);

        DialogEdit.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                DialogEdit = null;
            }
        });
        DialogEdit.show();
    }

    private void showBottomPickPhoto() {
        View view = getLayoutInflater().inflate(R.layout.bottomsheet_editphoto, null);

        view.findViewById(R.id.bottomsheet_photo_ln_gallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
                DialogEdit.dismiss();
            }
        });
        DialogEdit = new BottomSheetDialog(this);
        DialogEdit.setContentView(view);

        DialogEdit.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                DialogEdit = null;
            }
        });
        DialogEdit.show();
    }


    private void updateName(String uName) {
        muserDatabase.child("name").setValue(uName).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Update Successful", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateStatus(String suStatus) {
        muserDatabase.child("status").setValue(suStatus).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Update Successful", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openGallery() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {


        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GalleryPick && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();

            CropImage.activity(imageUri).setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                progressDialog = new ProgressDialog(ProfileActivity.this);
                progressDialog.setTitle("Uloading Image...");
                progressDialog.setMessage("Please Wait  while we upload and process the image.");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                Uri resultUri = result.getUri();

                String current_user_id = currentUser.getUid();
                StorageReference filepath = ImageStorage.child("profile_images").child(current_user_id + "jpg");


                filepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()) ;

                        Uri downloadUrl = uriTask.getResult();
                        final String sdownloadUrl = String.valueOf(downloadUrl);

                        muserDatabase.child("image").setValue(sdownloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    progressDialog.dismiss();
                                    Toast.makeText(ProfileActivity.this, "Success Uploading.", Toast.LENGTH_LONG).show();

                                } else {
                                    Toast.makeText(ProfileActivity.this, "Error in uploading.", Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                }
                            }
                        });

                    }
                });


             /*   filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String download_Url = uri.toString();

                        muserDatabase.child("image").setValue(download_Url).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    progressDialog.dismiss();
                                    Toast.makeText(ProfileActivity.this, "Success Uploading.", Toast.LENGTH_LONG).show();

                                } else {
                                    Toast.makeText(ProfileActivity.this, "Error in uploading.", Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                }
                            }
                        });
                    }
                });*/

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}