package com.example.treeiopro;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class LoginIntro extends AppCompatActivity {

    Button buttonGetStarted;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_intro);


        //Checking if user is already loggedin//
        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser()!=null){
            Toast.makeText(this,"User is already Logged in!", Toast.LENGTH_SHORT).show();
            try {
                redirect("MAIN");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //When User clicks get started button//
        buttonGetStarted = findViewById(R.id.btnGetStarted);
        buttonGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirect("LOGIN");
            }
        });
    }

    private void redirect(String name) {
        Intent intent = null;
        if (name == "LOGIN"){
            intent = new Intent(LoginIntro.this, LoginActivity.class);
        }
        if (name == "MAIN"){
            intent = new Intent(LoginIntro.this, MainActivity.class);
        }
        else{
            try {
                throw new Exception("no path Exists");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        startActivity(intent);
        finish();
    }
}

