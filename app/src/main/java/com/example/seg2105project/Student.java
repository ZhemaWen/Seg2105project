package com.example.seg2105project;

public class Student {
    private String firstName, lastName, email, address, cardNumber, holderName, cvv;

    public Student() {

    }

    public Student(String firstName, String lastName, String email, String address, String cardNumber, String holderName, String cvv){
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.address = address;
        this.cardNumber = cardNumber;
        this.holderName = holderName;
        this.cvv = cvv;
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

    public String getAddress() {
        return address;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getHolderName() {
        return holderName;
    }
    public String getCvv() {
        return cvv;
    }


}
