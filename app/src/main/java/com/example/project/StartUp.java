package com.example.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

public class StartUp extends AppCompatActivity {
   Button SignIn,SignUp;
   ImageView Clouds;
   Animation Sign,Blink,Alpha;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up);
        SignIn=(Button)findViewById(R.id.SignIn);
        SignUp=(Button)findViewById(R.id.SignUp);
        Clouds=(ImageView)findViewById(R.id.clouds);
        Sign= AnimationUtils.loadAnimation(this,R.anim.sign);
        Blink= AnimationUtils.loadAnimation(this,R.anim.bounce);


        SignIn.setAnimation(Sign);
        SignUp.setAnimation(Sign);
        Clouds.setAnimation(Blink);

        SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Alpha(SignIn);
                Intent intToSignIn = new Intent(StartUp.this,LoginActivity.class);
                startActivity(intToSignIn);
            }
        });
        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Alpha(SignUp);
                Intent intToSignUp = new Intent(StartUp.this,SignupActivity.class);
                startActivity(intToSignUp);
            }
        });
    }
    public void Alpha (Button bttn){
        Alpha= new AlphaAnimation(1.0f,0.7f);
        Alpha.setDuration(800);
        Alpha.setRepeatCount(1);
        Alpha.setRepeatMode(Animation.REVERSE);
        bttn.startAnimation(Alpha);
    }
}