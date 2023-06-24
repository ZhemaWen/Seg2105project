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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class home extends AppCompatActivity {

    private Button logoutButton;
    private TextView textView,messagetextview;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference userTypeRef,suspensionEndTimeRef;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        textView = findViewById(R.id.textView);
        messagetextview=findViewById(R.id.messagetextView);
        logoutButton = findViewById(R.id.logoutButton);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if(user == null){
            Intent intent = new Intent(home.this, LogIn.class);
            startActivity(intent);
            finish();
        }
        else{
            userTypeRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("userType");
            suspensionEndTimeRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("suspensionEndTime");

            // Attach a listener to retrieve the account type value
            userTypeRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Retrieve the account type value from the dataSnapshot
                    String userType = dataSnapshot.getValue(String.class);


                    // Update the welcome message with the user type
                    textView.setText("Welcome " + user.getEmail() + ", you are logged in as a " + userType);

                    if (userType.equals("Tutor")) {
                        checkSuspensionStatus();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle any errors that occur during the database read operation
                    // You can log or display an error message as needed
                }
            });
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
    private void checkSuspensionStatus() {
        DatabaseReference suspensionRef = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(user.getUid())
                .child("suspensionEndTime");

        suspensionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long suspensionEndTime = dataSnapshot.getValue(Long.class);
                messagetextview.setText(String.valueOf(suspensionEndTime));
                if (suspensionEndTime != null && suspensionEndTime > System.currentTimeMillis()) {
                    // The tutor is suspended
                    long remainingTimeMillis = suspensionEndTime - System.currentTimeMillis();
                    long remainingHours = remainingTimeMillis / (60 * 60 * 1000);
                    long remainingMinutes = (remainingTimeMillis % (60 * 60 * 1000)) / (60 * 1000);

                    String suspensionMessage = "Your account is suspended. The suspension will be lifted in " +
                            remainingHours + " hours and " + remainingMinutes + " minutes.";

                    messagetextview.setText(suspensionMessage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors that occur during the database read operation
                // You can log or display an error message as needed
            }
        });
    }

}
