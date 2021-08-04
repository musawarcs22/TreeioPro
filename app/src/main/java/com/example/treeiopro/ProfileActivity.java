package com.example.treeiopro;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class ProfileActivity extends AppCompatActivity {


    private FirebaseAuth auth;
    private TextView phoneNumber;
    private Button btnLogOut;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        phoneNumber = findViewById(R.id.txtPhoneNo);
        btnLogOut = findViewById(R.id.btnLogout);
        auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser().getPhoneNumber() != null){
            phoneNumber.setText(auth.getCurrentUser().getPhoneNumber());
        }

        btnLogOut = findViewById(R.id.btnLogout);
        btnLogOut.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}