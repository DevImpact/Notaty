package com.example.simplesocialapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserProfileActivity extends AppCompatActivity {

    private static final String TAG = "UserProfileActivity";

    private TextView textViewUsernameProfile; // Displays name
    private TextView textViewUserEmailProfile; // Example: Add a field in layout for email
    private TextView textViewUserPhoneNumberProfile; // Example: Add a field for phone
    // Add other TextViews for other profile fields (DOB, address, etc.)

    private RecyclerView recyclerViewUserPosts;
    private PostAdapter postAdapter;
    private List<Post> userPostList;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ListenerRegistration userPostsListener;
    private String userIdToDisplay; // UID of the user whose profile is being displayed

    // public static final String EXTRA_USER_ID_PROFILE = "com.example.simplesocialapp.USER_ID_PROFILE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile); // Ensure this layout has necessary TextViews

        textViewUsernameProfile = findViewById(R.id.textViewUsernameProfile);
        // Initialize other TextViews for email, phone, etc.
        // e.g., textViewUserEmailProfile = findViewById(R.id.textViewUserEmailProfile);
        // e.g., textViewUserPhoneNumberProfile = findViewById(R.id.textViewUserPhoneNumberProfile);

        recyclerViewUserPosts = findViewById(R.id.recyclerViewUserPosts);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Determine whose profile to display.
        // For now, assumes current user's profile.
        // If you were to allow viewing others' profiles, you'd pass userId via Intent.
        // String passedUserId = getIntent().getStringExtra(EXTRA_USER_ID_PROFILE);
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            userIdToDisplay = currentUser.getUid();
        } else {
            // Not logged in, or UID not passed, redirect.
            Toast.makeText(this, "User not found. Please log in.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(UserProfileActivity.this, PhoneNumberEntryActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        setupRecyclerView();
        loadUserProfile();
        // attachUserPostsListener() will be called in onResume
    }

    private void setupRecyclerView() {
        userPostList = new ArrayList<>();
        postAdapter = new PostAdapter(userPostList);
        recyclerViewUserPosts.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewUserPosts.setAdapter(postAdapter);
    }

    private void loadUserProfile() {
        if (userIdToDisplay == null) return;

        db.collection("users").document(userIdToDisplay).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String firstName = documentSnapshot.getString("firstName");
                            String lastName = documentSnapshot.getString("lastName");
                            String phoneNumber = documentSnapshot.getString("phoneNumber");
                            // String email = documentSnapshot.getString("email"); // If you store email

                            String displayName = (firstName != null && lastName != null) ? firstName + " " + lastName : "N/A";
                            if (firstName == null && lastName == null && mAuth.getCurrentUser() != null) {
                                displayName = mAuth.getCurrentUser().getEmail(); // Fallback to email if name not set
                                 if (displayName == null || displayName.isEmpty()) displayName = "User Profile";
                            }

                            textViewUsernameProfile.setText(displayName);
                            // if (textViewUserEmailProfile != null && email != null) textViewUserEmailProfile.setText(email);
                            // if (textViewUserPhoneNumberProfile != null && phoneNumber != null) textViewUserPhoneNumberProfile.setText(phoneNumber);
                            // Load other fields into their respective TextViews
                        } else {
                            Toast.makeText(UserProfileActivity.this, "User profile not found.", Toast.LENGTH_SHORT).show();
                            textViewUsernameProfile.setText("Profile Not Found");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error loading user profile", e);
                        Toast.makeText(UserProfileActivity.this, "Error loading profile.", Toast.LENGTH_SHORT).show();
                        textViewUsernameProfile.setText("Error Loading Profile");
                    }
                });
    }

    private void attachUserPostsListener() {
        if (userIdToDisplay == null) return;
        if (userPostsListener == null) { // Attach listener only if not already attached
            userPostsListener = db.collection("posts")
                    .whereEqualTo("userId", userIdToDisplay)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value,
                                            @Nullable FirebaseFirestoreException error) {
                            if (error != null) {
                                Log.w(TAG, "Listen failed for user posts.", error);
                                Toast.makeText(UserProfileActivity.this, "Error loading user posts.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            List<Post> posts = new ArrayList<>();
                            if (value != null) {
                                for (QueryDocumentSnapshot doc : value) {
                                    Post post = doc.toObject(Post.class);
                                    post.setPostId(doc.getId());
                                    posts.add(post);
                                }
                            }
                            postAdapter.setPosts(posts);
                            Log.d(TAG, "User posts loaded/updated: " + posts.size());
                            if (posts.isEmpty()) {
                                Toast.makeText(UserProfileActivity.this, "No posts by this user yet.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void detachUserPostsListener() {
        if (userPostsListener != null) {
            userPostsListener.remove();
            userPostsListener = null;
            Log.d(TAG, "User posts listener detached.");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check auth state again
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Please log in to continue.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UserProfileActivity.this, PhoneNumberEntryActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }
        // If userIdToDisplay was not set (e.g. due to error in onCreate), try to set it again.
        if (userIdToDisplay == null) userIdToDisplay = currentUser.getUid();
        
        loadUserProfile(); // Reload profile info
        attachUserPostsListener(); // Reload/refresh user's posts
    }

    @Override
    protected void onPause() {
        super.onPause();
        detachUserPostsListener();
    }
}
