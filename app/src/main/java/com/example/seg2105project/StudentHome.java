package com.example.seg2105project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StudentHome extends AppCompatActivity {
    private Button logoutButton, topicButton,requestButton;
    private TextView textView;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private int selectedYear, selectedMonth, selectedDay;
    private int selectedHour, selectedMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);

        textView = findViewById(R.id.textView);
        logoutButton = findViewById(R.id.logoutButton);
        topicButton = findViewById(R.id.searchtopicButton);
        requestButton= findViewById(R.id.requestsButton);
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
        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayRequests();
            }
        });
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(StudentHome.this, LogIn.class);
        startActivity(intent);
        finish();
    }
    private void displayRequests(){
        DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference()
                .child("users").child(user.getUid()).child("requests");
        Query query = requestRef.orderByChild("timestamp");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Request> requests = new ArrayList<>();
                for (DataSnapshot requestSnapshot : dataSnapshot.getChildren()) {
                    Request request = requestSnapshot.getValue(Request.class);
                    if (request != null) {
                        requests.add(request);
                    }
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(StudentHome.this);
                View dialogView = getLayoutInflater().inflate(R.layout.requestlist, null);
                builder.setView(dialogView);

                ListView listViewRequests = dialogView.findViewById(R.id.listViewRequest);

                // Create a default ArrayAdapter to display the list of requests
                ArrayAdapter<Request> adapter = new ArrayAdapter<>(StudentHome.this,
                        android.R.layout.simple_list_item_1, requests);

                listViewRequests.setAdapter(adapter);

                listViewRequests.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // Handle item click here
                        Request selectedRequest = requests.get(position);
                        // Perform the desired action for the selected request
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
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
                        Topic selectedTopic = topics.get(position);
                        displayTopicInfo(selectedTopic,selectedTutor);


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

    private void displayTopicInfo(Topic topic, Tutor tutor){
        AlertDialog.Builder builder = new AlertDialog.Builder(StudentHome.this);
        View dialogView = getLayoutInflater().inflate(R.layout.studenttopicdeatil, null);
        builder.setView(dialogView);


        TextView topicName = dialogView.findViewById(R.id.textViewTopicName);
        TextView year = dialogView.findViewById(R.id.textViewYearsOfExperience);
        TextView detail = dialogView.findViewById(R.id.textViewExperienceDescription);
        topicName.setText("Topic Name: " + topic.getTopicName());
        year.setText("Year of Experience: " + topic.getYearsOfExperience());
        detail.setText("Experience Description: " + topic.getExperienceDescription());

        Button tutorInfoButton = dialogView.findViewById(R.id.buttonTutorInfo);
        Button purchaseButton = dialogView.findViewById(R.id.buttonPurchaseTopic);

        tutorInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayTutorInfo(tutor);
            }
        });
        purchaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(topic);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showDatePickerDialog(Topic topic) {
        // Get the current date
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                StudentHome.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        selectedYear = year;
                        selectedMonth = month;
                        selectedDay = dayOfMonth;

                        Calendar selectedDateCalendar = Calendar.getInstance();
                        selectedDateCalendar.set(Calendar.YEAR, selectedYear);
                        selectedDateCalendar.set(Calendar.MONTH, selectedMonth);
                        selectedDateCalendar.set(Calendar.DAY_OF_MONTH, selectedDay);

                        Date selectedDate = selectedDateCalendar.getTime();

                        showTimeSlotSelection(topic, selectedDate);
                    }
                },
                currentYear,
                currentMonth,
                currentDay
        );// Set the minimum date to tomorrow
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());

        datePickerDialog.show();
    }

    private void showTimeSlotSelection(Topic topic, Date date) {
        // Create a list of time slots
        List<String> timeSlots = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(selectedYear, selectedMonth, selectedDay, 0, 0);
        calendar.add(Calendar.HOUR_OF_DAY, 2); // Start with the first time slot after the selected date
        while (calendar.get(Calendar.DAY_OF_MONTH) == selectedDay) {
            String timeSlot = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT).format(calendar.getTime());
            timeSlots.add(timeSlot);
            calendar.add(Calendar.HOUR_OF_DAY, 2); // Increment by 2 hours for the next time slot
        }

        // Convert the list of time slots to an array
        String[] timeSlotArray = timeSlots.toArray(new String[0]);

        // Show the time slot selection dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(StudentHome.this);
        builder.setTitle("Select a time slot")
                .setItems(timeSlotArray, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String selectedTimeSlot = timeSlotArray[which];
                        Toast.makeText(StudentHome.this, "Selected time slot: " + selectedTimeSlot, Toast.LENGTH_SHORT).show();

                        purchaseTopic(topic, selectedTimeSlot, date);
                    }
                });

        builder.create().show();
    }

    private void purchaseTopic(Topic topic, String timeSlot, Date date){
        DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference().child("users")
                .child(user.getUid())
                .child("requests");

        requestRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Create a new Request object with the input data
                String requestId = requestRef.push().getKey();


                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String dateString = dateFormat.format(date);
                Request request = new Request(topic, requestId, topic.getTutorId(), user.getUid(),dateString,timeSlot);


                // Save the request to the database
                requestRef.child(requestId).setValue(request)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(StudentHome.this, "Request purchased successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(StudentHome.this, "Failed to purchase request", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });

    }

    private void displayTutorInfo(Tutor tutor) {
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
