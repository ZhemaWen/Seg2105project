package com.example.seg2105project;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class StudentRegister extends AppCompatActivity {
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText addressEditText;
    private Button registerButton;
    private EditText cardNumberEditText;
    private EditText cvvEditText;
    private EditText holderNameEditText;

    FirebaseAuth mAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_register);
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        addressEditText = findViewById(R.id.addressEditText);
        registerButton = findViewById(R.id.registerButton);
        cardNumberEditText = findViewById(R.id.cardNumberEditText);
        cvvEditText = findViewById(R.id.cvvEditText);
        holderNameEditText = findViewById(R.id.holderNameEditText);


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }
    boolean isEmailAddressValid(Student student) {
        if (emailEditText.equals(student.getEmail())) {
            return true;
        }
        return false;
    }

    boolean isCardNumberValid(Student student) {
        if (cardNumberEditText.equals(student.getCardNumber())) {
            return true;
        }
        return false;
    }

    private void register() {
        // Get input values
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();
        String cardNumber = cardNumberEditText.getText().toString().trim();
        String holderName = holderNameEditText.getText().toString().trim();
        String cvv = cvvEditText.getText().toString().trim();

        // Perform input validation
        if (TextUtils.isEmpty(firstName)) {
            Toast.makeText(this, "Please enter your first name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(lastName)) {
            Toast.makeText(this, "Please enter your last name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter your email address", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(address)) {
            Toast.makeText(this, "Please enter your address", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(cardNumber)) {
            Toast.makeText(this, "Please enter the card number", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!cardNumber.matches("[0-9]+")) {
            Toast.makeText(this, "Card number should contain only numbers", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(holderName)) {
            Toast.makeText(this, "Please enter the cardholder name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(cvv)) {
            Toast.makeText(this, "Please enter the CVV", Toast.LENGTH_SHORT).show();
            return;
        }
        if (cvv.length() != 3) {
            Toast.makeText(this, "CVV must be 3 digit", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        if (task.isSuccessful()) {
                            SignInMethodQueryResult result = task.getResult();
                            if (result != null && result.getSignInMethods() != null && result.getSignInMethods().size() > 0) {
                                // Email is already registered
                                Toast.makeText(StudentRegister.this, "This email is already registered", Toast.LENGTH_SHORT).show();
                            } else {
                                // Email is not registered, proceed with registration
                                mAuth.createUserWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(StudentRegister.this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                                    // Get the current user's ID
                                                    String userId = mAuth.getCurrentUser().getUid();

                                                    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
                                                    Student student = new Student(firstName,  lastName, email,  address,  cardNumber, holderName,  cvv);
                                                    usersRef.child(userId).child("userType").setValue("Student");
                                                    usersRef.child(userId).child("studentName").setValue(firstName +" "+ lastName);
                                                    Toast.makeText(StudentRegister.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(StudentRegister.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(StudentRegister.this, "Failed to check email registration status", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}