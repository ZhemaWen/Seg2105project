package com.example.seg2105project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class home extends AppCompatActivity {

    private Button logoutButton, topicButton;
    private TextView textView, messagetextview;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference suspensionEndTimeRef;
    private DatabaseReference topicsRef;
    private boolean isSuspended = false;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        textView = findViewById(R.id.textView);
        messagetextview = findViewById(R.id.messagetextView);
        logoutButton = findViewById(R.id.logoutButton);
        topicButton = findViewById(R.id.topicButton);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user == null) {
            Intent intent = new Intent(home.this, LogIn.class);
            startActivity(intent);
            finish();
        } else {
            textView.setText("Welcome " + user.getEmail() + ", you are logged in as a tutor");
        }

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        topicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSuspended) {
                    Toast.makeText(home.this, "Your account is suspended. Please contact support.", Toast.LENGTH_SHORT).show();
                } else {
                    displayTopics();
                }
            }
        });

        checkSuspensionStatus();
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(home.this, LogIn.class);
        startActivity(intent);
        finish();
    }

    private void checkSuspensionStatus() {
        suspensionEndTimeRef = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(user.getUid())
                .child("suspensionEndTime");

        suspensionEndTimeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long suspensionEndTime = dataSnapshot.getValue(Long.class);
                messagetextview.setText("");
                if (suspensionEndTime != null && suspensionEndTime > System.currentTimeMillis()) {
                    // The tutor is suspended
                    long remainingTimeMillis = suspensionEndTime - System.currentTimeMillis();
                    long remainingHours = remainingTimeMillis / (60 * 60 * 1000);
                    long remainingMinutes = (remainingTimeMillis % (60 * 60 * 1000)) / (60 * 1000);

                    String suspensionMessage = "Your account is suspended. The suspension will be lifted in " +
                            remainingHours + " hours and " + remainingMinutes + " minutes.";

                    messagetextview.setText(suspensionMessage);
                     isSuspended = true;

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors that occur during the database read operation
            }
        });
    }
    private void displayTopics() {
        topicsRef = FirebaseDatabase.getInstance().getReference(). child("users")
                .child(user.getUid())
                .child("topics");

        topicsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Topic> topics = new ArrayList<>();
                for (DataSnapshot topicSnapshot : dataSnapshot.getChildren()) {
                    Topic topic = topicSnapshot.getValue(Topic.class);
                    if (topic != null) {
                        topics.add(topic);
                    }
                }

                // Create a dialog to display the topics list
                AlertDialog.Builder builder = new AlertDialog.Builder(home.this);
                View dialogView = getLayoutInflater().inflate(R.layout.topiclist, null);
                builder.setView(dialogView);

                // Retrieve the ListView from the dialog view
                ListView listViewTopics = dialogView.findViewById(R.id.listViewTopics);

                // Set up the adapter for the ListView
                TopicList adapter = new TopicList(home.this, topics);
                listViewTopics.setAdapter(adapter);

                AlertDialog dialog = builder.create();
                dialog.show();
                Button addTopic = dialogView.findViewById(R.id.buttonAddTopic);



                addTopic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showAddTopic();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });

    }
    private void showAddTopic() {
        AlertDialog.Builder builder = new AlertDialog.Builder(home.this);
        View dialogView = getLayoutInflater().inflate(R.layout.addtopic, null);
        builder.setView(dialogView);

        EditText editTextTopicName = dialogView.findViewById(R.id.editTextTopicName);
        EditText editTextYearsOfExperience = dialogView.findViewById(R.id.editTextYearsOfExperience);
        EditText editTextExperienceDescription = dialogView.findViewById(R.id.editTextExperienceDescription);
        Button buttonAddTopic = dialogView.findViewById(R.id.buttonAddTopic);

        AlertDialog dialog = builder.create();
        dialog.show();

        buttonAddTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String topicName = editTextTopicName.getText().toString().trim();
                String yearsOfExperience = editTextYearsOfExperience.getText().toString().trim();
                String experienceDescription = editTextExperienceDescription.getText().toString().trim();

                // Validate the input fields
                if (topicName.isEmpty() || yearsOfExperience.isEmpty() || experienceDescription.isEmpty()) {
                    Toast.makeText(home.this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create a new Topic object with the input data
                String topicId = topicsRef.push().getKey();
                Topic topic = new Topic(topicId, topicName, yearsOfExperience, experienceDescription);

                // Save the topic to the database
                topicsRef.child(topicId).setValue(topic)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(home.this, "Topic added successfully", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(home.this, "Failed to add topic", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}




