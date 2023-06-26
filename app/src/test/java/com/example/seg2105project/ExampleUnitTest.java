package com.example.seg2105project;

import org.junit.Test;
import static org.junit.Assert.assertEquals;




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
}
