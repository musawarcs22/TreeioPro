package com.example.treeiopro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    private Spinner spinner;
    private EditText phoneNumber;
    private TextView signUpClick;
    private Button btnContinue;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        spinner = findViewById(R.id.spinnerCountries);
        spinner.setAdapter(new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item, CountryData.countryNames));
        phoneNumber = findViewById(R.id.et_phone_no_L);

        // When sign Up is clicked
        signUpClick = findViewById(R.id.dontAccount);
        signUpClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this, SignUp.class);
                startActivity(intent);
                finish();
            }
        });
        // When Continue is clicked, Make Progress bar visible
        btnContinue = findViewById(R.id.btnCountineL);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar = findViewById(R.id.progressBarLogin);
                progressBar.setVisibility(View.VISIBLE);
            }
        });

        findViewById(R.id.btnCountineL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = CountryData.codes[spinner.getSelectedItemPosition()];
                String number = phoneNumber.getText().toString().trim();
                if(number.isEmpty()){
                    phoneNumber.setError("Number is Required");
                    phoneNumber.requestFocus();
                    return;
                }
                String phoneNumber = code + number;
                Intent intent = new Intent(LoginActivity.this, LoginVerificationActivity.class);
                intent.putExtra("phonenumber",phoneNumber);
                startActivity(intent);
                finish();
            }
        });
    }
}