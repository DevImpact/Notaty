package com.example.simplesocialapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> postList;

    public PostAdapter(List<Post> postList) {
        this.postList = postList;
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
        holder.textViewPostUserEmail.setText(post.getUserEmail());
        holder.textViewPostContent.setText(post.getContent());

        // Format timestamp
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        String formattedDate = sdf.format(new Date(post.getTimestamp()));
        holder.textViewPostTimestamp.setText(formattedDate);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public void updatePosts(List<Post> newPosts) {
        this.postList.clear();
        this.postList.addAll(newPosts);
        notifyDataSetChanged();
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
