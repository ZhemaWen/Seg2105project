package com.example.seg2105project;

import java.util.Date;

public class Request {
    private Topic topic;
    private String requestId;
    private String tutorId;
    private String studentId;
    private String status;
    private String timeSlot;
    private String date;
    private String requestDetails;
    private boolean isApproved;
    private boolean isRejected;

    public Request() {
        // Default constructor required for Firebase
        status = "Pending"; // Initialize status as "Pending"
    }

    public Request(Topic topic, String requestId, String tutorId, String studentId, String date, String timeSlot, String requestDetails) {
        this.topic = topic;
        this.requestId = requestId;
        this.tutorId = tutorId;
        this.studentId = studentId;
        status = "Pending"; // Initialize status as "Pending"
        this.timeSlot=timeSlot;
        this.date=date;
        this.requestDetails = requestDetails;
        this.isApproved = false;
        this.isRejected = false;
    }

    public Request(Topic topic, String requestId, String tutorId, String uid, String dateString, String timeSlot) {
    }

    public Request(String s) {
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getTutorId() {
        return tutorId;
    }

    public void setTutorId(String tutorId) {
        this.tutorId = tutorId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimeSlot() {
        return timeSlot;
    }



    public String getDate(){
        return date;
    }



    public String getRequestDetails() {
        return requestDetails;
    }

    public void setRequestDetails(String requestDetails) {
        this.requestDetails = requestDetails;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }

    public boolean isRejected() {
        return isRejected;
    }

    public void setRejected(boolean rejected) {
        isRejected = rejected;
    }
}

