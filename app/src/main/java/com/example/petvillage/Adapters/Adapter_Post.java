package com.example.petvillage.Adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.petvillage.Main.PostDetail;
import com.example.petvillage.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import com.example.petvillage.Models.Model_Post;
import com.example.petvillage.Models.Model_Comment;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

// Post (Details) Adapter
public class Adapter_Post extends RecyclerView.Adapter<Adapter_Post.ViewHolder> {

    public ArrayList<Model_Post> list; // List to save post data
    private static String COMMENT_KEY = "Comment"; // Key to use for review data in Firebase

    // Constructor, initialize the post list and notify data changes
    public Adapter_Post(ArrayList<Model_Post> list) {
        this.list = list;
        this.notifyDataSetChanged();
    }

    // Update the list and notify of changes
    public void filter_list(ArrayList<Model_Post> filter_list){
        list = filter_list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Load the view from the layout file
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Model_Post modelPost = list.get(position); // Get the post data of the current location
        holder.title.setText(modelPost.getTitle()); //Set title
        holder.date.setText(holder.formatDate(modelPost.getTimestamp())); // Set date
        holder.liked_count.setText(modelPost.getLikes() + " Likes"); // Set the number of likes
        holder.nickname.setText("By: " + modelPost.getNickname()); // Set nickname

        Glide.with(holder.nickname.getContext()).load(modelPost.getImg()).into(holder.img); // Load images

        // Listen and count the number of comments
        DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference(COMMENT_KEY).child(modelPost.getId());
        commentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long count = dataSnapshot.getChildrenCount(); // Get the number of child nodes as the number of comments
                holder.comment_count.setText(count + " Comments");
                Log.d("Adapter_Post", "Comments loaded for post ID " + modelPost.getId() + ": " + count);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Adapter_Post", "Failed to read comment count: " + databaseError.getMessage());
            }
        });

        // Set the post click event and enter the post details
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.nickname.getContext(), PostDetail.class);
            intent.putExtra("id", modelPost.getId());
            holder.nickname.getContext().startActivity(intent);
        });

        // Set a long press listener and provide modification and deletion options
        holder.itemView.setOnLongClickListener(v -> {
            String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            if (currentUserId != null && currentUserId.equals(modelPost.getUserId())) {
                showUserOptionsDialog(holder, modelPost);
                return true;  // Indicates that the event has been processed
            } else {
                Toast.makeText(holder.nickname.getContext(), "You can only modify your OWN posts.", Toast.LENGTH_SHORT).show();
                return false;  //Indicates that the event has not been handled
            }
        });
    }

    // Pop-up dialog box provides modification and deletion options
    private void showUserOptionsDialog(ViewHolder holder, Model_Post modelPost) {
        AlertDialog.Builder builder = new AlertDialog.Builder(holder.nickname.getContext());
        builder.setTitle("Choose an action for the post:");

        builder.setPositiveButton("Update", (dialog, which) -> showUpdateDialog(holder, modelPost));
        builder.setNegativeButton("Delete", (dialog, which) -> showDeleteConfirmation(holder, modelPost));

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Show Update dialog
    private void showUpdateDialog(ViewHolder holder, Model_Post modelPost) {
        final Dialog dialog = new Dialog(holder.nickname.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.update_post_dialog);

        EditText title = dialog.findViewById(R.id.et_title);
        EditText content = dialog.findViewById(R.id.et_content);
        EditText nickname = dialog.findViewById(R.id.et_nickname);

        // Make sure all elements are not empty
        if (title != null && content != null && nickname != null) {
            title.setText(modelPost.getTitle());
            content.setText(modelPost.getContent());
            nickname.setText(modelPost.getNickname());

            dialog.findViewById(R.id.btn_update).setOnClickListener(v -> {
                if (validateFields(title, content, nickname)) {
                    updatePost(modelPost.getId(), title.getText().toString(), content.getText().toString(), nickname.getText().toString(), dialog);
                }
            });

            dialog.show();
        } else {
            Toast.makeText(holder.nickname.getContext(), "Error: Dialog layout is not correctly loaded.", Toast.LENGTH_SHORT).show();
        }
    }

    // Check whether the input field is empty
    private boolean validateFields(EditText title, EditText desc, EditText nickname) {
        if (title.getText().toString().isEmpty() || desc.getText().toString().isEmpty() || nickname.getText().toString().isEmpty()) {
            if (title.getText().toString().isEmpty()) title.setError("Field is Required!");
            if (desc.getText().toString().isEmpty()) desc.setError("Field is Required!");
            if (nickname.getText().toString().isEmpty()) nickname.setError("Field is Required!");
            return false;
        }
        return true;
    }

    // Update post information
    private void updatePost(String postId, String title, String content, String nickname, Dialog dialog) {
        HashMap<String, Object> updateMap = new HashMap<>();
        updateMap.put("title", title);
        updateMap.put("content", content);
        updateMap.put("nickname", nickname);

        FirebaseFirestore.getInstance().collection("POSTs").document(postId).update(updateMap)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(dialog.getContext(), "Post updated successfully", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(dialog.getContext(), "Update failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showDeleteConfirmation(ViewHolder holder, Model_Post modelPost) {
        AlertDialog.Builder builder = new AlertDialog.Builder(holder.nickname.getContext());
        builder.setTitle("Are you sure you want to delete this post?");

        // Swap the button's text and response functions
        builder.setPositiveButton("No", (dialog, which) -> {
            dialog.dismiss(); // this is "No", just close the dialog
        });

        builder.setNegativeButton("Yes", (dialog, which) -> {
            deletePost(modelPost.getId(), holder); // this is "yes", perform the delete operation
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Perform the operation of deleting posts and related comments
    private void deletePost(String postId, ViewHolder holder) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseDatabase realtimeDatabase = FirebaseDatabase.getInstance();
        DatabaseReference commentRef = realtimeDatabase.getReference(COMMENT_KEY).child(postId);

        // Delete comment
        commentRef.removeValue().addOnSuccessListener(aVoid -> {
            Log.d("deletePost", "Comments deleted successfully for post ID: " + postId);

            // Get the image URL of the post
            db.collection("POSTs").document(postId).get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    // Get the URL of the image
                    String imgUrl = documentSnapshot.getString("img");

                    // Delete image files
                    if (imgUrl != null && !imgUrl.isEmpty()) {
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference imgRef = storage.getReferenceFromUrl(imgUrl);
                        imgRef.delete().addOnSuccessListener(aVoid1 -> {
                            // Delete post data
                            db.collection("POSTs").document(postId).delete()
                                    .addOnSuccessListener(aVoid2 -> Toast.makeText(holder.nickname.getContext(), "Post, image and comments deleted", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e -> Toast.makeText(holder.nickname.getContext(), "Failed to delete post: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        }).addOnFailureListener(e -> Toast.makeText(holder.nickname.getContext(), "Failed to delete image: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    } else {
                        // If there is no associated picture, delete the post data directly
                        db.collection("POSTs").document(postId).delete()
                                .addOnSuccessListener(aVoid2 -> Toast.makeText(holder.nickname.getContext(), "Post and comments deleted", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(holder.nickname.getContext(), "Failed to delete post: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                } else {
                    Toast.makeText(holder.nickname.getContext(), "Post not found", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> Toast.makeText(holder.nickname.getContext(), "Failed to fetch post: " + e.getMessage(), Toast.LENGTH_SHORT).show());

        }).addOnFailureListener(e -> {
            Log.e("deletePost", "Failed to delete comments for post ID: " + postId, e);
            Toast.makeText(holder.nickname.getContext(), "Failed to delete comments: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return list.size(); // Return the total number of posts
    }

    // ViewHolder used to cache view components
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView date, title, liked_count, nickname, comment_count;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.imageViewPost);
            date = itemView.findViewById(R.id.t_date);
            title = itemView.findViewById(R.id.textViewTitle);
            liked_count = itemView.findViewById(R.id.textViewLikes);
            nickname = itemView.findViewById(R.id.textViewName);
            comment_count = itemView.findViewById(R.id.textViewCommentCount);  // Initialize comment_count
        }

        // Format the timestamp into a more readable date format
        private String formatDate(long time) {
            Calendar now = Calendar.getInstance();
            Calendar timeToCheck = Calendar.getInstance();
            timeToCheck.setTimeInMillis(time);

            if (now.get(Calendar.YEAR) == timeToCheck.get(Calendar.YEAR)) {
                if (now.get(Calendar.DAY_OF_YEAR) == timeToCheck.get(Calendar.DAY_OF_YEAR)) {
                    return "Today " + DateFormat.format("HH:mm", timeToCheck);
                } else if (now.get(Calendar.DAY_OF_YEAR) - 1 == timeToCheck.get(Calendar.DAY_OF_YEAR)) {
                    return "Yesterday " + DateFormat.format("HH:mm", timeToCheck);
                }
            }
            return DateFormat.format("dd-MM-yyyy  HH:mm", timeToCheck).toString();
        }
    }
}
