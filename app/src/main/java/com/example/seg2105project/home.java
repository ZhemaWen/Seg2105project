package com.example.seg2105project;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class home extends AppCompatActivity {

    private Button logoutButton;
    FirebaseAuth auth;
    FirebaseUser user;
    TextView textView;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        textView = findViewById(R.id.textView);
        logoutButton = findViewById(R.id.logoutButton);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if(user == null){
            Intent intent = new Intent(home.this, LogIn.class);
            startActivity(intent);
            finish();
        }
        else{
            textView.setText("Welcome "+user.getEmail());
        }

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle logout button click
                logout();
            }
        });
    }

    private void logout() {
        // Perform logout operation here
        FirebaseAuth.getInstance().signOut();

        //For example, you can redirect to the login activity
        Intent intent = new Intent(home.this, LogIn.class);
        startActivity(intent);
        finish(); // Optional: If you want to finish the current activity after logout
    }
}
