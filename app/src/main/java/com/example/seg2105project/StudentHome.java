package com.example.seg2105project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StudentHome extends AppCompatActivity {
    private Button logoutButton, topicButton;
    private TextView textView;
    private FirebaseAuth auth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);

        textView = findViewById(R.id.textView);
        logoutButton = findViewById(R.id.logoutButton);
        topicButton = findViewById(R.id.searchtopicButton);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user == null) {
            Intent intent = new Intent(StudentHome.this, LogIn.class);
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

        topicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayOfferedTopics();
            }
        });
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(StudentHome.this, LogIn.class);
        startActivity(intent);
        finish();
    }

    private void displayOfferedTopics() {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        Query query = usersRef.orderByChild("userType").equalTo("Tutor");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Topic> topics = new ArrayList<>();
                List<Tutor> tutors = new ArrayList<>();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    Tutor tutor = userSnapshot.child("userInfo").getValue(Tutor.class);
                    if (tutor != null && !tutor.getIsSuspended()) {
                        for (DataSnapshot topicSnapshot : userSnapshot.child("offerTopics").getChildren()) {
                            Topic topic = topicSnapshot.getValue(Topic.class);
                            if (topic != null) {
                                tutors.add(tutor);
                                topics.add(topic);
                            }
                        }
                    }
                }

                // Create a dialog to display the topics list
                AlertDialog.Builder builder = new AlertDialog.Builder(StudentHome.this);
                View dialogView = getLayoutInflater().inflate(R.layout.studenttopiclist, null);
                builder.setView(dialogView);

                // Retrieve the ListView from the dialog view
                ListView listViewTopics = dialogView.findViewById(R.id.listViewTopics);

                // Set up the adapter for the ListView
                TopicList adapter = new TopicList(StudentHome.this, topics);
                listViewTopics.setAdapter(adapter);

                listViewTopics.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Tutor selectedTutor = tutors.get(position);


                        displayTopicInfo(selectedTutor);

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }


    private void displayTopicInfo(Tutor tutor) {
        AlertDialog.Builder builder = new AlertDialog.Builder(StudentHome.this);
        View dialogView = getLayoutInflater().inflate(R.layout.lessonsinfo, null);
        builder.setView(dialogView);

        ImageView imageViewTutorPhoto = dialogView.findViewById(R.id.imageViewTutorPhoto);
        TextView textViewTutorName = dialogView.findViewById(R.id.textViewTutorName);
        TextView textViewNativeLanguage = dialogView.findViewById(R.id.textViewNativeLanguage);
        TextView textViewHourlyRate = dialogView.findViewById(R.id.textViewHourlyRate);
        TextView textViewDescription = dialogView.findViewById(R.id.textViewTutorDescription);
        TextView textViewLessonsGiven = dialogView.findViewById(R.id.textViewLessonsGiven);
        TextView textViewLessonsRating = dialogView.findViewById(R.id.textViewLessonsRating);

        // Set the tutor information
        // imageViewTutorPhoto.setImageResource(tutor.getPhoto()); // Set the actual tutor photo
        textViewTutorName.setText(tutor.getFirstName() + " " + tutor.getLastName());
        textViewNativeLanguage.setText("Native Language: " + tutor.getNativeLanguage());
        textViewHourlyRate.setText("Hourly Rate: $" + tutor.getHourlyRate());
        textViewDescription.setText("Description: " + tutor.getDescription());
        textViewLessonsGiven.setText("Lessons Given: " + tutor.getLessonsGiven());
        textViewLessonsRating.setText("Lessons Rating: " + tutor.getLessonsRate());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}

