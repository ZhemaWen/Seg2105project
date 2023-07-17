package com.example.seg2105project;

public class Tutor {
    private  String tutorID;
    private String firstName;
    private String lastName;
    private String email;
    private String educationLevel;
    private String nativeLanguage;
    private String description;
    private boolean isSuspended;
    private double hourlyRate;
    private int lessonsGiven;
    private double lessonsRate;

    public Tutor() {
        // Default constructor required for Firebase database operations
    }

    public Tutor(String tutorId,String firstName, String lastName, String email, String educationLevel, String nativeLanguage, String description) {
        this.tutorID = tutorId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.educationLevel = educationLevel;
        this.nativeLanguage = nativeLanguage;
        this.description = description;
        this.isSuspended = false;
        this.hourlyRate=0;
        this.lessonsGiven=0;
        this.lessonsRate=0;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getEducationLevel() {
        return educationLevel;
    }

    public String getNativeLanguage() {
        return nativeLanguage;
    }

    public String getDescription() {
        return description;
    }
    public boolean getIsSuspended(){return isSuspended;}

    public double getHourlyRate() {
        return hourlyRate;
    }

    public double getLessonsRate() {
        return lessonsRate;
    }

    public int getLessonsGiven() {
        return lessonsGiven;
    }
}

