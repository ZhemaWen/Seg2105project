package com.example.seg2105project;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LogIn extends AppCompatActivity {
    private EditText userNameET;
    private EditText passwordET;
    private Button loginButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        userNameET = findViewById(R.id.userNameET);
        passwordET = findViewById(R.id.passwordET);
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = userNameET.getText().toString().trim();
                String password = passwordET.getText().toString().trim();


                if (isValid(email, password)) {
                    // Successful login
                    Toast.makeText(LogIn.this, "Login Successful", Toast.LENGTH_SHORT).show();
                } else {
                    // Invalid credentials
                    Toast.makeText(LogIn.this, "Invalid Input", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private boolean isValid(String userName, String password) {
        //admin
        String adminEmail = "admin";
        String adminPassword = "4code";

        if (userName.equals(adminEmail) && password.equals(adminPassword)) {
            // Admin login
            return true;
        }
        return false;
    }


}