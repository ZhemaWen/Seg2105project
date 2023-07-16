package com.example.seg2105project;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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

public class RequestsActivity extends AppCompatActivity {

    private List<Request> requests;
    private FirebaseAuth auth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_list_tutor);

        requests = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user == null) {
            // User is not logged in, handle accordingly
            return;
        }

        DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference()
                .child("users").child(user.getUid()).child("requests");
        Query query = requestRef.orderByChild("timestamp");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                requests.clear();
                for (DataSnapshot requestSnapshot : dataSnapshot.getChildren()) {
                    Request request = requestSnapshot.getValue(Request.class);
                    if (request != null) {
                        requests.add(request);
                    }
                }

                showRequestsList();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private void showRequestsList() {
        LinearLayout requestsLayout = findViewById(R.id.requestsLayout);
        requestsLayout.removeAllViews();

        if (requests.isEmpty()) {
            TextView noRequestsTextView = new TextView(this);
            noRequestsTextView.setText("There are no requests");
            requestsLayout.addView(noRequestsTextView);
        } else {
            for (final Request request : requests) {
                View requestRowView = LayoutInflater.from(this).inflate(R.layout.request_list_tutor, null);

                TextView requestTextView = requestRowView.findViewById(R.id.requestTextView);
                Button rejectButton = requestRowView.findViewById(R.id.rejectButton);
                Button approveButton = requestRowView.findViewById(R.id.approveButton);

                requestTextView.setText(request.getRequestDetails());

                rejectButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handleReject(request);
                    }
                });

                approveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handleApprove(request);
                    }
                });

                requestsLayout.addView(requestRowView);
            }
        }
    }

    private void handleReject(Request request) {
        // Perform the reject action for the request
        Toast.makeText(this, "Request Rejected: " + request.getRequestDetails(), Toast.LENGTH_SHORT).show();
        requests.remove(request);
        showRequestsList(); // Update the UI
    }

    private void handleApprove(Request request) {
        // Perform the approve action for the request
        Toast.makeText(this, "Request Approved: " + request.getRequestDetails(), Toast.LENGTH_SHORT).show();
        requests.remove(request);
        showRequestsList(); // Update the UI
    }
}
