package com.example.iptt;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private EditText usernameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private Button signUpButton, goToLoginButton;
    private ProgressBar progressBar;
    private TextView passwordStrengthText;

    private View weakBar, moderateBar, strongBar;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_page);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Initialize UI elements
        usernameEditText = findViewById(R.id.username);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        confirmPasswordEditText = findViewById(R.id.confirmPassword);
        signUpButton = findViewById(R.id.signUpButton);
        goToLoginButton = findViewById(R.id.goToLoginButton);
        progressBar = findViewById(R.id.progressBar);
        passwordStrengthText = findViewById(R.id.passwordStrengthText);
        weakBar = findViewById(R.id.weakBar);
        moderateBar = findViewById(R.id.moderateBar);
        strongBar = findViewById(R.id.strongBar);
        EditText passwordInput = findViewById(R.id.password);
        LinearLayout passwordStrengthMeter = findViewById(R.id.passwordStrengthMeter);
        View moderateBar2 = findViewById(R.id.moderateBar2);
        View strongBar1 = findViewById(R.id.strongBar1);
        View strongBar2 = findViewById(R.id.strongBar2);



        // Add password strength checker
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    passwordStrengthMeter.setVisibility(View.VISIBLE);
                    passwordStrengthText.setVisibility(View.VISIBLE);

                    boolean hasUpperCase = s.toString().matches(".*[A-Z].*");
                    boolean hasLowerCase = s.toString().matches(".*[a-z].*");
                    boolean hasNumber = s.toString().matches(".*\\d.*");
                    boolean hasSpecialChar = s.toString().matches(".*[@#$%^&+=!].*");

                    if (s.length() < 4) {
                        weakBar.setVisibility(View.VISIBLE);
                        moderateBar.setVisibility(View.GONE);
                        moderateBar2.setVisibility(View.INVISIBLE);
                        strongBar.setVisibility(View.INVISIBLE);
                        passwordStrengthText.setText("Weak");
                        passwordStrengthText.setTextColor(Color.RED);
                    } else if (s.length() < 8 || !(hasUpperCase && hasLowerCase && hasNumber && hasSpecialChar)) {
                        weakBar.setVisibility(View.GONE);
                        moderateBar.setVisibility(View.VISIBLE);
                        moderateBar2.setVisibility(View.VISIBLE);
                        strongBar1.setVisibility(View.GONE);
                        strongBar2.setVisibility(View.GONE);
                        strongBar.setVisibility(View.INVISIBLE);
                        passwordStrengthText.setText("Moderate");
                        passwordStrengthText.setTextColor(Color.parseColor("#FFA500"));

                    } else {
                        weakBar.setVisibility(View.GONE);
                        moderateBar.setVisibility(View.GONE);
                        moderateBar2.setVisibility(View.GONE);
                        strongBar1.setVisibility(View.VISIBLE);
                        strongBar2.setVisibility(View.VISIBLE);
                        strongBar.setVisibility(View.VISIBLE);
                        passwordStrengthText.setText("Strong");
                        passwordStrengthText.setTextColor(Color.parseColor("#4CAF50"));
                    }
                } else {
                    passwordStrengthMeter.setVisibility(View.GONE);
                    passwordStrengthText.setVisibility(View.GONE);
                    weakBar.setVisibility(View.GONE);
                    moderateBar.setVisibility(View.GONE);
                    strongBar.setVisibility(View.GONE);
                    passwordStrengthText.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        signUpButton.setOnClickListener(v -> validateEmailBeforeSignUp());
        goToLoginButton.setOnClickListener(v -> startActivity(new Intent(SignUpActivity.this, MainActivity.class)));
    }

    private void validateEmailBeforeSignUp() {
        String email = emailEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email) || !isValidEmailFormat(email)) {
            showSnackbar("Invalid email format!", "#F44336");
            return;
        }
        registerUser();
    }

    private boolean isValidEmailFormat(String email) {
        return email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
    }

    private void registerUser() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            showSnackbar("Username cannot be empty!", "#F44336");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showSnackbar("Passwords do not match!", "#F44336");
            return;
        }

        if (password.length() < 8) {
            showSnackbar("Password must be at least 8 characters long!", "#F44336");
            return;
        }

        boolean hasUpperCase = password.matches(".*[A-Z].*");
        boolean hasLowerCase = password.matches(".*[a-z].*");
        boolean hasNumber = password.matches(".*\\d.*");
        boolean hasSpecialChar = password.matches(".*[@#$%^&+=!].*");

        StringBuilder missingRequirements = new StringBuilder("Password must contain: ");
        boolean isInvalid = false;

        if (!hasUpperCase) {
            missingRequirements.append("atlest 1 uppercase letter, ");
            isInvalid = true;
        }
        if (!hasLowerCase) {
            missingRequirements.append("atleast 1 lowercase letter, ");
            isInvalid = true;
        }
        if (!hasNumber) {
            missingRequirements.append("atleast 1 number, ");
            isInvalid = true;
        }
        if (!hasSpecialChar) {
            missingRequirements.append("atleast 1 special character (@#$%^&+=!), ");
            isInvalid = true;
        }

        if (isInvalid) {
            // Remove the trailing comma and space
            showSnackbar(missingRequirements.substring(0, missingRequirements.length() - 2) + "!", "#F44336");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(emailEditText.getText().toString().trim(), password)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            user.sendEmailVerification();
                            saveUserToDatabase(user.getUid(), username, user.getEmail());
                            showSnackbar("Verification email sent!", "#4CAF50");
                            new android.os.Handler().postDelayed(() -> {
                                startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                                finish();
                            }, 3000);
                        }
                    } else {
                        showSnackbar("Sign-up failed!", "#F44336");
                    }
                });
    }

    private void saveUserToDatabase(String userId, String username, String email) {
        Map<String, Object> user = new HashMap<>();
        user.put("username", username);
        user.put("email", email);
        databaseReference.child(userId).setValue(user);
    }


    private void showSnackbar(String message, String colorHex) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        snackbar.setBackgroundTint(Color.parseColor(colorHex));
        snackbar.setTextColor(Color.WHITE);
        snackbar.show();


    }
}
