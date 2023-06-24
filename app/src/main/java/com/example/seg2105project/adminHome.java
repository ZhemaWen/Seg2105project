package com.example.seg2105project;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

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

public class adminHome extends AppCompatActivity {
    private Button logoutButton;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private TextView textView;
    private ListView listViewComplaints;
    private DatabaseReference complaintsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        listViewComplaints = findViewById(R.id.listViewComplaints);
        textView = findViewById(R.id.textView);
        logoutButton = findViewById(R.id.logoutButton);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user == null) {
            Intent intent = new Intent(adminHome.this, LogIn.class);
            startActivity(intent);
            finish();
        } else {
            textView.setText("Welcome admin");
        }

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        // Get reference to the "complaints" node in the database
        complaintsRef = FirebaseDatabase.getInstance().getReference().child("complaints");

        displayAllComplaints();
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(adminHome.this, LogIn.class);
        startActivity(intent);
        finish();
    }

    private void displayAllComplaints() {
        Query query = complaintsRef.orderByChild("timestamp");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Complaint> complaints = new ArrayList<>();
                for (DataSnapshot complaintSnapshot : dataSnapshot.getChildren()) {
                    Complaint complaint = complaintSnapshot.getValue(Complaint.class);
                    if (complaint != null&&complaint.isSuspended()==false) {
                        complaints.add(complaint);
                    }
                }
                ComplaintList adapter = new ComplaintList(adminHome.this, complaints);
                listViewComplaints.setAdapter(adapter);

                listViewComplaints.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Complaint complaint = complaints.get(position);
                        showComplaintDialog(complaint);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }



    private void showComplaintDialog(final Complaint complaint) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.complaint_dialog, null);
        dialogBuilder.setView(dialogView);

        Button dismissButton = dialogView.findViewById(R.id.buttonDismiss);
        Button suspendButton = dialogView.findViewById(R.id.buttonSuspend);

        final AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissComplaint(complaint);
                dialog.dismiss();
            }
        });

        suspendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSuspensionDialog(complaint);
                dialog.dismiss();
            }
        });
    }


    private void showSuspensionDialog(final Complaint complaint) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.suspension_dialog, null);
        dialogBuilder.setView(dialogView);

        Button button1Hour = dialogView.findViewById(R.id.button1Hour);
        Button button24Hours = dialogView.findViewById(R.id.button24Hours);
        Button button7Days = dialogView.findViewById(R.id.button7Days);

        final AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        button1Hour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                suspendTutor(complaint, 1);
                dialog.dismiss();
            }
        });

        button24Hours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                suspendTutor(complaint, 24);
                dialog.dismiss();
            }
        });

        button7Days.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                suspendTutor(complaint, 7 * 24);
                dialog.dismiss();
            }
        });
    }

    private void suspendTutor(Complaint complaint, int durationInHours) {
        // Calculate the suspension end time based on the current timestamp and the duration
        long currentTimestamp = System.currentTimeMillis();
        long suspensionEndTimestamp = currentTimestamp + (durationInHours * 60 * 60 * 1000);
        complaintsRef.child(complaint.getComplaintId()).child("suspended").setValue(true);
        DatabaseReference tutorRef = FirebaseDatabase.getInstance().getReference().child("users").child(complaint.getTutorId());
        tutorRef.child("suspensionEndTime").setValue(suspensionEndTimestamp);
        // Optionally, you can update the UI or show a message to indicate the tutor has been suspended
    }
    private void dismissComplaint(Complaint complaint) {
        DatabaseReference complaintRef = complaintsRef.child(complaint.getComplaintId());
        complaintRef.removeValue();
        // Optionally, you can update the UI or show a message to indicate the complaint has been dismissed
    }




}
