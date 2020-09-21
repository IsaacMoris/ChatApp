package com.example.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignupActivity extends AppCompatActivity {
    private EditText email , pass;
    private Button btnSign;
    private  TextView signin;
    private FirebaseAuth iFirebaseAuth;
    private DatabaseReference userDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        intitalize();

        btnSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stEmail = email.getText().toString();
                String stPass = pass.getText().toString();

                if(stEmail.isEmpty()) {
                    email.setError("Please enter email");
                    email.requestFocus();
                }
                else if(stPass.isEmpty())
                {
                    pass.setError("Please enter password");
                    pass.requestFocus();
                }
                else if (stEmail.isEmpty() && stPass.isEmpty())
                {
                    Toast.makeText(SignupActivity.this , "Fields are empty!" ,Toast.LENGTH_SHORT).show();
                }
                else if (!(stEmail.isEmpty() && stPass.isEmpty()))
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
                                FirebaseUser current_user=FirebaseAuth.getInstance().getCurrentUser();
                                String uid=current_user.getUid() ;
                                userDatabase= FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                                HashMap<String , String> hashMap = new HashMap<>() ;
                                hashMap.put("name" , "No Name");
                                hashMap.put("status" , "Hey there , i'm using Titans chat") ;
                                hashMap.put("image" , "Empty") ;
                                userDatabase.setValue(hashMap);
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
    private void intitalize()
    {
        iFirebaseAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.TextEmailAddress);
        pass = findViewById(R.id.TextPassword);
        btnSign = findViewById(R.id.buttonSign);
        signin = findViewById(R.id.textView);
    }
}