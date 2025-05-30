package com.example.simplesocialapp;

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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue; // Required for serverTimestamp if not using @ServerTimestamp annotation in model

public class CreatePostActivity extends AppCompatActivity {

    private static final String TAG = "CreatePostActivity";

    private EditText editTextPostContent;
    private Button buttonSubmitPost;
    private ProgressBar progressBarCreatePost; // Assuming you have a ProgressBar in the layout

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post); // Ensure this layout has a ProgressBar

        editTextPostContent = findViewById(R.id.editTextPostContent);
        buttonSubmitPost = findViewById(R.id.buttonSubmitPost);
        // Initialize progressBarCreatePost - Let's assume its ID is progressBarCreatePost
        // If it's not in the XML, this line will cause an error.
        // For now, I'll comment it out and assume it will be added or is already there.
        // progressBarCreatePost = findViewById(R.id.progressBarCreatePost);


        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        buttonSubmitPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String postContent = editTextPostContent.getText().toString().trim();

                if (TextUtils.isEmpty(postContent)) {
                    Toast.makeText(CreatePostActivity.this, "Post content cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser == null) {
                    Toast.makeText(CreatePostActivity.this, "You must be logged in to post.", Toast.LENGTH_SHORT).show();
                    // Optionally, redirect to login/phone entry
                    // Intent intent = new Intent(CreatePostActivity.this, PhoneNumberEntryActivity.class);
                    // startActivity(intent);
                    // finish();
                    return;
                }

                String userId = currentUser.getUid();
                // Attempt to get email as username. Fallback if not available.
                String username = (currentUser.getEmail() != null && !currentUser.getEmail().isEmpty()) ? currentUser.getEmail() : "Anonymous";
                // A more robust way would be to fetch from the user's profile in Firestore
                // For example: db.collection("users").document(userId).get()... then get "firstName" + "lastName"

                if (progressBarCreatePost != null) progressBarCreatePost.setVisibility(View.VISIBLE);
                buttonSubmitPost.setEnabled(false);

                Post newPost = new Post(userId, username, postContent);
                // The timestamp will be set by @ServerTimestamp in the Post model
                // Or, if not using annotation, you'd do: newPost.setTimestamp(null); and use FieldValue for a map.
                // When adding a POJO, @ServerTimestamp is preferred.

                db.collection("posts").add(newPost)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                                Toast.makeText(CreatePostActivity.this, "Post created successfully", Toast.LENGTH_SHORT).show();
                                if (progressBarCreatePost != null) progressBarCreatePost.setVisibility(View.GONE);
                                buttonSubmitPost.setEnabled(true);
                                finish(); // Go back to MainFeedActivity
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                                Toast.makeText(CreatePostActivity.this, "Error creating post: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                if (progressBarCreatePost != null) progressBarCreatePost.setVisibility(View.GONE);
                                buttonSubmitPost.setEnabled(true);
                            }
                        });
            }
        });
    }
}
