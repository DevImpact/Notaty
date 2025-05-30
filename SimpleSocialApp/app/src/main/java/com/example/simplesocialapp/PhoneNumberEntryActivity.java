package com.example.simplesocialapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

// Import libphonenumber (ensure you have the dependency)
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

public class PhoneNumberEntryActivity extends AppCompatActivity {

    private static final String TAG = "PhoneNumberEntry";
    public static final String EXTRA_VERIFICATION_ID = "com.example.simplesocialapp.VERIFICATION_ID";
    public static final String EXTRA_PHONE_NUMBER = "com.example.simplesocialapp.PHONE_NUMBER";


    private EditText editTextPhoneNumber;
    private Button buttonSendOtp;
    private ProgressBar progressBarSendOtp;

    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private PhoneNumberUtil phoneUtil;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number_entry);

        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        buttonSendOtp = findViewById(R.id.buttonSendOtp);
        progressBarSendOtp = findViewById(R.id.progressBarSendOtp);

        mAuth = FirebaseAuth.getInstance();
        try {
            phoneUtil = PhoneNumberUtil.createInstance(this);
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize PhoneNumberUtil", e);
            Toast.makeText(this, "Error initializing phone services. Please restart the app.", Toast.LENGTH_LONG).show();
            finish(); // Or handle more gracefully
            return;
        }


        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);
                Toast.makeText(PhoneNumberEntryActivity.this, "Verification Completed Automatically.", Toast.LENGTH_SHORT).show();
                // Sign in with the credential
                // signInWithPhoneAuthCredential(credential); // You would implement this method
                // For now, conceptual navigation or just log.
                // If auto-retrieval, mVerificationId might not be set here, credential has the info.
                // We'll move to OtpVerificationActivity which expects verificationId.
                // This flow might need adjustment if auto-completion is the primary path.
                // For this task, focus is on manual OTP entry.
                progressBarSendOtp.setVisibility(View.GONE);
                buttonSendOtp.setVisibility(View.VISIBLE);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.w(TAG, "onVerificationFailed", e);
                Toast.makeText(PhoneNumberEntryActivity.this, "OTP sending failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                progressBarSendOtp.setVisibility(View.GONE);
                buttonSendOtp.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                Log.d(TAG, "onCodeSent:" + verificationId);
                Toast.makeText(PhoneNumberEntryActivity.this, "OTP sent successfully.", Toast.LENGTH_SHORT).show();

                mVerificationId = verificationId;
                // Save token if you need to resend OTP: mResendToken = token;

                Intent intent = new Intent(PhoneNumberEntryActivity.this, OtpVerificationActivity.class);
                intent.putExtra(EXTRA_VERIFICATION_ID, verificationId);
                intent.putExtra(EXTRA_PHONE_NUMBER, editTextPhoneNumber.getText().toString().trim()); // Pass the phone number too
                startActivity(intent);

                progressBarSendOtp.setVisibility(View.GONE);
                buttonSendOtp.setVisibility(View.VISIBLE);
            }
        };

        buttonSendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumberStr = editTextPhoneNumber.getText().toString().trim();

                if (TextUtils.isEmpty(phoneNumberStr)) {
                    Toast.makeText(PhoneNumberEntryActivity.this, "Please enter a phone number.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validate phone number format (basic validation using libphonenumber)
                // For simplicity, assuming US numbers if no country code, but it's better to use a country code picker.
                // The library is powerful, for this example a basic check:
                try {
                    // Assuming default region if no "+" is present. For international, number should be E.164 format.
                    // For this example, let's assume user includes '+' and country code.
                    if (!phoneNumberStr.startsWith("+")) {
                         Toast.makeText(PhoneNumberEntryActivity.this, "Please include country code (e.g., +1 for US).", Toast.LENGTH_LONG).show();
                         return;
                    }
                    Phonenumber.PhoneNumber swissNumberProto = phoneUtil.parse(phoneNumberStr, null); // null for region if number has '+'
                    if (!phoneUtil.isValidNumber(swissNumberProto)) {
                        Toast.makeText(PhoneNumberEntryActivity.this, "Invalid phone number format.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (Exception e) { // NumberParseException
                    Log.e(TAG, "PhoneNumber Parsing error: " + e.getMessage());
                    Toast.makeText(PhoneNumberEntryActivity.this, "Invalid phone number format.", Toast.LENGTH_SHORT).show();
                    return;
                }


                progressBarSendOtp.setVisibility(View.VISIBLE);
                buttonSendOtp.setVisibility(View.GONE);

                PhoneAuthOptions options =
                        PhoneAuthOptions.newBuilder(mAuth)
                                .setPhoneNumber(phoneNumberStr)       // Phone number to verify
                                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                .setActivity(PhoneNumberEntryActivity.this)                 // Activity (for callback binding)
                                .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                                .build();
                PhoneAuthProvider.verifyPhoneNumber(options);
            }
        });
    }
}
