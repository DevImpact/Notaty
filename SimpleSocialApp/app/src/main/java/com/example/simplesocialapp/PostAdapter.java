package com.example.simplesocialapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.Timestamp; // Import Firestore Timestamp
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> postList;

    public PostAdapter(List<Post> postList) {
        this.postList = postList != null ? postList : new ArrayList<>();
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.textViewPostUserEmail.setText(post.getUsername()); // Changed from getEmail to getUsername
        holder.textViewPostContent.setText(post.getContent());

        // Format Firestore Timestamp
        Timestamp timestamp = post.getTimestamp();
        if (timestamp != null) {
            Date date = timestamp.toDate(); // Convert Firebase Timestamp to java.util.Date
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
            holder.textViewPostTimestamp.setText(sdf.format(date));
        } else {
            holder.textViewPostTimestamp.setText("No date"); // Or some placeholder
        }
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    // Method to update the list of posts in the adapter
    public void setPosts(List<Post> newPosts) {
        this.postList.clear();
        if (newPosts != null) {
            this.postList.addAll(newPosts);
        }
        notifyDataSetChanged(); // Notify adapter that data has changed
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView textViewPostUserEmail;
        TextView textViewPostContent;
        TextView textViewPostTimestamp;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewPostUserEmail = itemView.findViewById(R.id.textViewPostUserEmail);
            textViewPostContent = itemView.findViewById(R.id.textViewPostContent);
            textViewPostTimestamp = itemView.findViewById(R.id.textViewPostTimestamp);
        }
    }
}
