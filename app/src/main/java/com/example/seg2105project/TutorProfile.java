package com.example.seg2105project;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TutorProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lessonsinfo);

        // Find views and set the tutor's profile information
        ImageView imageViewTutorPhoto = findViewById(R.id.imageViewTutorPhoto);
        TextView textViewTutorName = findViewById(R.id.textViewTutorName);
        TextView textViewNativeLanguage = findViewById(R.id.textViewNativeLanguage);
        TextView textViewHourlyRate = findViewById(R.id.textViewHourlyRate);
        TextView textViewTutorDescription = findViewById(R.id.textViewTutorDescription);
        TextView textViewLessonsGiven = findViewById(R.id.textViewLessonsGiven);
        TextView textViewLessonsRating = findViewById(R.id.textViewLessonsRating);

        // Set the values based on the tutor's data retrieved from Firebase or any other source
        // For example:
        imageViewTutorPhoto.setImageResource(R.drawable.placeholderprofile);
        textViewTutorName.setText("Tutor Name");
        textViewNativeLanguage.setText("Native Language: English");
        textViewHourlyRate.setText("Hourly Rate: $25");
        textViewTutorDescription.setText("Average Rating: 4.5");
        textViewLessonsGiven.setText("Lessons Given: 50");
        textViewLessonsRating.setText("Lessons Rating: 4.2");
    }
}
