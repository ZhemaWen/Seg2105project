package com.example.seg2105project;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import android.app.Activity;

import java.util.List;


public class ExampleUnitTest {
    @Test
    public void testValidAdminCredentials() {
        String email = "admin@4code.com";
        String password = "4code4code";

        LogIn activity = new LogIn();
        boolean isValidAdmin = activity.isValidAdmin(email, password);

        assertEquals(true,isValidAdmin);
    }

    @Test
    public void testInvalidAdminCredentials() {
        String email = "admin@4code.com";
        String password = "incorrectpassword";

        LogIn activity = new LogIn();
        boolean isValidAdmin = activity.isValidAdmin(email, password);

        assertEquals(false,isValidAdmin);
    }

    @Test
    public void testEmptyAdminCredentials() {
        String email = "";
        String password = "";

        LogIn activity = new LogIn();
        boolean isValidAdmin = activity.isValidAdmin(email, password);

        assertEquals(false,isValidAdmin);
    }

    @Test
    public void testRegularUserCredentials() {
        String email = "regularuser@example.com";
        String password = "password123";

        LogIn activity = new LogIn();
        boolean isValidAdmin = activity.isValidAdmin(email, password);

        assertEquals(false,isValidAdmin);
    }
    

    @Test
    public void testAddToComplaintList() {
        Complaint newComplaint = new Complaint() ;
        Activity context=null;


        ComplaintList activity = new ComplaintList(context, (List<Complaint>) newComplaint);
        boolean isTopicAdded = activity.isComplaintAdded(newComplaint);
        assertEquals(true,isTopicAdded);

    }

    @Test
    public void testDeleteFromComplaintList() {
        Complaint newComplaint = new Complaint() ;
        Activity context=null;


        ComplaintList activity = new ComplaintList(context, (List<Complaint>) newComplaint);
        boolean isTopicAdded = activity.isComplaintDeleted(newComplaint);
        assertEquals(false,isTopicAdded);

    }

    @Test
    public void testAddTopicToTutorProfile() {
        Topic newTopic = new Topic("1234", "Physics", "3", "specialization") ;
        Activity context=null;


        TopicList activity = new TopicList(context, (List<Topic>) newTopic);
        boolean isTopicAdded = activity.isTopicAdded(newTopic);
        assertEquals(true,isTopicAdded);


    }

    @Test
    public void testDeleteTopicFromTutorProfile() {
        Topic newTopic = new Topic("1234", "Physics", "3", "specialization") ;
        Activity context=null;


        TopicList activity = new TopicList(context, (List<Topic>) newTopic);
        boolean isTopicDeleted = activity.isTopicDeleted(newTopic);
        assertEquals(false,isTopicDeleted);

    }
}
