package com.example.seg2105project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class adminHome extends AppCompatActivity {

    private Button logoutButton;
    FirebaseAuth auth;
    FirebaseUser user;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        textView = findViewById(R.id.textView);
        logoutButton = findViewById(R.id.logoutButton);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if(user == null){
            Intent intent = new Intent(adminHome.this, LogIn.class);
            startActivity(intent);
            finish();
        }
        else{
            textView.setText("Welcome admin");
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
        Intent intent = new Intent(adminHome.this, LogIn.class);
        startActivity(intent);
        finish(); // Optional: If you want to finish the current activity after logout
    }
}
