package com.example.iptt;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private Button loginButton, signUpButton;
    private TextView forgotPasswordTextView, statusTextView;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Initialize UI elements
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        signUpButton = findViewById(R.id.signUpButton);
        forgotPasswordTextView = findViewById(R.id.forgotPasswordButton);
        statusTextView = findViewById(R.id.statusTextView);

        // Login button click listener
        loginButton.setOnClickListener(v -> loginUser());



        // Sign-up button click listener
        signUpButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        // Forgot Password click listener
        forgotPasswordTextView.setOnClickListener(v -> showForgotPasswordDialog());
    }

    private void loginUser() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(username)) {
            usernameEditText.setError("Username is required!");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required!");
            return;
        }

        // Retrieve email from Firebase Database using username
        databaseReference.orderByChild("username").equalTo(username)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                String email = userSnapshot.child("email").getValue(String.class);
                                authenticateUser(email, password);
                                return;
                            }
                        } else {
                            usernameEditText.setError("Username not found!");
                            usernameEditText.requestFocus();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(MainActivity.this, "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void authenticateUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null && user.isEmailVerified()) {
                            Log.d("MainActivity", "✅ Login Successful, Navigating to HomePageActivity");

                            Toast.makeText(MainActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                            statusTextView.setText("Status: Logged in");

                            // ✅ Correct Intent Usage
                            Intent intent = new Intent(MainActivity.this, HomePageActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish(); // Close MainActivity
                        } else {
                            Log.e("MainActivity", "❌ Email not verified");

                            mAuth.signOut();
                            Toast.makeText(MainActivity.this, "Please verify your email before logging in.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Log.e("MainActivity", "❌ Login failed: " + task.getException().getMessage());

                        statusTextView.setText("Status: Login failed");
                        Toast.makeText(MainActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                    }
                });
    }



    private void showForgotPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reset Password");

        // Create an EditText for email input
        final EditText emailInput = new EditText(this);
        emailInput.setHint("Enter your registered email");
        builder.setView(emailInput);

        // Set up dialog buttons
        builder.setPositiveButton("Reset", (dialog, which) -> {
            String email = emailInput.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(MainActivity.this, "Email is required!", Toast.LENGTH_SHORT).show();
            } else {
                sendPasswordResetEmail(email);
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    private void sendPasswordResetEmail(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Password reset link sent to your email!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
