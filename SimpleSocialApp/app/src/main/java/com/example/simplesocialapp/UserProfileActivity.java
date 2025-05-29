package com.example.simplesocialapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserProfileActivity extends AppCompatActivity {

    private TextView textViewUsernameProfile;
    private RecyclerView recyclerViewUserPosts;
    private PostAdapter postAdapter;
    private List<Post> userPostList;
    private SharedPreferences sharedPreferences;
    private Gson gson;
    private String userEmail;

    public static final String EXTRA_USER_EMAIL_PROFILE = "com.example.simplesocialapp.USER_EMAIL_PROFILE";
    private static final String POSTS_KEY = "all_posts";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        textViewUsernameProfile = findViewById(R.id.textViewUsernameProfile);
        recyclerViewUserPosts = findViewById(R.id.recyclerViewUserPosts);

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        gson = new Gson();

        userEmail = getIntent().getStringExtra(EXTRA_USER_EMAIL_PROFILE);

        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(this, "User profile not available.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        textViewUsernameProfile.setText(userEmail); // Display the user's email

        setupRecyclerView();
        loadUserPosts();
    }

    private void setupRecyclerView() {
        userPostList = new ArrayList<>();
        postAdapter = new PostAdapter(userPostList); // Use the same adapter
        recyclerViewUserPosts.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewUserPosts.setAdapter(postAdapter);
    }

    private void loadUserPosts() {
        String jsonPosts = sharedPreferences.getString(POSTS_KEY, null);
        if (jsonPosts != null) {
            Type type = new TypeToken<ArrayList<Post>>() {}.getType();
            List<Post> allPosts = gson.fromJson(jsonPosts, type);

            if (allPosts != null && !allPosts.isEmpty()) {
                // Filter posts for the current user and ensure they are sorted (newest first)
                // The main list is already sorted by CreatePostActivity
                List<Post> filteredPosts = allPosts.stream()
                        .filter(post -> userEmail.equals(post.getUserEmail()))
                        .collect(Collectors.toList());
                
                userPostList.clear();
                userPostList.addAll(filteredPosts);
                postAdapter.notifyDataSetChanged();

                if (userPostList.isEmpty()) {
                    Toast.makeText(this, "No posts yet!", Toast.LENGTH_SHORT).show();
                }
            } else {
                 Toast.makeText(this, "No posts found.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No posts found.", Toast.LENGTH_SHORT).show();
        }
    }
}
