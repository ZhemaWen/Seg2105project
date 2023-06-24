package com.example.seg2105project;

public class Tutor {
    private  String tutorID;
    private String firstName;
    private String lastName;
    private String email;
    private String educationLevel;
    private String nativeLanguage;
    private String description;

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
}

