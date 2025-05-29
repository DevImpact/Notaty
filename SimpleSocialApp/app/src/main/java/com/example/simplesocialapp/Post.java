package com.example.simplesocialapp;

import java.util.UUID;

public class Post {
    private String postId;
    private String userEmail;
    private String content;
    private long timestamp;

    public Post(String userEmail, String content) {
        this.postId = UUID.randomUUID().toString();
        this.userEmail = userEmail;
        this.content = content;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters
    public String getPostId() {
        return postId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getContent() {
        return content;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
