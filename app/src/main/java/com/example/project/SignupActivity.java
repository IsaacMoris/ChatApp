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

public class SignupActivity extends AppCompatActivity {
    EditText email , pass;
    Button btnSign;
    TextView signin;
    FirebaseAuth iFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        iFirebaseAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.TextEmailAddress);
        pass = findViewById(R.id.TextPassword);
        btnSign = findViewById(R.id.buttonSign);
        signin = findViewById(R.id.textView);

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
}