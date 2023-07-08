package com.example.seg2105project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LogIn extends AppCompatActivity {
    private EditText userNameET;
    private EditText passwordET;
    private Button loginButton;
    FirebaseAuth mAuth;
    private DatabaseReference userTypeRef;


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            FirebaseAuth.getInstance().signOut();
        }
    }
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        mAuth = FirebaseAuth.getInstance();
        userNameET = findViewById(R.id.userNameET);
        passwordET = findViewById(R.id.passwordET);
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = userNameET.getText().toString().trim();
                String password = passwordET.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(LogIn.this, "Please enter your email address", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(LogIn.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(LogIn.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(LogIn.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }


                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(LogIn.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                    if (isValidAdmin(email, password)) {
                                        // Successful login as admin
                                        Intent intent = new Intent(LogIn.this, adminHome.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        if (user != null) {
                                            userTypeRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("userType");

                                            userTypeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    String userType = dataSnapshot.getValue(String.class);
                                                    if (userType != null) {
                                                        if (userType.equals("Student")) {
                                                            // Successful login as student
                                                            Intent intent = new Intent(LogIn.this, StudentHome.class);
                                                            startActivity(intent);
                                                            finish();
                                                        } else  {

                                                            // Successful
                                                            Intent intent = new Intent(LogIn.this, home.class);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                    // Handle error
                                                }
                                            });
                                        }
                                    }
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(LogIn.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
    boolean isValidAdmin(String userName, String password) {
        //admin
        String adminEmail = "admin@4code.com";
        String adminPassword = "4code4code";

        if (userName.equals(adminEmail) && password.equals(adminPassword)) {
            // Admin login
            return true;
        }
        return false;
    }


}