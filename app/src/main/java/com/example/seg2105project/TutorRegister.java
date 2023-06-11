package com.example.seg2105project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TutorRegister extends AppCompatActivity {
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Spinner educationLevelSpinner;
    private Spinner nativeLanguageSpinner;
    private EditText descriptionEditText;
    private Button registerButton;
    private FirebaseAuth mAuth;
    Button BSelectImage;
    private ImageView IVPreviewImage;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_register);
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        educationLevelSpinner = findViewById(R.id.educationLevelSpinner);
        nativeLanguageSpinner = findViewById(R.id.nativeLanguageSpinner);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        registerButton = findViewById(R.id.registerButton);
        BSelectImage = findViewById(R.id.addProfilePictureButton);
        IVPreviewImage = findViewById(R.id.IVPreviewImage);

        // handle the Choose Image button to trigger
        // the image chooser function
        BSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooser();
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }
    void imageChooser() {

        // create an instance of the
        // intent of the type image
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        // pass the constant to compare it
        // with the returned requestCode
        startActivityForResult(Intent.createChooser(i, "Select Picture"), 1);
    }

    // this function is triggered when user
    // selects the image from the imageChooser
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == 1) {
                // Get the url of the image from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // update the preview image in the layout
                    IVPreviewImage.setImageURI(selectedImageUri);
                }
            }
        }
    }
    private void register() {
        // Get input values
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String educationLevel = educationLevelSpinner.getSelectedItem().toString().trim();
        String nativeLanguage = nativeLanguageSpinner.getSelectedItem().toString().trim();
        String description = descriptionEditText.getText().toString().trim();

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

        if (TextUtils.isEmpty(educationLevel)) {
            Toast.makeText(this, "Please select your education level", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(nativeLanguage)) {
            Toast.makeText(this, "Please select your native language", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Please enter a short description", Toast.LENGTH_SHORT).show();
            return;
        }

        if (description.length() > 600) {
            Toast.makeText(this, "Description must be least than 600 characters", Toast.LENGTH_SHORT).show();
            return;
        }


        // Perform tutor registration using Firebase Authentication
        mAuth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        if (task.isSuccessful()) {

                            SignInMethodQueryResult result = task.getResult();
                            if (result != null && result.getSignInMethods() != null && result.getSignInMethods().size() > 0) {
                                // Email is already registered
                                Toast.makeText(TutorRegister.this, "This email is already registered", Toast.LENGTH_SHORT).show();
                            } else {
                                // Email is not registered, proceed with registration
                                mAuth.createUserWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(TutorRegister.this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                                    // Get the current user's ID
                                                    String userId = mAuth.getCurrentUser().getUid();

                                                    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");

                                                    usersRef.child(userId).child("userType").setValue("Tutor");

                                                    Toast.makeText(TutorRegister.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(TutorRegister.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(TutorRegister.this, "Failed to check email registration status", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
