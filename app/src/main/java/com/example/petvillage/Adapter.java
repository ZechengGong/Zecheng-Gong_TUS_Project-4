package com.example.petvillage;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.petvillage.BlogDetail;

import java.util.ArrayList;
import java.util.HashMap;


public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    public ArrayList<Model> list;

    public Adapter(ArrayList<Model> list) {
        this.list = list;
        this.notifyDataSetChanged();
    }
    public void filter_list(ArrayList<Model> filter_list){
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
        Model model = list.get(position);
        holder.title.setText(model.getTitle());
        holder.date.setText(model.getDate());
        holder.share_count.setText(model.getLikes() + " Likes");
        holder.author.setText("By: " + model.getAuthor());

        Glide.with(holder.author.getContext()).load(model.getImg()).into(holder.img);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.author.getContext(), BlogDetail.class);
            intent.putExtra("id", model.getId());
            holder.author.getContext().startActivity(intent);
        });

        holder.itemView.setOnLongClickListener(v -> {
            String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            if (currentUserId.equals(model.getUserId())) {
                showUserOptionsDialog(holder, model);
            } else {
                Toast.makeText(holder.author.getContext(), "You can only modify your own posts.", Toast.LENGTH_SHORT).show();
            }
            return true;
        });
    }

    private void showUserOptionsDialog(ViewHolder holder, Model model) {
        AlertDialog.Builder builder = new AlertDialog.Builder(holder.author.getContext());
        builder.setTitle("Choose an action for the post:");

        builder.setPositiveButton("Update", (dialog, which) -> showUpdateDialog(holder, model));
        builder.setNegativeButton("Delete", (dialog, which) -> showDeleteConfirmation(holder, model));

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showUpdateDialog(ViewHolder holder, Model model) {
        final Dialog dialog = new Dialog(holder.author.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.update_dialog);

        EditText title = dialog.findViewById(R.id.b_title);
        EditText desc = dialog.findViewById(R.id.b_desc);
        EditText author = dialog.findViewById(R.id.b_author);

        title.setText(model.getTitle());
        desc.setText(model.getDesc());
        author.setText(model.getAuthor());

        dialog.findViewById(R.id.btn_publish).setOnClickListener(v -> {
            if (validateFields(title, desc, author)) {
                updatePost(model.getId(), title.getText().toString(), desc.getText().toString(), author.getText().toString(), dialog);
            }
        });

        dialog.show();
    }

    private boolean validateFields(EditText title, EditText desc, EditText author) {
        if (title.getText().toString().isEmpty() || desc.getText().toString().isEmpty() || author.getText().toString().isEmpty()) {
            if (title.getText().toString().isEmpty()) title.setError("Field is Required!");
            if (desc.getText().toString().isEmpty()) desc.setError("Field is Required!");
            if (author.getText().toString().isEmpty()) author.setError("Field is Required!");
            return false;
        }
        return true;
    }

    private void updatePost(String postId, String title, String desc, String author, Dialog dialog) {
        HashMap<String, Object> updateMap = new HashMap<>();
        updateMap.put("title", title);
        updateMap.put("desc", desc);
        updateMap.put("author", author);

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

    private void showDeleteConfirmation(ViewHolder holder, Model model) {
        AlertDialog.Builder builder = new AlertDialog.Builder(holder.author.getContext());
        builder.setTitle("Are you sure you want to delete this post?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            deletePost(model.getId(), holder);
        });
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void deletePost(String postId, ViewHolder holder) {
        FirebaseFirestore.getInstance().collection("POSTs").document(postId).delete()
                .addOnSuccessListener(aVoid -> Toast.makeText(holder.author.getContext(), "Post deleted successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(holder.author.getContext(), "Failed to delete post: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView date, title, share_count, author;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.imageView3);
            date = itemView.findViewById(R.id.t_date);
            title = itemView.findViewById(R.id.textView9);
            share_count = itemView.findViewById(R.id.textView10);
            author = itemView.findViewById(R.id.textView8);
        }
    }
}