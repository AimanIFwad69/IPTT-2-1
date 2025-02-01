package com.example.iptt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // Declare UI elements
    private EditText usernameEditText, passwordEditText;
    private Button loginButton;
    private TextView statusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);


        // Set click listener for the login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // intent to navigate to a new activity
                Intent intent = new Intent(MainActivity.this, HomePageActivity.class);
                startActivity(intent); // Start the new activity

                // Get input values
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // Validate login (simple hardcoded check)
                if (username.equals("admin") && password.equals("1234")) {
                    statusTextView.setText("Status: Logged in");
                    Toast.makeText(MainActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                } else {
                    statusTextView.setText("Status: Login failed");
                    Toast.makeText(MainActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
