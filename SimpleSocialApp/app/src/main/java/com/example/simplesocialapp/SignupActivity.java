package com.example.simplesocialapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    private EditText editTextUsername;
    private EditText editTextEmailSignup;
    private EditText editTextPasswordSignup;
    private Button buttonSignup;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextEmailSignup = findViewById(R.id.editTextEmailSignup);
        editTextPasswordSignup = findViewById(R.id.editTextPasswordSignup);
        buttonSignup = findViewById(R.id.buttonSignup);
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextUsername.getText().toString().trim();
                String email = editTextEmailSignup.getText().toString().trim();
                String password = editTextPasswordSignup.getText().toString().trim();

                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(SignupActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Basic validation for email format (optional, but good practice)
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(SignupActivity.this, "Invalid email format", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                // Storing user credentials.
                // Key: email (as LoginActivity will use email to retrieve password)
                // Value: password (plain text for this example, HASH in real app)
                // Also storing username separately associated with the email for potential future use.
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(email + "_password", password);
                editor.putString(email + "_username", username);
                // In a real app, password should be hashed: e.g., hashPassword(password)
                editor.apply();

                Toast.makeText(SignupActivity.this, "Sign-up Successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Finish SignupActivity so user can't navigate back to it
            }
        });
    }
    // In a real application, you would have a method like this:
    // private String hashPassword(String password) {
    //     // Implement password hashing (e.g., using bcrypt or Argon2)
    //     return hashedPassword;
    // }
}
