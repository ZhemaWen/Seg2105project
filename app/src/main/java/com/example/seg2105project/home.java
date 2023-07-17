package com.example.seg2105project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class home extends AppCompatActivity {

    private Button logoutButton, topicButton,requestButton,profileButton;
    private TextView textView, messagetextview;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference suspensionEndTimeRef;
    private DatabaseReference topicsRef, offerTopicsRef;
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
        requestButton = findViewById(R.id.requestsButton);
        profileButton = findViewById(R.id.profileButton);
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
        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSuspended) {
                    Toast.makeText(home.this, "Your account is suspended. Please contact support.", Toast.LENGTH_SHORT).show();
                } else {
                    displayRequests();


                }
            }
        });
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSuspended) {
                    Toast.makeText(home.this, "Your account is suspended. Please contact support.", Toast.LENGTH_SHORT).show();
                } else {
                    DatabaseReference tutorRef = FirebaseDatabase.getInstance().getReference()
                            .child("users")
                            .child(user.getUid())
                            .child("userInfo");

                    tutorRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Tutor tutor = dataSnapshot.getValue(Tutor.class);
                            if (tutor != null) {
                                displayTutorProfile(tutor);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle error
                        }
                    });
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
                else if(suspensionEndTime != null){
                    FirebaseDatabase.getInstance().getReference()
                            .child("users")
                            .child(user.getUid())
                            .child("userInfo").child("isSuspended").setValue(false);
                    suspensionEndTimeRef.removeValue();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors that occur during the database read operation
            }
        });
    }

    private void displayOfferTopics() {
        offerTopicsRef.addValueEventListener(new ValueEventListener() {
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
                View dialogView = getLayoutInflater().inflate(R.layout.offertopiclist, null);
                builder.setView(dialogView);

                // Retrieve the ListView from the dialog view
                ListView listViewTopics = dialogView.findViewById(R.id.listViewTopics);

                // Set up the adapter for the ListView
                TopicList adapter = new TopicList(home.this, topics);
                listViewTopics.setAdapter(adapter);
                listViewTopics.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Topic topic = topics.get(position);
                        showOfferTopicDialog(topic);
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

    private void showOfferTopicDialog(final Topic topic) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.offertopicdetail, null);
        dialogBuilder.setView(dialogView);
        TextView topicName = dialogView.findViewById(R.id.textViewTopicName);
        TextView year = dialogView.findViewById(R.id.textViewYearsOfExperience);
        TextView detail = dialogView.findViewById(R.id.textViewExperienceDescription);
        topicName.setText("Topic Name: " + topic.getTopicName());
        year.setText("Year of Experience: " + topic.getYearsOfExperience());
        detail.setText("Experience Description: " + topic.getExperienceDescription());

        Button removeButton = dialogView.findViewById(R.id.buttonRemoveTopic);

        final AlertDialog dialog = dialogBuilder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                offerTopicsRef.child(topic.getTopicId()).removeValue();
                topicsRef.child(topic.getTopicId()).child("isOffered").setValue(false);
                dialog.dismiss();
            }
        });
    }

    private void displayTopics() {
        topicsRef = FirebaseDatabase.getInstance().getReference().child("users")
                .child(user.getUid())
                .child("topics");
        offerTopicsRef = FirebaseDatabase.getInstance().getReference().child("users")
                .child(user.getUid())
                .child("offerTopics");
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
                listViewTopics.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Topic topic = topics.get(position);
                        showTopicDialog(topic);
                    }
                });

                Button addTopicButton = dialogView.findViewById(R.id.buttonAddTopic);
                addTopicButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showAddTopic();
                    }
                });

                Button offerListButton = dialogView.findViewById(R.id.buttonOfferlist);
                offerListButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        displayOfferTopics();
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

    private void showAddTopic() {
        AlertDialog.Builder builder = new AlertDialog.Builder(home.this);
        View dialogView = getLayoutInflater().inflate(R.layout.addtopic, null);
        builder.setView(dialogView);

        EditText editTextTopicName = dialogView.findViewById(R.id.editTextTopicName);
        EditText editTextYearsOfExperience = dialogView.findViewById(R.id.editTextYearsOfExperience);
        EditText editTextExperienceDescription = dialogView.findViewById(R.id.editTextExperienceDescription);
        Button buttonAddTopic = dialogView.findViewById(R.id.buttonAddTopic);

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
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

                topicsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int numTopics = (int) dataSnapshot.getChildrenCount();

                        // Check if the maximum limit (20) is reached
                        if (numTopics >= 20) {
                            Toast.makeText(home.this, "You have reached the maximum limit of 20 topics", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            return;
                        }

                        // Create a new Topic object with the input data
                        String topicId = topicsRef.push().getKey();
                        Topic topic = new Topic(user.getUid(),topicId, topicName, yearsOfExperience, experienceDescription);

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

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle error
                    }
                });
            }
        });
    }

    private void showTopicDialog(final Topic topic) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.topicdetail, null);
        dialogBuilder.setView(dialogView);
        TextView topicName = dialogView.findViewById(R.id.textViewTopicName);
        TextView year = dialogView.findViewById(R.id.textViewYearsOfExperience);
        TextView detail = dialogView.findViewById(R.id.textViewExperienceDescription);
        topicName.setText("Topic Name: " + topic.getTopicName());
        year.setText("Year of Experience: " + topic.getYearsOfExperience());
        detail.setText("Experience Description: " + topic.getExperienceDescription());

        Button removeButton = dialogView.findViewById(R.id.buttonRemoveTopic);
        Button offerButton = dialogView.findViewById(R.id.buttonOffer);

        final AlertDialog dialog = dialogBuilder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(topic.getIsOffered()){
                    offerTopicsRef.child(topic.getTopicId()).removeValue();
                }
                topicsRef.child(topic.getTopicId()).removeValue();
                dialog.dismiss();
            }
        });

        offerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                offerTopicsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        int numTopics = (int) dataSnapshot.getChildrenCount();

                        // Check if the maximum limit (5) is reached
                        if (numTopics >= 5) {
                            Toast.makeText(home.this, "You have reached the maximum limit of 5 topics", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            return;
                        }
                            // Save the topic to the database
                        if(topic.getIsOffered()) {
                            Toast.makeText(home.this, "already offered", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            return;
                        }
                            topic.offered();
                            topicsRef.child(topic.getTopicId()).child("isOffered").setValue(true);
                            offerTopicsRef.child(topic.getTopicId()).setValue(topic);
                            offerTopicsRef.child(topic.getTopicId()).child("tutorId").setValue(user.getUid());
                            // The topic with the given ID does not exist in the database

                            dialog.dismiss();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle error
                    }
                });
            }
        });
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

                    AlertDialog.Builder builder = new AlertDialog.Builder(home.this);
                    View dialogView = getLayoutInflater().inflate(R.layout.requestlist, null);
                    builder.setView(dialogView);

                    ListView listViewRequests = dialogView.findViewById(R.id.listViewRequest);

                    // Create the custom RequestList adapter to display the list of requests
                    Requestlist adapter = new Requestlist(home.this, requests);

                    listViewRequests.setAdapter(adapter);

                    listViewRequests.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            // Handle item click here
                            Request selectedRequest = requests.get(position);
                            // Perform the desired action for the selected request
                            displayTutorRequestDetail(selectedRequest);

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
    private void displayTutorRequestDetail(Request request) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.tutor_request_detail, null);
        builder.setView(dialogView);

        TextView textViewTopicName = dialogView.findViewById(R.id.textViewTopicName);
        TextView textViewYearsOfExperience = dialogView.findViewById(R.id.textViewYearsOfExperience);
        TextView textViewExperienceDescription = dialogView.findViewById(R.id.textViewExperienceDescription);
        TextView textViewDate = dialogView.findViewById(R.id.textViewDate);
        TextView textViewTimeSlot = dialogView.findViewById(R.id.textViewTimeSlot);
        Button buttonApprove = dialogView.findViewById(R.id.buttonApprove);
        Button buttonReject = dialogView.findViewById(R.id.buttonReject);

        // Set the request details in the views
        textViewTopicName.setText("Topic name: " + request.getTopic().getTopicName());
        textViewYearsOfExperience.setText("Years of Experience: " + request.getTopic().getYearsOfExperience());
        textViewExperienceDescription.setText("Description: " + request.getTopic().getExperienceDescription());
        textViewDate.setText("Date: " + request.getDate());
        textViewTimeSlot.setText("Time Slot: " + request.getTimeSlot());

        // Set click listeners for the buttons
        DatabaseReference tutorRef = FirebaseDatabase.getInstance().getReference().child("users")
                .child(user.getUid())
                .child("requests");
        DatabaseReference studentRef = FirebaseDatabase.getInstance().getReference().child("users")
                .child(request.getStudentId())
                .child("requests");
        if(request.getStatus().equals("Pending")) {
            buttonApprove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tutorRef.child(request.getRequestId()).child("status").setValue("Approve")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        studentRef.child(request.getRequestId()).child("status").setValue("Approve")
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                                                                    .child("users")
                                                                    .child(user.getUid())
                                                                    .child("userInfo")
                                                                    .child("lessonsGiven");

                                                            userRef.runTransaction(new Transaction.Handler() {
                                                                @NonNull
                                                                @Override
                                                                public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                                                                    Integer currentValue = mutableData.getValue(Integer.class);
                                                                    if (currentValue == null) {
                                                                        // If the value is null, initialize it to 0
                                                                        mutableData.setValue(0);
                                                                    } else {
                                                                        // Increment the value by 1
                                                                        mutableData.setValue(currentValue + 1);
                                                                    }
                                                                    return Transaction.success(mutableData);
                                                                }

                                                                @Override
                                                                public void onComplete(@Nullable DatabaseError databaseError, boolean committed, @Nullable DataSnapshot dataSnapshot) {

                                                                }
                                                            });
                                                            studentRef.child(request.getRequestId()).child("review").setValue(false);
                                                            Toast.makeText(getApplicationContext(), "Request Approved", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Toast.makeText(getApplicationContext(), "Failed to approve request", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Failed to approve request", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            });

            buttonReject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tutorRef.child(request.getRequestId()).removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        studentRef.child(request.getRequestId()).child("status").setValue("Reject")
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(getApplicationContext(), "Request Rejected", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Toast.makeText(getApplicationContext(), "Failed to reject request", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Failed to reject request", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            });
        }
        else{
            Toast.makeText(getApplicationContext(), "unable to change request not pending", Toast.LENGTH_SHORT).show();

        }
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void displayTutorProfile(Tutor tutor) {
        AlertDialog.Builder builder = new AlertDialog.Builder(home.this);
        View dialogView = getLayoutInflater().inflate(R.layout.tutor_profile, null);
        builder.setView(dialogView);

        ImageView imageViewTutorPhoto = dialogView.findViewById(R.id.imageViewTutorPhoto);
        TextView textViewTutorName = dialogView.findViewById(R.id.textViewTutorName);
        TextView textViewNativeLanguage = dialogView.findViewById(R.id.textViewNativeLanguage);
        TextView textViewHourlyRate = dialogView.findViewById(R.id.textViewHourlyRate);
        EditText editTextHourlyRate = dialogView.findViewById(R.id.editTextHourlyRate);
        Button buttonUpdateHourlyRate = dialogView.findViewById(R.id.buttonUpdateHourlyRate);
        TextView textViewDescription = dialogView.findViewById(R.id.textViewDescription);
        TextView textViewLessonsGiven = dialogView.findViewById(R.id.textViewLessonsGiven);
        TextView textViewLessonsRating = dialogView.findViewById(R.id.textViewLessonsRating);

        // Set the tutor information
        // imageViewTutorPhoto.setImageResource(tutor.getPhoto()); // Set the actual tutor photo
        textViewTutorName.setText(tutor.getFirstName() + " " + tutor.getLastName());
        textViewNativeLanguage.setText("Native Language: " + tutor.getNativeLanguage());
        textViewHourlyRate.setText("Hourly Rate: $" + tutor.getHourlyRate());
        editTextHourlyRate.setText(String.valueOf(tutor.getHourlyRate()));
        textViewDescription.setText("Description: " + tutor.getDescription());
        textViewLessonsGiven.setText("Lessons Given: " + tutor.getLessonsGiven());
        textViewLessonsRating.setText("Lessons Rating: " + tutor.getLessonsRate());

        AlertDialog dialog = builder.create();
        dialog.show();

        buttonUpdateHourlyRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newHourlyRateStr = editTextHourlyRate.getText().toString().trim();
                if (!newHourlyRateStr.isEmpty()) {
                    // Check if the input is a valid number with two decimal places and no negative values
                    if (newHourlyRateStr.matches("^\\d+(\\.\\d{1,2})?$") && Double.parseDouble(newHourlyRateStr) >= 0) {
                        double newHourlyRate = Double.parseDouble(newHourlyRateStr);
                        // Update the tutor's hourly rate in the database
                        DatabaseReference hourlyRateRef = FirebaseDatabase.getInstance().getReference()
                                .child("users")
                                .child(user.getUid())
                                .child("userInfo")
                                .child("hourlyRate");

                        hourlyRateRef.setValue(newHourlyRate, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError == null) {
                                    textViewHourlyRate.setText("Hourly Rate: $" + newHourlyRate);
                                    Toast.makeText(home.this, "Hourly rate updated successfully", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(home.this, "Failed to update hourly rate", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(home.this, "Please enter a valid hourly rate (positive number with two decimal places)", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(home.this, "Please enter a valid hourly rate", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


}
