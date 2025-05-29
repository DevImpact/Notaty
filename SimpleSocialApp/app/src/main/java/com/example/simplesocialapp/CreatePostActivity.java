package com.example.simplesocialapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CreatePostActivity extends AppCompatActivity {

    private EditText editTextPostContent;
    private Button buttonSubmitPost;
    private SharedPreferences sharedPreferences;
    private Gson gson;
    private String currentUserEmail;

    public static final String EXTRA_USER_EMAIL = "com.example.simplesocialapp.USER_EMAIL";
    private static final String POSTS_KEY = "all_posts";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        editTextPostContent = findViewById(R.id.editTextPostContent);
        buttonSubmitPost = findViewById(R.id.buttonSubmitPost);
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        gson = new Gson();

        currentUserEmail = getIntent().getStringExtra(EXTRA_USER_EMAIL);
        if (currentUserEmail == null || currentUserEmail.isEmpty()) {
            Toast.makeText(this, "Error: User email not provided.", Toast.LENGTH_LONG).show();
            finish(); // Can't create a post without user info
            return;
        }

        buttonSubmitPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String postContent = editTextPostContent.getText().toString().trim();

                if (TextUtils.isEmpty(postContent)) {
                    Toast.makeText(CreatePostActivity.this, "Post content cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                Post newPost = new Post(currentUserEmail, postContent);
                savePost(newPost);

                Toast.makeText(CreatePostActivity.this, "Post created successfully", Toast.LENGTH_SHORT).show();
                finish(); // Go back to MainFeedActivity
            }
        });
    }

    private void savePost(Post post) {
        String jsonPosts = sharedPreferences.getString(POSTS_KEY, null);
        List<Post> postsList;

        if (jsonPosts == null) {
            postsList = new ArrayList<>();
        } else {
            Type type = new TypeToken<ArrayList<Post>>() {}.getType();
            postsList = gson.fromJson(jsonPosts, type);
        }

        postsList.add(post);
        // Sort by timestamp descending to have newest first, makes retrieval easier for feed
        Collections.sort(postsList, (p1, p2) -> Long.compare(p2.getTimestamp(), p1.getTimestamp()));

        String updatedJsonPosts = gson.toJson(postsList);
        sharedPreferences.edit().putString(POSTS_KEY, updatedJsonPosts).apply();
    }
}
