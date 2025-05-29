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

public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogin;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "Email and password cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                String storedPasswordKey = email + "_password";
                String storedPassword = sharedPreferences.getString(storedPasswordKey, null);

                if (storedPassword != null && storedPassword.equals(password)) {
                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    
                    // Save the logged-in user's email to pass to MainFeedActivity
                    // Optionally, could also save to SharedPreferences if needed globally by other activities
                    // SharedPreferences.Editor editor = sharedPreferences.edit();
                    // editor.putString("currentUserEmail", email);
                    // editor.apply();

                    Intent intent = new Intent(LoginActivity.this, MainFeedActivity.class);
                    intent.putExtra(MainFeedActivity.EXTRA_USER_EMAIL_LOGIN, email); // Pass email to MainFeedActivity
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
