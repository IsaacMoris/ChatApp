package com.example.project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignupActivity extends AppCompatActivity {
    private TextInputLayout name,status,email , pass;
    private CircleImageView uImage;
    private static final int GalleryPick = 1;
    private Button btnSign;
    private  TextView signin;
    private FirebaseAuth iFirebaseAuth;
    private DatabaseReference userDatabase;

    private StorageReference ImageStorage;

    private  Uri resultUri ;
    private FirebaseUser current_user ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        intitalize();
        uImage = findViewById(R.id.uimage);
        uImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), GalleryPick);
            }
        });
        btnSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stEmail = email.getEditText().getText().toString();
                String stPass = pass.getEditText().getText().toString();
                 final String stName = name.getEditText().getText().toString();
                 final String stStatus = status.getEditText().getText().toString();

                if(stName.isEmpty()) {
                    name.setError("Please enter Name");
                    name.requestFocus();
                }
                else if(stStatus.isEmpty())
                {
                    status.setError("Please enter Status");
                    status.requestFocus();
                }
                else if(stEmail.isEmpty()) {
                    email.setError("Please enter Email");
                    email.requestFocus();
                }

                else if(stPass.isEmpty())
                {
                    pass.setError("Please enter Password");
                    pass.requestFocus();
                }


                else if (stEmail.isEmpty() && stPass.isEmpty()&&stStatus.isEmpty()&&stName.isEmpty())
                {
                    Toast.makeText(SignupActivity.this , "Fields are empty!" ,Toast.LENGTH_SHORT).show();
                }
                else if (!(stEmail.isEmpty() && stPass.isEmpty()&&stName.isEmpty()&&stStatus.isEmpty()))
                {
                    iFirebaseAuth.createUserWithEmailAndPassword(stEmail , stPass).addOnCompleteListener(SignupActivity.this, new OnCompleteListener<com.google.firebase.auth.AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful())
                            {
                                Toast.makeText(SignupActivity.this , "SignUp unsuccessful , Try again" ,Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                ADDtoDataBase(stName , stStatus , resultUri);

                                startActivity(new Intent(SignupActivity.this , HomeActivity.class));
                            }
                        }
                    });
                }
                else
                {
                    Toast.makeText(SignupActivity.this , "Error Occured" ,Toast.LENGTH_SHORT).show();
                }
            }
        });

        signin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(SignupActivity.this , LoginActivity.class);
                startActivity(i);
            }
        });

    }
    void ADDtoDataBase(String name , String status , Uri uriresult)
    {
        current_user=FirebaseAuth.getInstance().getCurrentUser();
        String uid=current_user.getUid() ;
        userDatabase= FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        HashMap<String , String> hashMap = new HashMap<>() ;
        hashMap.put("name" , name);
        hashMap.put("status" , status); ;
        hashMap.put("image" , "Empty") ;
        userDatabase.setValue(hashMap);

        if(!Uri.EMPTY.equals(uriresult))
        {

            StorageReference filepath = ImageStorage.child("profile_images").child(uid + "jpg");

            filepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful()) ;

                    Uri downloadUrl = uriTask.getResult();
                    final String sdownloadUrl = String.valueOf(downloadUrl);

                    userDatabase.child("image").setValue(sdownloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SignupActivity.this, "Success Uploading.", Toast.LENGTH_LONG).show();

                            } else {
                                Toast.makeText(SignupActivity.this, "Error in uploading.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                }
            });
        }
    }
    private void intitalize()
    {
        iFirebaseAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.TextEmailAddress);
        pass = findViewById(R.id.TextPassword);
        btnSign = findViewById(R.id.buttonSign);
        signin = findViewById(R.id.textView);
        name = findViewById(R.id.TextName);
        status=findViewById(R.id.TextStatus);
        name.requestFocus();
        ImageStorage = FirebaseStorage.getInstance().getReference();
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



                 resultUri = result.getUri();
                 uImage.setImageURI(resultUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}