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


public class Adapter_Post extends RecyclerView.Adapter<Adapter_Post.ViewHolder> {

    public ArrayList<Model_Post> list;
    private static String COMMENT_KEY = "Comment";

    public Adapter_Post(ArrayList<Model_Post> list) {
        this.list = list;
        this.notifyDataSetChanged();
    }
    public void filter_list(ArrayList<Model_Post> filter_list){
        list = filter_list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Model_Post modelPost = list.get(position);
        holder.title.setText(modelPost.getTitle());
        holder.date.setText(holder.formatDate(modelPost.getTimestamp()));
        holder.liked_count.setText(modelPost.getLikes() + " Likes");
        holder.nickname.setText("By: " + modelPost.getNickname());

        Glide.with(holder.nickname.getContext()).load(modelPost.getImg()).into(holder.img);

        DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference(COMMENT_KEY).child(modelPost.getId());
        commentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long count = dataSnapshot.getChildrenCount(); // 获取子节点数作为评论数量
                holder.comment_count.setText(count + " Comments");
                Log.d("Adapter_Post", "Comments loaded for post ID " + modelPost.getId() + ": " + count);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Adapter_Post", "Failed to read comment count: " + databaseError.getMessage());
            }
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.nickname.getContext(), PostDetail.class);
            intent.putExtra("id", modelPost.getId());
            holder.nickname.getContext().startActivity(intent);
        });

        // 设置长按监听器
        holder.itemView.setOnLongClickListener(v -> {
            String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            if (currentUserId != null && currentUserId.equals(modelPost.getUserId())) {
                showUserOptionsDialog(holder, modelPost);
                return true;  // 表明事件已被处理
            } else {
                Toast.makeText(holder.nickname.getContext(), "You can only modify your OWN posts.", Toast.LENGTH_SHORT).show();
                return false;  // 表明事件未被处理
            }
        });
    }

    private void showUserOptionsDialog(ViewHolder holder, Model_Post modelPost) {
        AlertDialog.Builder builder = new AlertDialog.Builder(holder.nickname.getContext());
        builder.setTitle("Choose an action for the post:");

        builder.setPositiveButton("Update", (dialog, which) -> showUpdateDialog(holder, modelPost));
        builder.setNegativeButton("Delete", (dialog, which) -> showDeleteConfirmation(holder, modelPost));

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showUpdateDialog(ViewHolder holder, Model_Post modelPost) {
        final Dialog dialog = new Dialog(holder.nickname.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.update_post_dialog);

        EditText title = dialog.findViewById(R.id.et_title);
        EditText content = dialog.findViewById(R.id.et_content);
        EditText nickname = dialog.findViewById(R.id.et_nickname);

        // 确保所有元素都不为空
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

    private boolean validateFields(EditText title, EditText desc, EditText nickname) {
        if (title.getText().toString().isEmpty() || desc.getText().toString().isEmpty() || nickname.getText().toString().isEmpty()) {
            if (title.getText().toString().isEmpty()) title.setError("Field is Required!");
            if (desc.getText().toString().isEmpty()) desc.setError("Field is Required!");
            if (nickname.getText().toString().isEmpty()) nickname.setError("Field is Required!");
            return false;
        }
        return true;
    }

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

        // 交换按钮的文本和响应功能
        builder.setPositiveButton("No", (dialog, which) -> {
            dialog.dismiss(); // 实际上，这里现在是“否”，所以只需关闭对话框
        });

        builder.setNegativeButton("Yes", (dialog, which) -> {
            deletePost(modelPost.getId(), holder); // 实际上，这里现在是“是”，执行删除操作
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deletePost(String postId, ViewHolder holder) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseDatabase realtimeDatabase = FirebaseDatabase.getInstance();
        DatabaseReference commentRef = realtimeDatabase.getReference(COMMENT_KEY).child(postId);

        // 删除评论
        commentRef.removeValue().addOnSuccessListener(aVoid -> {
            Log.d("deletePost", "Comments deleted successfully for post ID: " + postId);

            // 获取帖子的图片URL
            db.collection("POSTs").document(postId).get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    // 获取图片的 URL
                    String imgUrl = documentSnapshot.getString("img");

                    // 删除图片文件
                    if (imgUrl != null && !imgUrl.isEmpty()) {
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference imgRef = storage.getReferenceFromUrl(imgUrl);
                        imgRef.delete().addOnSuccessListener(aVoid1 -> {
                            // 删除帖子数据
                            db.collection("POSTs").document(postId).delete()
                                    .addOnSuccessListener(aVoid2 -> Toast.makeText(holder.nickname.getContext(), "Post, image and comments deleted successfully", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e -> Toast.makeText(holder.nickname.getContext(), "Failed to delete post: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        }).addOnFailureListener(e -> Toast.makeText(holder.nickname.getContext(), "Failed to delete image: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    } else {
                        // 没有关联的图片，直接删除帖子数据
                        db.collection("POSTs").document(postId).delete()
                                .addOnSuccessListener(aVoid2 -> Toast.makeText(holder.nickname.getContext(), "Post and comments deleted successfully", Toast.LENGTH_SHORT).show())
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
        return list.size();
    }

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
            comment_count = itemView.findViewById(R.id.textViewCommentCount);  // 初始化 comment_count
        }
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
