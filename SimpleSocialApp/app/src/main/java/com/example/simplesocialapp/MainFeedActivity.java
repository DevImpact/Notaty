package com.example.simplesocialapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainFeedActivity extends AppCompatActivity {

    private RecyclerView recyclerViewFeed;
    private FloatingActionButton fabCreatePost;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private SharedPreferences sharedPreferences;
    private Gson gson;
    private String currentUserEmail;

    public static final String EXTRA_USER_EMAIL_LOGIN = "com.example.simplesocialapp.USER_EMAIL_LOGIN";
    private static final String POSTS_KEY = "all_posts";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_feed);

        currentUserEmail = getIntent().getStringExtra(EXTRA_USER_EMAIL_LOGIN);
        if (currentUserEmail == null || currentUserEmail.isEmpty()) {
            Toast.makeText(this, "User email not available. Please re-login.", Toast.LENGTH_LONG).show();
            // Navigate back to LoginActivity if email is missing
            Intent intent = new Intent(MainFeedActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        recyclerViewFeed = findViewById(R.id.recyclerViewFeed);
        fabCreatePost = findViewById(R.id.fabCreatePost);
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        gson = new Gson();

        setupRecyclerView();
        loadPosts();

        fabCreatePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainFeedActivity.this, CreatePostActivity.class);
                intent.putExtra(CreatePostActivity.EXTRA_USER_EMAIL, currentUserEmail);
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

    private void loadPosts() {
        String jsonPosts = sharedPreferences.getString(POSTS_KEY, null);
        if (jsonPosts != null) {
            Type type = new TypeToken<ArrayList<Post>>() {}.getType();
            List<Post> loadedPosts = gson.fromJson(jsonPosts, type);
            if (loadedPosts != null) {
                postList.clear();
                postList.addAll(loadedPosts); // Posts are already sorted newest first
                postAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPosts();
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
            Intent intent = new Intent(MainFeedActivity.this, UserProfileActivity.class);
            intent.putExtra(UserProfileActivity.EXTRA_USER_EMAIL_PROFILE, currentUserEmail);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_logout) {
            // Clear SharedPreferences or specific keys
            // For this example, clearing all UserPrefs. Be cautious with this in a real app.
            SharedPreferences.Editor editor = sharedPreferences.edit();
            // editor.remove("currentUserEmail"); // Example of removing specific key
            // editor.remove(currentUserEmail + "_password"); // Example
            // editor.remove(currentUserEmail + "_username"); // Example
            editor.clear(); // Clears all data in "UserPrefs"
            editor.apply();

            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(MainFeedActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
