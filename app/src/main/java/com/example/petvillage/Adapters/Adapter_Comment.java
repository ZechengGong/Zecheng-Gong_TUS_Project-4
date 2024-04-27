package com.example.petvillage.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import com.example.petvillage.Models.Model_Comment;
import com.example.petvillage.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Adapter_Comment extends RecyclerView.Adapter<Adapter_Comment.CommentViewHolder> {

    private Activity mActivity;
    private Context mContext;
    private List<Model_Comment> mData;

    private static String COMMENT_KEY = "Comment";


    public Adapter_Comment(Activity  activity, List<Model_Comment> mData) {
        this.mActivity  = activity;
        this.mData = mData;
        this.mContext = activity;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.comment_list,parent,false);
        return new CommentViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Model_Comment comment = mData.get(position);
        Glide.with(mContext).load(mData.get(position).getuImg()).into(holder.img_user);
        holder.tv_name.setText(mData.get(position).getUname());
        holder.tv_content.setText(mData.get(position).getContent());
        holder.tv_date.setText(timestampToString((Long)mData.get(position).getTimestamp()));

        holder.itemView.setOnLongClickListener(v -> {
            Log.d("AdapterComment", "Long click detected");

            if (FirebaseAuth.getInstance().getCurrentUser() != null &&
                    FirebaseAuth.getInstance().getCurrentUser().getUid().equals(comment.getUid())) {
                Log.d("AdapterComment", "Showing delete dialog for comment");
                showDeleteDialog(comment);
            } else {
                Toast.makeText(mContext, "You can only delete your OWN comments.", Toast.LENGTH_SHORT).show();
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder{

        ImageView img_user;
        TextView tv_name,tv_content,tv_date;

        public CommentViewHolder(View itemView) {
            super(itemView);
            img_user = itemView.findViewById(R.id.comment_user_img);
            tv_name = itemView.findViewById(R.id.comment_username);
            tv_content = itemView.findViewById(R.id.comment_content);
            tv_date = itemView.findViewById(R.id.comment_date);
        }
    }

    private void showDeleteDialog(Model_Comment comment) {
        new AlertDialog.Builder(mActivity) // 使用 Activity 的 Context
                .setTitle("Delete Comment")
                .setMessage("Are you sure you want to delete this comment?")
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Yes", (dialog, which) -> deleteComment(comment))
                .show();
    }

    // 删除评论的方法
    private void deleteComment(Model_Comment comment) {
        Log.d("AdapterComment", "Trying to delete comment with ID: " + comment.getCommentId() + " under post ID: " + comment.getPostId());

        if (comment.getCommentId() == null || comment.getCommentId().isEmpty() || comment.getPostId() == null || comment.getPostId().isEmpty()) {
            Toast.makeText(mActivity, "Invalid comment or post ID", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference()
                .child(COMMENT_KEY)
                .child(comment.getPostId())
                .child(comment.getCommentId());

        commentRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    int index = mData.indexOf(comment);
                    if (index != -1) {
                        mData.remove(index);
                        notifyItemRemoved(index);
                        Log.d("AdapterComment", "Comment deleted successfully from Firebase and local list.");
                    } else {
                        Log.d("AdapterComment", "Comment was not found in the local list.");
                    }
                    Toast.makeText(mActivity, "Comment deleted successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(mActivity, "Failed to delete comment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("AdapterComment", "Failed to delete comment: " + e.getMessage(), e);
                });
    }

    private String timestampToString(long time) {
        Calendar now = Calendar.getInstance();
        Calendar timeToCheck = Calendar.getInstance();
        timeToCheck.setTimeInMillis(time);

        DateFormat dateFormat = new DateFormat();

        // Check if the comment date is today
        if (now.get(Calendar.YEAR) == timeToCheck.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) == timeToCheck.get(Calendar.DAY_OF_YEAR)) {
            return "Today " + DateFormat.format("HH:mm", timeToCheck);
        }
        // Check if the comment date was yesterday
        else if (now.get(Calendar.YEAR) == timeToCheck.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) - 1 == timeToCheck.get(Calendar.DAY_OF_YEAR)) {
            return "Yesterday " + DateFormat.format("HH:mm", timeToCheck);
        }
        // For other days
        else {
            return DateFormat.format("dd-MM-yyyy HH:mm", timeToCheck).toString();
        }
    }


}
