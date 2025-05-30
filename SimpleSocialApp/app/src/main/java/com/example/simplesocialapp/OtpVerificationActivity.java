package com.example.simplesocialapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class OtpVerificationActivity extends AppCompatActivity {

    private static final String TAG = "OtpVerification";
    public static final String EXTRA_USER_UID = "com.example.simplesocialapp.USER_UID";
    public static final String EXTRA_PHONE_NUMBER_FINAL = "com.example.simplesocialapp.PHONE_NUMBER_FINAL";


    private EditText editTextOtp;
    private Button buttonVerifyOtp;
    private ProgressBar progressBarVerifyOtp;
    private TextView textViewPhoneNumberDisplay;

    private FirebaseAuth mAuth;
    private String mVerificationId;
    private String mPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        editTextOtp = findViewById(R.id.editTextOtp);
        buttonVerifyOtp = findViewById(R.id.buttonVerifyOtp);
        progressBarVerifyOtp = findViewById(R.id.progressBarVerifyOtp);
        textViewPhoneNumberDisplay = findViewById(R.id.textViewPhoneNumberDisplay);

        mAuth = FirebaseAuth.getInstance();

        mVerificationId = getIntent().getStringExtra(PhoneNumberEntryActivity.EXTRA_VERIFICATION_ID);
        mPhoneNumber = getIntent().getStringExtra(PhoneNumberEntryActivity.EXTRA_PHONE_NUMBER);

        if (mVerificationId == null || mVerificationId.isEmpty()) {
            Toast.makeText(this, "Verification ID not found. Please try again.", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Verification ID is null or empty.");
            finish();
            return;
        }

        if (mPhoneNumber != null && !mPhoneNumber.isEmpty()) {
            textViewPhoneNumberDisplay.setText(mPhoneNumber);
        } else {
            textViewPhoneNumberDisplay.setText("Phone number not available");
        }

        buttonVerifyOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otpString = editTextOtp.getText().toString().trim();

                if (TextUtils.isEmpty(otpString)) {
                    Toast.makeText(OtpVerificationActivity.this, "Please enter OTP.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (otpString.length() < 6) { // Basic validation for OTP length
                    Toast.makeText(OtpVerificationActivity.this, "Please enter a valid 6-digit OTP.", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBarVerifyOtp.setVisibility(View.VISIBLE);
                buttonVerifyOtp.setVisibility(View.GONE);

                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otpString);
                signInWithPhoneAuthCredential(credential);
            }
        });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            Toast.makeText(OtpVerificationActivity.this, "Phone number verified successfully!", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = task.getResult().getUser();

                            if (user != null) {
                                // Navigate to DetailedProfileSetupActivity
                                Intent intent = new Intent(OtpVerificationActivity.this, DetailedProfileSetupActivity.class);
                                intent.putExtra(EXTRA_USER_UID, user.getUid());
                                intent.putExtra(EXTRA_PHONE_NUMBER_FINAL, mPhoneNumber); // Pass the phone number
                                // Clear previous activities from the stack before starting profile setup
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish(); // Finish OtpVerificationActivity
                            } else {
                                // Should not happen if task is successful
                                Log.e(TAG, "User is null after successful sign in.");
                                Toast.makeText(OtpVerificationActivity.this, "Verification successful, but failed to get user info.", Toast.LENGTH_LONG).show();
                                progressBarVerifyOtp.setVisibility(View.GONE);
                                buttonVerifyOtp.setVisibility(View.VISIBLE);
                            }

                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            String errorMessage = "OTP verification failed.";
                            if (task.getException() != null) {
                                errorMessage += " " + task.getException().getMessage();
                            }
                            Toast.makeText(OtpVerificationActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                            progressBarVerifyOtp.setVisibility(View.GONE);
                            buttonVerifyOtp.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }
}
