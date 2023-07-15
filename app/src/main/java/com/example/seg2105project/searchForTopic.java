package com.example.seg2105project;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class searchForTopic extends AppCompatActivity {
    private void searchTopic(String searchCriteria) {
        DatabaseReference topicRef = FirebaseDatabase.getInstance().getReference().child("topics");
        Query query = topicRef.orderByChild("title").startAt(searchCriteria).endAt(searchCriteria + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Topic> availableTopics = new ArrayList<>();


                for (DataSnapshot topicSnapshot : dataSnapshot.getChildren()) {
                    Topic topic = topicSnapshot.getValue(Topic.class);

                    // Check if the tutor is not suspended
                    if ( topic!= null ) {
                        Tutor relatedTutor = topic.getTutor();
                        if(!relatedTutor.getIsSuspended()){
                        availableTopics.add(topic);
                        }
                    }
                }

                // Display the found lessons to the student
                displaySearchResults(availableTopics);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                String errorMessage = databaseError.getMessage();
                Toast.makeText(getApplicationContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displaySearchResults(List<Topic> topics) {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        topicAdapter adapter = new topicAdapter(topics);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

}
