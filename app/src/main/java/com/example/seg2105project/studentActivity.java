package com.example.seg2105project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class studentActivity extends AppCompatActivity {

    private Button logoutButton;
    private TextView textView;
    private FirebaseAuth auth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        textView = findViewById(R.id.textView);
        logoutButton = findViewById(R.id.logoutButton);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user == null) {
            Intent intent = new Intent(studentActivity.this, LogIn.class);
            startActivity(intent);
            finish();
        } else {
            textView.setText("Welcome student");
        }

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(studentActivity.this, LogIn.class);
        startActivity(intent);
        finish();
    }
}
