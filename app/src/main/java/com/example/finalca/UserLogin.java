package com.example.finalca;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class UserLogin extends AppCompatActivity {

    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private Button mLoginButton;
    private TextView mNoAccountTextView;
    private ProgressBar mProgressBar;


    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        // Initialize views
        mEmailEditText = findViewById(R.id.email);
        mPasswordEditText = findViewById(R.id.password);
        mLoginButton = findViewById(R.id.loginBtn);
        mNoAccountTextView = findViewById(R.id.noAccount);
        mProgressBar = findViewById(R.id.progressBar);


        // Initialize Firebase authentication and database references
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Set click listener for login button
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get user input
                String email = mEmailEditText.getText().toString().trim();
                String password = mPasswordEditText.getText().toString().trim();

                // Validate input fields
                if (email.isEmpty()) {
                    mEmailEditText.setError("Please enter your email");
                    mEmailEditText.requestFocus();
                    return;
                }
                if (password.isEmpty()) {
                    mPasswordEditText.setError("Please enter your password");
                    mPasswordEditText.requestFocus();
                    return;
                }

                // Show progress bar
                mProgressBar.setVisibility(View.VISIBLE);

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(UserLogin.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, get user data from Firebase Realtime Database
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {
                                        mDatabase.child("user").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                // Hide progress bar
                                                mProgressBar.setVisibility(View.GONE);

                                                // Check if user data exists in database
                                                if (snapshot.exists()) {
                                                    // User exists, save user data to UserSingleton instance
                                                    User currentUser = snapshot.getValue(User.class);
                                                    UserSingleton.getInstance().setUser(currentUser);

                                                    // Navigate to main activity
                                                    Intent intent = new Intent(UserLogin.this, MainActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                } else {
                                                    // User does not exist, sign out and show error message
                                                    mAuth.signOut();
                                                    Toast.makeText(UserLogin.this, "Authentication failed: User data does not exist", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                // Hide progress bar
                                                mProgressBar.setVisibility(View.GONE);

                                                // Show error message
                                                Toast.makeText(UserLogin.this, "Authentication failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                } else {
                                    // Sign in failed and show error message
                                    Toast.makeText(UserLogin.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });

        // Set click listener for "no account" text view
        mNoAccountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to user registration activity
                Intent intent = new Intent(UserLogin.this, UserRegister.class);
                startActivity(intent);
            }
        });
    }}

