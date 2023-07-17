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
    private boolean review;

    public Request() {
        // Default constructor required for Firebase
        status = "Pending"; // Initialize status as "Pending"
    }

    public Request(Topic topic, String requestId, String tutorId, String studentId, String date, String timeSlot) {
        this.topic = topic;
        this.requestId = requestId;
        this.tutorId = tutorId;
        this.studentId = studentId;
        status = "Pending"; // Initialize status as "Pending"
        this.timeSlot=timeSlot;
        this.date=date;
        this.review=false;
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

    public boolean isReview() {
        return review;
    }

    public String getDate(){
        return date;
    }
    public void setReview(boolean review){
        this.review=review;
    }
}


