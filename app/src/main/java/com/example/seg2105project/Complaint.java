package com.example.seg2105project;

import java.util.Calendar;
import java.util.Date;

public class Complaint {
    private String complaintId;
    private String tutorId;
    private String description;
    private Date timestamp;
    private boolean suspended;


    public Complaint() {
        // Default constructor required for Firebase
    }

    public Complaint(String complaintId, String tutorId, String description, Date timestamp) {
        this.complaintId = complaintId;
        this.tutorId = tutorId;
        this.description = description;
        this.timestamp = timestamp;
        this.suspended = false;
    }

    // Getters and setters

    public String getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(String complaintId) {
        this.complaintId = complaintId;
    }

    public String getTutorId() {
        return tutorId;
    }

    public void setTutorId(String tutorId) {
        this.tutorId = tutorId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public boolean isSuspended() {
        return suspended;
    }

    public void setSuspended(boolean suspended) {
        this.suspended = suspended;
    }




}
