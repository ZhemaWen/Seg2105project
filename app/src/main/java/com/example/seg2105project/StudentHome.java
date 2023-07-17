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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StudentHome extends AppCompatActivity {
    private Button logoutButton, topicButton,requestButton,reviewButton;
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
        reviewButton=findViewById(R.id.reviewButton);
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
        reviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayReviews();
            }
        });
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(StudentHome.this, LogIn.class);
        startActivity(intent);
        finish();
    }
    private void displayReviews(){
        {
            DatabaseReference reviewRef = FirebaseDatabase.getInstance().getReference()
                    .child("users").child(user.getUid()).child("reviews");
            Query query = reviewRef.orderByChild("timestamp");
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<Review> reviews = new ArrayList<>();
                    for (DataSnapshot reviewSnapshot : dataSnapshot.getChildren()) {
                        Review review = reviewSnapshot.getValue(Review.class);
                        if (review != null) {
                            reviews.add(review);
                        }
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(StudentHome.this);
                    View dialogView = getLayoutInflater().inflate(R.layout.reviewlist, null);
                    builder.setView(dialogView);

                    ListView listViewReviews = dialogView.findViewById(R.id.listViewReview);

                    // Create the custom RequestList adapter to display the list of requests
                    Reviewlist adapter = new Reviewlist(StudentHome.this, reviews);

                    listViewReviews.setAdapter(adapter);

                    listViewReviews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            // Handle item click here
                            Review selectedReview = reviews.get(position);
                            displayStudentReviewDetail(selectedReview);

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

    }
    private void displayRequests(){
        {
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

                    // Create the custom RequestList adapter to display the list of requests
                    Requestlist adapter = new Requestlist(StudentHome.this, requests);

                    listViewRequests.setAdapter(adapter);

                    listViewRequests.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            // Handle item click here
                            Request selectedRequest = requests.get(position);
                            // Perform the desired action for the selected request
                            if(selectedRequest.getStatus().equals("Approve")){
                                displayStudentRequestDetail(selectedRequest);
                            }

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

    }



        private void displayOfferedTopics() {
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
            Query query = usersRef.orderByChild("userType").equalTo("Tutor");
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<Topic> originalTopics = new ArrayList<>();
                    List<Tutor> originalTutors = new ArrayList<>();
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        Tutor tutor = userSnapshot.child("userInfo").getValue(Tutor.class);
                        if (tutor != null && !tutor.getIsSuspended()) {
                            for (DataSnapshot topicSnapshot : userSnapshot.child("offerTopics").getChildren()) {
                                Topic topic = topicSnapshot.getValue(Topic.class);
                                if (topic != null) {
                                    originalTutors.add(tutor);
                                    originalTopics.add(topic);
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

                    // Set up the adapter for the ListView with the original topics list
                    TopicList adapter = new TopicList(StudentHome.this, originalTopics);
                    listViewTopics.setAdapter(adapter);

                    listViewTopics.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Tutor selectedTutor = originalTutors.get(position);
                            Topic selectedTopic = originalTopics.get(position);
                            displayTopicInfo(selectedTopic, selectedTutor);
                        }
                    });

                    Button sort = dialogView.findViewById(R.id.buttonSort);
                    Button search = dialogView.findViewById(R.id.buttonSearch);
                    EditText text = dialogView.findViewById(R.id.editTextSearch);

                    sort.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Sort the topics list based on your desired criteria
                            Collections.sort(originalTopics, new Comparator<Topic>() {
                                @Override
                                public int compare(Topic topic1, Topic topic2) {
                                    // Implement your comparison logic here
                                    // For example, sort by topic name:
                                    return topic1.getTopicName().compareToIgnoreCase(topic2.getTopicName());
                                }
                            });
                            // Notify the adapter that the list has changed
                            adapter.notifyDataSetChanged();
                        }
                    });

                    search.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Perform the search operation based on your desired criteria
                            // For example, search by topic name:

                            String searchKeyword = text.getText().toString().trim();
                            List<Topic> searchResults = new ArrayList<>();
                            for (Topic topic : originalTopics) {
                                if (topic.getTopicName().contains(searchKeyword)) {
                                    searchResults.add(topic);
                                }
                            }
                            // Update the topics list with the search results
                            originalTopics.clear();
                            originalTopics.addAll(searchResults);
                            // Notify the adapter that the list has changed
                            adapter.notifyDataSetChanged();
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
        DatabaseReference tutorRef = FirebaseDatabase.getInstance().getReference().child("users")
                .child(topic.getTutorId())
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
                tutorRef.child(requestId).setValue(request);
                requestRef.child(requestId).setValue(request)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    requestRef.child(requestId).child("topic").child("isOffered").setValue(false);
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
    private void displayStudentRequestDetail(Request request) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.student_request_detail, null);
        builder.setView(dialogView);

        TextView textViewTopicName = dialogView.findViewById(R.id.textViewTopicName);
        TextView textViewYearsOfExperience = dialogView.findViewById(R.id.textViewYearsOfExperience);
        TextView textViewExperienceDescription = dialogView.findViewById(R.id.textViewExperienceDescription);
        TextView textViewDate = dialogView.findViewById(R.id.textViewDate);
        TextView textViewTimeSlot = dialogView.findViewById(R.id.textViewTimeSlot);
        Button buttonComplaint = dialogView.findViewById(R.id.buttonComplaint);
        Button buttonRate = dialogView.findViewById(R.id.buttonReview);
        RatingBar ratingBar =dialogView.findViewById(R.id.ratingBar);
        EditText editTextComplaint = dialogView.findViewById(R.id.editTextComplaint);
        CheckBox checkBoxAnonymous = dialogView.findViewById(R.id.checkBoxAnonymous);

        // Set the request details in the views
        textViewTopicName.setText("Topic name: " + request.getTopic().getTopicName());
        textViewYearsOfExperience.setText("Years of Experience: " + request.getTopic().getYearsOfExperience());
        textViewExperienceDescription.setText("Description: " + request.getTopic().getExperienceDescription());
        textViewDate.setText("Date: " + request.getDate());
        textViewTimeSlot.setText("Time Slot: " + request.getTimeSlot());

        // Set click listeners for the buttons
        buttonComplaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String complaintDescription = editTextComplaint.getText().toString().trim();
                if (complaintDescription.length() > 800) {
                    Toast.makeText(StudentHome.this, "Complaint cannot exceed 800 characters", Toast.LENGTH_SHORT).show();
                } else {
                    // Handle complaint submission with complaintDescription
                    DatabaseReference complaintsRef = FirebaseDatabase.getInstance().getReference().child("complaints");
                    String complaintId = complaintsRef.push().getKey();
                    long currentTimestamp = System.currentTimeMillis();
                    Date date = new Date(currentTimestamp);
                    Complaint complaint = new Complaint(complaintId, request.getTutorId(), complaintDescription, date);
                    complaintsRef.child(complaintId).setValue(complaint)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(StudentHome.this, "Complaint submitted successfully", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(StudentHome.this, "Failed to submit complaint", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        buttonRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float rate = ratingBar.getRating();
                int ratingInt = Math.round(rate);
                String rating = String.valueOf(ratingInt);

                String reviewText = editTextComplaint.getText().toString().trim();
                DatabaseReference studentRef = FirebaseDatabase.getInstance().getReference().child("users")
                        .child(request.getStudentId());

                // Validate rating input
                if (rating.isEmpty() || !rating.matches("[1-5]")) {
                    Toast.makeText(StudentHome.this, "Please enter a rating between 1 and 5", Toast.LENGTH_SHORT).show();
                }
                // Validate review input
                else if (reviewText.length() > 800) {
                    Toast.makeText(StudentHome.this, "Review cannot exceed 800 characters", Toast.LENGTH_SHORT).show();
                } else if (request.isReview()||request.getTopic().getIsOffered()) {
                    Toast.makeText(StudentHome.this, "Review already submit for this topic", Toast.LENGTH_SHORT).show();

                } else {

                    DatabaseReference tutorRef = FirebaseDatabase.getInstance().getReference().child("users")
                            .child(request.getTutorId()).child("userInfo").child("lessonsRate");
                    studentRef.child("studentName").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Handle data retrieval success
                            if (dataSnapshot.exists()) {
                                // Retrieve the value from the dataSnapshot
                                String name = dataSnapshot.getValue(String.class);
                                String reviewId = studentRef.child("reviews").push().getKey();
                                String studentName = checkBoxAnonymous.isChecked() ? "Anonymous" : name; // Replace "Student Name" with actual student name implementation
                                Date reviewDate = new Date(System.currentTimeMillis());

                                Review review = new Review(reviewId, studentName, reviewDate, request, Integer.parseInt(rating), reviewText);
                                studentRef.child("reviews").child(reviewId).setValue(review)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    tutorRef.addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            if (snapshot.exists()) {
                                                                Double totalRating = snapshot.getValue(Double.class);
                                                                double newRating = (totalRating + Double.parseDouble(rating)) / 2;
                                                                tutorRef.setValue(newRating);
                                                            } else {
                                                                tutorRef.setValue(Double.parseDouble(rating));
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                                    FirebaseDatabase.getInstance().getReference().child("users")
                                                            .child(request.getStudentId()).child("requests")
                                                            .child(request.getRequestId()).child("review").setValue(true);
                                                    FirebaseDatabase.getInstance().getReference().child("users")
                                                            .child(request.getStudentId()).child("requests")
                                                            .child(request.getRequestId()).child("topic").child("isOffered").setValue(true);
                                                    request.setReview(true);
                                                    Toast.makeText(StudentHome.this, "Review submitted successfully", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(StudentHome.this, "Failed to submit review", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                            } else {

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void displayStudentReviewDetail(Review review) {
        Request request = review.getRequest();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.reviewdetail, null);
        builder.setView(dialogView);


        TextView textViewTopicName = dialogView.findViewById(R.id.textViewTopicName);
        TextView textViewYearsOfExperience = dialogView.findViewById(R.id.textViewYearsOfExperience);
        TextView textViewExperienceDescription = dialogView.findViewById(R.id.textViewExperienceDescription);
        TextView textViewDate = dialogView.findViewById(R.id.textViewDate);
        TextView textViewTimeSlot = dialogView.findViewById(R.id.textViewTimeSlot);
        Button buttonDelete = dialogView.findViewById(R.id.buttonDelete);
        Button buttonChange = dialogView.findViewById(R.id.buttonChange);
        EditText editTextComplaint = dialogView.findViewById(R.id.editTextComplaint);
        CheckBox checkBoxAnonymous = dialogView.findViewById(R.id.checkBoxAnonymous);
        RatingBar ratingBar = dialogView.findViewById(R.id.ratingBar);

        // Set the request details in the views
        textViewTopicName.setText("Topic name: " + request.getTopic().getTopicName());
        textViewYearsOfExperience.setText("Years of Experience: " + request.getTopic().getYearsOfExperience());
        textViewExperienceDescription.setText("Description: " + request.getTopic().getExperienceDescription());
        textViewDate.setText("Date: " + request.getDate());
        textViewTimeSlot.setText("Time Slot: " + request.getTimeSlot());

        // Set click listeners for the buttons
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference reviewRef = FirebaseDatabase.getInstance().getReference().child("users")
                        .child(request.getStudentId()).child("reviews").child(review.getReviewId());


                reviewRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            FirebaseDatabase.getInstance().getReference().child("users")
                                    .child(request.getStudentId()).child("requests")
                                    .child(request.getRequestId()).child("review").setValue(false);
                            FirebaseDatabase.getInstance().getReference().child("users")
                                    .child(request.getStudentId()).child("requests")
                                    .child(request.getRequestId()).child("topic").child("isOffered").setValue(false);
                            request.setReview(false);
                            Toast.makeText(StudentHome.this, "Review remove successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(StudentHome.this, "Failed to remove review", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        buttonChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date reviewDate = review.getDate();
                Date currentDate = new Date();
                long timeDifference = currentDate.getTime() - reviewDate.getTime();
                long millisecondsInWeek = 7 * 24 * 60 * 60 * 1000;
                boolean isMoreThanAWeekAgo = timeDifference > millisecondsInWeek;


                float rate = ratingBar.getRating();
                int ratingInt = Math.round(rate);
                String rating = String.valueOf(ratingInt);
                String reviewText = editTextComplaint.getText().toString().trim();
                DatabaseReference studentRef = FirebaseDatabase.getInstance().getReference().child("users")
                        .child(request.getStudentId());

                // Validate rating input
                if (rating.isEmpty() || !rating.matches("[1-5]")) {
                    Toast.makeText(StudentHome.this, "Please enter a rating between 1 and 5", Toast.LENGTH_SHORT).show();
                }
                // Validate review input
                else if (reviewText.length() > 800) {
                    Toast.makeText(StudentHome.this, "Review cannot exceed 800 characters", Toast.LENGTH_SHORT).show();
                } else if (isMoreThanAWeekAgo) {
                    Toast.makeText(StudentHome.this, "Review not within a week", Toast.LENGTH_SHORT).show();

                } else {
                    DatabaseReference reviewRef = FirebaseDatabase.getInstance().getReference().child("users")
                            .child(request.getStudentId()).child("reviews").child(review.getReviewId());


                    reviewRef.removeValue();
                    DatabaseReference tutorRef = FirebaseDatabase.getInstance().getReference().child("users")
                            .child(request.getTutorId()).child("userInfo").child("lessonsRate");
                    studentRef.child("studentName").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Handle data retrieval success
                            if (dataSnapshot.exists()) {
                                // Retrieve the value from the dataSnapshot
                                String name = dataSnapshot.getValue(String.class);
                                String reviewId = studentRef.child("reviews").push().getKey();
                                String studentName = checkBoxAnonymous.isChecked() ? "Anonymous" : name; // Replace "Student Name" with actual student name implementation
                                Date reviewDate = new Date(System.currentTimeMillis());

                                Review review = new Review(reviewId, studentName, reviewDate, request, Integer.parseInt(rating), reviewText);
                                studentRef.child("reviews").child(reviewId).setValue(review)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    tutorRef.addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            if (snapshot.exists()) {
                                                                Double totalRating = snapshot.getValue(Double.class);
                                                                double newRating = (totalRating + Double.parseDouble(rating)) / 2;
                                                                tutorRef.setValue(newRating);
                                                            } else {
                                                                tutorRef.setValue(Double.parseDouble(rating));
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                                    FirebaseDatabase.getInstance().getReference().child("users")
                                                            .child(request.getStudentId()).child("requests")
                                                            .child(request.getRequestId()).child("review").setValue(true);
                                                    FirebaseDatabase.getInstance().getReference().child("users")
                                                            .child(request.getStudentId()).child("requests")
                                                            .child(request.getRequestId()).child("topic").child("isOffered").setValue(true);
                                                    request.setReview(true);
                                                    Toast.makeText(StudentHome.this, "Review submitted successfully", Toast.LENGTH_SHORT).show();
                                                    recreate();
                                                } else {
                                                    Toast.makeText(StudentHome.this, "Failed to submit review", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                            } else {

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }



}
