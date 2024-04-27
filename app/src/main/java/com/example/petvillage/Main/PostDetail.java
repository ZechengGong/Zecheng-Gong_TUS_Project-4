package com.example.petvillage.Main;

import android.content.Context;
import android.graphics.Matrix;
import android.os.Bundle;
import android.text.Html;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.petvillage.Adapters.Adapter_Comment;
import com.example.petvillage.Models.Model_Post;
import com.example.petvillage.R;
import com.example.petvillage.databinding.ActivityPostDetailsBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import com.example.petvillage.Models.Model_Comment;


public class PostDetail extends AppCompatActivity {
    private ActivityPostDetailsBinding binding;
    private String id;
    private String title, content;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private RecyclerView RvComment;
    private Adapter_Comment adapterComment;
    private List<Model_Comment> listModelComment;
    private EditText editTextComment;
    private ImageButton btnAddComment;

    private static String COMMENT_KEY = "Comment";

    private Matrix matrix = new Matrix();
    private float scale = 1f;
    private ScaleGestureDetector scaleGestureDetector;
    private boolean isImageZoomed = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        showData();

        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        // 设置 ImageView 的 onTouchListener 来处理缩放手势
        binding.imageViewPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("PostDetail", "ImageView clicked"); // 添加日志确认点击事件
                if (!isImageZoomed) {
                    binding.imageViewPostImage.animate().scaleX(2f).scaleY(2f).setDuration(300).start();
                    isImageZoomed = true;
                } else {
                    binding.imageViewPostImage.animate().scaleX(1f).scaleY(1f).setDuration(300).start();
                    isImageZoomed = false;
                }
            }
        });

        RvComment = findViewById(R.id.commentsRecyclerView);
        editTextComment = findViewById(R.id.post_detail_comment);
        btnAddComment = findViewById(R.id.post_detail_add_comment_btn);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        // add Comment button click listener
        btnAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String commentContent = editTextComment.getText().toString().trim();
                if (commentContent.isEmpty()) {
                    Toast.makeText(PostDetail.this, "Comment cannot be empty!", Toast.LENGTH_SHORT).show();
                    btnAddComment.setVisibility(View.VISIBLE);
                    return;
                }

                btnAddComment.setVisibility(View.INVISIBLE);
                DatabaseReference commentReference = firebaseDatabase.getReference(COMMENT_KEY).child(id).push();
                String commentId = commentReference.getKey();  // 获取生成的评论ID
                String uid = firebaseUser.getUid();
                String uname = firebaseUser.getDisplayName();
                String uImg = (firebaseUser.getPhotoUrl() != null) ? firebaseUser.getPhotoUrl().toString() : "default_user_image_url";

                // 传入 postId
                Model_Comment modelComment = new Model_Comment(commentContent, uid, uImg, uname, commentId, id);

                if (firebaseUser != null) {
                    commentReference.setValue(modelComment)
                            .addOnSuccessListener(aVoid -> {
                                Log.d("CommentDebug", "Comment added successfully");
                                showMessage("Comment added");
                                editTextComment.setText("");
                                hideKeyboard();
                                btnAddComment.setVisibility(View.VISIBLE);
                            })
                            .addOnFailureListener(e -> {
                                Log.d("CommentDebug", "Failed to add comment: " + e.getMessage());
                                showMessage("Fail to add comment: " + e.getMessage());
                                btnAddComment.setVisibility(View.VISIBLE);
                            });
                } else {
                    Toast.makeText(PostDetail.this, "Please login to add comments.", Toast.LENGTH_SHORT).show();
                    btnAddComment.setVisibility(View.VISIBLE);
                }
            }
        });

        iniRvComment();
    }

//                DatabaseReference testRef = firebaseDatabase.getReference("Test").child("TestChild");

