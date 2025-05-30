package com.example.simplesocialapp;

import com.google.firebase.Timestamp; // Firestore Timestamp
import com.google.firebase.firestore.FieldValue; // For serverTimestamp during creation
import com.google.firebase.firestore.ServerTimestamp; // Annotation for automatic timestamp

public class Post {
    private String postId; // Document ID from Firestore
    private String userId;
    private String username; // Or userEmail, display name
    private String content;
    private @ServerTimestamp Timestamp timestamp; // Automatically set by Firestore on server

    // Empty constructor required for Firestore deserialization
    public Post() {}

    public Post(String userId, String username, String content) {
        this.userId = userId;
        this.username = username;
        this.content = content;
        // Timestamp is set by @ServerTimestamp or FieldValue.serverTimestamp() during write
    }

    // Getters
    public String getPostId() {
        return postId;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getContent() {
        return content;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    // Setters
    public void setPostId(String postId) {
        this.postId = postId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
