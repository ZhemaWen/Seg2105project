package com.example.seg2105project;

import java.util.Date;

public class Review {
    private String reviewId;
    private String studentName;
    private Date date;
    private Request request;
    private int rating;
    private String reviewText;

    public Review() {
        // Default constructor required for Firebase
    }

    public Review(String reviewId,String studentName, Date date, Request request, int rating, String reviewText) {
        this.reviewId=reviewId;
        this.studentName = studentName;
        this.date = date;
        this.request = request;
        this.rating = rating;
        this.reviewText = reviewText;
    }
    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }
    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request){
        this.request=request;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }
}