//                testRef.setValue("TestValue").addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Log.d("TestDebug", "Test write successful");
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.d("TestDebug", "Test write failed: " + e.getMessage());
//                    }
//                });

    private void iniRvComment() {
        RvComment.setLayoutManager(new LinearLayoutManager(this));
        if (listModelComment == null) {
            listModelComment = new ArrayList<>();
        }
        adapterComment = new Adapter_Comment(PostDetail.this, listModelComment);
        RvComment.setAdapter(adapterComment);

        DatabaseReference commentRef = firebaseDatabase.getReference(COMMENT_KEY).child(id);
        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listModelComment.clear(); // 确保在添加数据前清空列表
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Model_Comment modelComment = snap.getValue(Model_Comment.class);
                    if (modelComment != null) {
                        listModelComment.add(modelComment);
                    }
                }
                Collections.reverse(listModelComment); // Reverse the list
                adapterComment.notifyDataSetChanged(); // Notify adapter
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("FirebaseError", "Failed to read comment: " + databaseError.getMessage());
            }
        });
    }


    private void showMessage(String message) {
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }

    private void checkLikeStatus() {
        DocumentReference blogRef = FirebaseFirestore.getInstance().collection("POSTs").document(id);
        blogRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    List<String> likedBy = (List<String>) documentSnapshot.get("likedBy");
                    if (likedBy != null && likedBy.contains(firebaseUser.getUid())) {
                        binding.btnLiked.setImageResource(R.drawable.liked);  // 已点赞状态图标
                    } else {
                        binding.btnLiked.setImageResource(R.drawable.like);  // 未点赞状态图标
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("BlogDetail", "Error getting document: ", e);
            }
        });
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scale *= detector.getScaleFactor();
            scale = Math.max(0.1f, Math.min(scale, 5.0f)); // 设置缩放范围
            matrix.setScale(scale, scale);
            binding.imageViewPostImage.setImageMatrix(matrix);
            return true;
        }
    }

    private void showData() {
        id = getIntent().getStringExtra("id");
        checkLikeStatus(); // Check and set the like status

        FirebaseFirestore.getInstance().collection("POSTs").document(id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    // Converting the document snapshot to a Model_Post object
                    Model_Post post = documentSnapshot.toObject(Model_Post.class);
                    if (post != null) {
                        Glide.with(getApplicationContext()).load(post.getImg()).into(binding.imageViewPostImage);
                        binding.textViewName.setText(Html.fromHtml("<font color='B7B7B7'>By </font> <font color='#000000'>" + post.getNickname()));
                        binding.textViewTitle.setText(post.getTitle());
                        binding.textViewContents.setText(post.getContent());

                        // Convert timestamp if it's stored as a long
                        Long timeMillis = post.getTimestamp();
                        if (timeMillis != null) {
                            binding.textViewPostDate.setText("Published on: " + formatDate(timeMillis));
                        }

                        title = post.getTitle();
                        content = post.getContent();

                        // Load the profile image of the post creator
                        if (post.getUserImg() != null && !post.getUserImg().isEmpty()) {
                            Glide.with(getApplicationContext()).load(post.getUserImg()).into(binding.imageViewPerson);
                        } else {
                            // Default image if user image is not available
                            binding.imageViewPerson.setImageResource(R.drawable.ic_dafault_user_photo);
                        }
                    }
                } else if (error != null) {
                    Log.e("PostDetail", "Error loading post", error);
                }
            }
        });

        binding.btnLiked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference postRef = db.collection("POSTs").document(id);

                postRef.get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> likedBy = (List<String>) documentSnapshot.get("likedBy");
                        if (likedBy != null && likedBy.contains(firebaseUser.getUid())) {
                            // 如果已经点赞了，这次点击是为了取消点赞
                            postRef.update(
                                    "likes", FieldValue.increment(-1),
                                    "likedBy", FieldValue.arrayRemove(firebaseUser.getUid())
                            ).addOnSuccessListener(aVoid -> {
                                binding.btnLiked.setImageResource(R.drawable.like);  // 更新为未点赞图标
                                Toast.makeText(PostDetail.this, "Like removed!", Toast.LENGTH_SHORT).show();
                            }).addOnFailureListener(e -> {
                                Toast.makeText(PostDetail.this, "Error updating like: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                        } else {
                            // 如果还没点赞，这次点击是为了点赞
                            postRef.update(
                                    "likes", FieldValue.increment(1),
                                    "likedBy", FieldValue.arrayUnion(firebaseUser.getUid())
                            ).addOnSuccessListener(aVoid -> {
                                binding.btnLiked.setImageResource(R.drawable.liked);  // 更新为已点赞图标
                                Toast.makeText(PostDetail.this, "Like successfully", Toast.LENGTH_SHORT).show();
                            }).addOnFailureListener(e -> {
                                Toast.makeText(PostDetail.this, "Error updating like: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                        }
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(PostDetail.this, "Error fetching post details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });


        // 设置图片点击事件，用于放大和缩小
        binding.imageViewPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isImageZoomed) {
                    // 放大图片
                    binding.imageViewPostImage.animate().scaleX(2f).scaleY(2f).setDuration(300).start();
                    isImageZoomed = true;
                } else {
                    // 缩小图片
                    binding.imageViewPostImage.animate().scaleX(1f).scaleY(1f).setDuration(300).start();
                    isImageZoomed = false;
                }
            }
        });
//        binding.btu_back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });
    }

    private String formatDate(long time) {
        Calendar now = Calendar.getInstance();
        Calendar timeToCheck = Calendar.getInstance();
        timeToCheck.setTimeInMillis(time);

        // Check if the date is today
        if (now.get(Calendar.YEAR) == timeToCheck.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) == timeToCheck.get(Calendar.DAY_OF_YEAR)) {
            return "Today " + DateFormat.format("HH:mm", timeToCheck);
        }
        // Check if the date was yesterday
        else if (now.get(Calendar.YEAR) == timeToCheck.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) - 1 == timeToCheck.get(Calendar.DAY_OF_YEAR)) {
            return "Yesterday " + DateFormat.format("HH:mm", timeToCheck);
        }
        // For other dates
        else {
            return DateFormat.format("dd-MM-yyyy HH:mm", timeToCheck).toString();
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editTextComment.getWindowToken(), 0);
    }
}