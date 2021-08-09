package com.example.treeiopro;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

public class LoginIntro extends AppCompatActivity {

    Button buttonGetStarted;
    Switch btnSwitch;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_intro);
        //When User clicks switch button to change the language//
        btnSwitch = findViewById(R.id.btnSwitchLanguage);

        Locale current = getResources().getConfiguration().locale;
        String currentLocale = String.valueOf(current);
        Log.i("LOCAL", "onCreate: "+currentLocale);
        if(currentLocale.equals("ur")) {
            btnSwitch.setText("English");
        }
        else{
            btnSwitch.setText("اردو");
        }

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





        btnSwitch.setOnClickListener(new View.OnClickListener() {
            Locale current = getResources().getConfiguration().locale;
            String currentLocale = String.valueOf(current);
        //Log.i("LOCAL", "onCreate: "+current);

            @Override
            public void onClick(View v) {

                if(currentLocale.equals("ur")) {
                    setAppLocale("eu");
                }
                else{
                    setAppLocale("ur");
                }
            }
        });

    }

    private void setAppLocale(String ur) {
        Locale locale = new Locale(ur);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        Toast.makeText(LoginIntro.this, getResources().getString(R.string.lbl_langSelectEnglis), Toast.LENGTH_SHORT).show();


        Intent intent = new Intent(LoginIntro.this, LoginIntro.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);


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

