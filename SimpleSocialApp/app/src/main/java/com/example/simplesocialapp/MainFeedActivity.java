package com.example.simplesocialapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainFeedActivity extends AppCompatActivity {

    private static final String TAG = "MainFeedActivity";

    private RecyclerView recyclerViewFeed;
    private FloatingActionButton fabCreatePost;
    private PostAdapter postAdapter;
    private List<Post> postList; // Keep a list to hold posts

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ListenerRegistration postsListener;

    // No longer expecting email via Intent for this activity's primary user identification.
    // Will rely on FirebaseAuth.getCurrentUser()
    // public static final String EXTRA_USER_EMAIL_LOGIN = "com.example.simplesocialapp.USER_EMAIL_LOGIN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_feed);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // Not logged in, redirect to phone number entry
            Toast.makeText(this, "Please log in to continue.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainFeedActivity.this, PhoneNumberEntryActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }
        // Log.d(TAG, "Current user: " + currentUser.getUid() + " Email: " + currentUser.getEmail());


        recyclerViewFeed = findViewById(R.id.recyclerViewFeed);
        fabCreatePost = findViewById(R.id.fabCreatePost);

        setupRecyclerView();
        // loadPosts() will be handled by onResume's listener attachment

        fabCreatePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // No need to pass user email, CreatePostActivity will get it from FirebaseAuth
                Intent intent = new Intent(MainFeedActivity.this, CreatePostActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setupRecyclerView() {
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(postList);
        recyclerViewFeed.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewFeed.setAdapter(postAdapter);
    }

    private void attachPostsListener() {
        if (postsListener == null) { // Attach listener only if not already attached
            postsListener = db.collection("posts")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .limit(50) // Limiting to 50 posts for performance
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value,
                                            @Nullable FirebaseFirestoreException error) {
                            if (error != null) {
                                Log.w(TAG, "Listen failed.", error);
                                Toast.makeText(MainFeedActivity.this, "Error loading posts.", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            List<Post> newPosts = new ArrayList<>();
                            if (value != null) {
                                for (QueryDocumentSnapshot doc : value) {
                                    Post post = doc.toObject(Post.class);
                                    post.setPostId(doc.getId()); // Set the document ID as postId
                                    newPosts.add(post);
                                }
                            }
                            postAdapter.setPosts(newPosts); // Update adapter with new list
                            Log.d(TAG, "Posts loaded/updated: " + newPosts.size());
                        }
                    });
        }
    }

    private void detachPostsListener() {
        if (postsListener != null) {
            postsListener.remove();
            postsListener = null;
            Log.d(TAG, "Posts listener detached.");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check user again in onResume, in case of auth state changes while paused
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Please log in to continue.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainFeedActivity.this, PhoneNumberEntryActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }
        attachPostsListener(); // Load/refresh posts
    }

    @Override
    protected void onPause() {
        super.onPause();
        detachPostsListener(); // Detach listener to prevent memory leaks and unnecessary background work
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_profile) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                Intent intent = new Intent(MainFeedActivity.this, UserProfileActivity.class);
                // UserProfileActivity will get current user's UID from FirebaseAuth
                // No need to pass UID if it's always the current user's profile
                // intent.putExtra(UserProfileActivity.EXTRA_USER_ID_PROFILE, currentUser.getUid());
                startActivity(intent);
            } else {
                 Toast.makeText(this, "Not logged in.", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (itemId == R.id.action_logout) {
            mAuth.signOut(); // Sign out from Firebase
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainFeedActivity.this, PhoneNumberEntryActivity.class); // Or LoginActivity if you have one
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
