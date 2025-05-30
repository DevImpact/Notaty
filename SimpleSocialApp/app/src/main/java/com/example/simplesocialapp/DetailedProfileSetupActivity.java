package com.example.simplesocialapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DetailedProfileSetupActivity extends AppCompatActivity {

    private static final String TAG = "DetailedProfileSetup";

    private EditText editTextFirstName, editTextLastName, editTextDateOfBirth, editTextCountry;
    private EditText editTextAddress, editTextHobbies, editTextJob;
    private Spinner spinnerRelationshipStatus;
    private Button buttonConfirmProfile;
    private ProgressBar progressBarProfileSetup;

    private String userUid;
    private String phoneNumber; // Received from OtpVerificationActivity

    private PhoneNumberUtil phoneNumberUtil;
    private Calendar calendar;

    private FirebaseFirestore db; // Firestore instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_profile_setup);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        userUid = getIntent().getStringExtra(OtpVerificationActivity.EXTRA_USER_UID);
        phoneNumber = getIntent().getStringExtra(OtpVerificationActivity.EXTRA_PHONE_NUMBER_FINAL);

        if (userUid == null || userUid.isEmpty()) {
            Toast.makeText(this, "User ID not found. Cannot setup profile.", Toast.LENGTH_LONG).show();
            Log.e(TAG, "User UID is null or empty.");
            finish();
            return;
        }
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            Toast.makeText(this, "Phone number not found.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Phone number is null or empty.");
        }

        editTextFirstName = findViewById(R.id.editTextFirstName);
        editTextLastName = findViewById(R.id.editTextLastName);
        editTextDateOfBirth = findViewById(R.id.editTextDateOfBirth);
        editTextCountry = findViewById(R.id.editTextCountry);
        editTextAddress = findViewById(R.id.editTextAddress);
        editTextHobbies = findViewById(R.id.editTextHobbies);
        editTextJob = findViewById(R.id.editTextJob);
        spinnerRelationshipStatus = findViewById(R.id.spinnerRelationshipStatus);
        buttonConfirmProfile = findViewById(R.id.buttonConfirmProfile);
        progressBarProfileSetup = findViewById(R.id.progressBarProfileSetup);
        calendar = Calendar.getInstance();

        phoneNumberUtil = PhoneNumberUtil.getInstance();
        prefillCountry();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.relationship_statuses, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRelationshipStatus.setAdapter(adapter);

        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        editTextDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(DetailedProfileSetupActivity.this, dateSetListener,
                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        buttonConfirmProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInput()) {
                    saveProfileToFirestore();
                }
            }
        });
    }

    private void prefillCountry() {
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            try {
                Phonenumber.PhoneNumber pn = phoneNumberUtil.parse(phoneNumber, null);
                String regionCode = phoneNumberUtil.getRegionCodeForNumber(pn);
                if (regionCode != null && !regionCode.isEmpty()) {
                    String countryName = new Locale("", regionCode).getDisplayCountry();
                    editTextCountry.setText(countryName);
                }
            } catch (NumberParseException e) {
                Log.e(TAG, "Error parsing phone number to get region: " + e.getMessage());
            }
        }
    }

    private void updateLabel() {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        editTextDateOfBirth.setText(sdf.format(calendar.getTime()));
    }

    private boolean validateInput() {
        if (TextUtils.isEmpty(editTextFirstName.getText().toString().trim())) {
            editTextFirstName.setError("First name is required.");
            Toast.makeText(this, "First name is required.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(editTextLastName.getText().toString().trim())) {
            editTextLastName.setError("Last name is required.");
            Toast.makeText(this, "Last name is required.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(editTextDateOfBirth.getText().toString().trim())) {
            Toast.makeText(this, "Date of birth is required.", Toast.LENGTH_SHORT).show();
            return false;
        }
         if (userUid == null || userUid.isEmpty()) { // Re-check userUid before attempting save
            Toast.makeText(this, "Critical error: User ID missing. Cannot save profile.", Toast.LENGTH_LONG).show();
            Log.e(TAG, "User UID is null or empty at the point of saving profile.");
            return false;
        }
        return true;
    }

    private void saveProfileToFirestore() {
        progressBarProfileSetup.setVisibility(View.VISIBLE);
        buttonConfirmProfile.setVisibility(View.GONE);

        String firstName = editTextFirstName.getText().toString().trim();
        String lastName = editTextLastName.getText().toString().trim();
        String dob = editTextDateOfBirth.getText().toString().trim();
        String country = editTextCountry.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();
        String relationshipStatus = spinnerRelationshipStatus.getSelectedItem().toString();
        String hobbies = editTextHobbies.getText().toString().trim();
        String job = editTextJob.getText().toString().trim();

        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("firstName", firstName);
        userProfile.put("lastName", lastName);
        userProfile.put("dateOfBirth", dob);
        userProfile.put("country", country);
        userProfile.put("address", address);
        userProfile.put("hobbies", hobbies);
        userProfile.put("job", job);
        userProfile.put("relationshipStatus", relationshipStatus);
        userProfile.put("phoneNumber", phoneNumber); // Storing the phone number
        userProfile.put("createdAt", FieldValue.serverTimestamp());
        // userProfile.put("profilePictureUrl", ""); // Placeholder for future

        db.collection("users").document(userUid).set(userProfile)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBarProfileSetup.setVisibility(View.GONE);
                        // buttonConfirmProfile can remain hidden as we navigate away
                        Toast.makeText(DetailedProfileSetupActivity.this, "Profile created successfully!", Toast.LENGTH_SHORT).show();

                        // Navigate to MainFeedActivity (or another main part of the app)
                        Intent intent = new Intent(DetailedProfileSetupActivity.this, MainFeedActivity.class);
                        // Pass any necessary info, like user's email/name if needed by MainFeedActivity
                        // For now, MainFeedActivity gets user email from Login (old flow) or would need new logic for Firebase users
                        // If MainFeedActivity is expecting an email from the old SharedPreferences login,
                        // we might need to pass something like user.getEmail() if available, or adjust MainFeedActivity.
                        // For simplicity, let's assume MainFeedActivity can handle a user who just completed profile setup.
                        // We might need to pass userUid or fetch display name for MainFeedActivity in a real app.
                        // The old MainFeedActivity expects EXTRA_USER_EMAIL_LOGIN.
                        // Let's pass the phone number for now as a placeholder for user identifier.
                        intent.putExtra(MainFeedActivity.EXTRA_USER_EMAIL_LOGIN, phoneNumber);


                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish(); // Finish DetailedProfileSetupActivity
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBarProfileSetup.setVisibility(View.GONE);
                        buttonConfirmProfile.setVisibility(View.VISIBLE); // Allow retry
                        Log.w(TAG, "Error writing document", e);
                        Toast.makeText(DetailedProfileSetupActivity.this, "Error creating profile: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
