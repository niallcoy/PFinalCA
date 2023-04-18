package com.example.finalca;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class AdminRegister extends AppCompatActivity {

    private EditText mNameEditText;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private EditText mEmployeeID;
    private Button mRegisterButton;
    private ProgressBar mProgressBar;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_register);

        // Initialize views
        mNameEditText = findViewById(R.id.rName);
        mEmailEditText = findViewById(R.id.rEmail);
        mPasswordEditText = findViewById(R.id.rPassword);
        mRegisterButton = findViewById(R.id.registerButton);
        mEmployeeID = findViewById(R.id.rEmployeeID);
        mProgressBar = findViewById(R.id.progressBar);

        // Initialize Firebase database reference
        mDatabase = AdminSingleton.getInstance().getReference(); // using singleton from AdminSingleton class
        // Set click listener for register button
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get user input
                String name = mNameEditText.getText().toString().trim();
                String email = mEmailEditText.getText().toString().trim();
                String password = mPasswordEditText.getText().toString().trim();
                String employeeID = mEmployeeID.getText().toString().trim();
                // Validate input fields
                if (name.isEmpty()) {
                    mNameEditText.setError("Please enter your name");
                    mNameEditText.requestFocus();
                    return;
                }
                if (email.isEmpty()) {
                    mEmailEditText.setError("Please enter your email");
                    mEmailEditText.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    mEmailEditText.setError("Please enter a valid email address");
                    mEmailEditText.requestFocus();
                    return;
                }
                if (password.isEmpty()) {
                    mPasswordEditText.setError("Please enter a password");
                    mPasswordEditText.requestFocus();
                    return;
                }
                if (employeeID.isEmpty()) {
                    mEmployeeID.setError("Please enter your employee ID");
                    mEmployeeID.requestFocus();
                    return;
                }

                // Create hashmap for admin data
                HashMap<String, Object> adminMap = new HashMap<>();
                adminMap.put("name", name);
                adminMap.put("email", email);
                adminMap.put("password", password);
                adminMap.put("employeeID", employeeID);

                // Show progress bar
                mProgressBar.setVisibility(View.VISIBLE);

                // Save data to Firebase Realtime Database
                mDatabase.child("admin").setValue(adminMap,  new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        // Hide progress bar
                        mProgressBar.setVisibility(View.GONE);

                        // Show toast message indicating success or failure
                        if (databaseError == null) {
                            Toast.makeText(AdminRegister.this, "Registration successful", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AdminRegister.this, "Registration failed: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
