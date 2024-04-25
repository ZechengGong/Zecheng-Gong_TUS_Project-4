package com.example.petvillage.Adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.petvillage.Models.Model_Post;


public class Adapter_Post extends RecyclerView.Adapter<Adapter_Post.ViewHolder> {

    public ArrayList<Model_Post> list;

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Model_Post modelPost = list.get(position);
        holder.title.setText(modelPost.getTitle());
        holder.date.setText(modelPost.getDate());
        holder.share_count.setText(modelPost.getLikes() + " Likes");
        holder.nickname.setText("By: " + modelPost.getNickname());

        Glide.with(holder.nickname.getContext()).load(modelPost.getImg()).into(holder.img);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.nickname.getContext(), PostDetail.class);
            intent.putExtra("id", modelPost.getId());
            holder.nickname.getContext().startActivity(intent);
        });

        holder.itemView.setOnLongClickListener(v -> {
            String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            if (currentUserId.equals(modelPost.getUserId())) {
                showUserOptionsDialog(holder, modelPost);
            } else {
                Toast.makeText(holder.nickname.getContext(), "You can only modify your own posts.", Toast.LENGTH_SHORT).show();
            }
            return true;
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
        dialog.setContentView(R.layout.update_dialog);

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
        FirebaseFirestore.getInstance().collection("POSTs").document(postId).delete()
                .addOnSuccessListener(aVoid -> Toast.makeText(holder.nickname.getContext(), "Post deleted successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(holder.nickname.getContext(), "Failed to delete post: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView date, title, share_count, nickname;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.imageView3);
            date = itemView.findViewById(R.id.t_date);
            title = itemView.findViewById(R.id.textView9);
            share_count = itemView.findViewById(R.id.textView10);
            nickname = itemView.findViewById(R.id.textView8);
        }
    }
}