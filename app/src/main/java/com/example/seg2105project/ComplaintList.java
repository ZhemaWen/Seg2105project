package com.example.seg2105project;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ComplaintList extends ArrayAdapter<Complaint> {

    private Activity context;
    private List<Complaint> complaints;

    public ComplaintList(Activity context, List<Complaint> complaints) {
        super(context, R.layout.layout_complaints, complaints);
        this.context = context;
        this.complaints = complaints;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_complaints, null, true);

        TextView textViewTutorEmail = listViewItem.findViewById(R.id.textViewTutorId);
        TextView textViewDescription = listViewItem.findViewById(R.id.textViewDescription);

        Complaint complaint = complaints.get(position);
        String tutorId = complaint.getTutorId();

        // Retrieve the tutor email using the tutor ID
        DatabaseReference tutorRef = FirebaseDatabase.getInstance().getReference().child("users").child(tutorId).child("userInfo");
        tutorRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Tutor tutor = dataSnapshot.getValue(Tutor.class);
                    if (tutor != null) {
                        String tutorEmail = tutor.getEmail();
                        textViewTutorEmail.setText(tutorEmail);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });

        textViewDescription.setText(complaint.getDescription());

        return listViewItem;
    }

}
