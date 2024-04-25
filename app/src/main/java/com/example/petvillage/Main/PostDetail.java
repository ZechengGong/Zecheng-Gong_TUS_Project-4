package com.example.petvillage.Main;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
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
import com.example.petvillage.R;
import com.example.petvillage.databinding.ActivityBlogDetailBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.List;

import com.example.petvillage.Models.Model_Comment;


public class PostDetail extends AppCompatActivity {
    private ActivityBlogDetailBinding binding;
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

    private static String COMMENT_KEY = "Comment" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBlogDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        showData();

        RvComment = findViewById(R.id.commentsRecyclerView);
        editTextComment = findViewById(R.id.post_detail_comment);
        btnAddComment = findViewById(R.id.post_detail_add_comment_btn);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        // add Comment button click listner
        btnAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment_content = editTextComment.getText().toString().trim();  // 获取输入并去除两端空白
                if (comment_content.isEmpty()) {
                    Toast.makeText(PostDetail.this, "Comment cannot be empty!", Toast.LENGTH_SHORT).show();
                    btnAddComment.setVisibility(View.VISIBLE);  // 重新显示评论按钮
                    return;  // 退出处理过程，不执行添加评论
                }

                btnAddComment.setVisibility(View.INVISIBLE);
                DatabaseReference commentReference = firebaseDatabase.getReference(COMMENT_KEY).child(id).push();
                String uid = firebaseUser.getUid();
                String uname = firebaseUser.getDisplayName();
                String uimg = (firebaseUser.getPhotoUrl() != null) ? firebaseUser.getPhotoUrl().toString() : "default_user_image_url";
                Model_Comment modelComment = new Model_Comment(comment_content, uid, uimg, uname);

                Log.d("CommentDebug", "UID: " + uid + ", Name: " + uname + ", Image URL: " + uimg);

                String commentPath = COMMENT_KEY + "/" + id;
                Log.d("CommentDebug", "Reference Path: " + commentPath);

                DatabaseReference testRef = firebaseDatabase.getReference("Test").child("TestChild");
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

                commentReference.setValue(modelComment).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("CommentDebug", "Comment added successfully");
                        showMessage("Comment added");
                        editTextComment.setText("");
                        btnAddComment.setVisibility(View.VISIBLE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("CommentDebug", "Failed to add comment: " + e.getMessage());
                        showMessage("Fail to add comment : " + e.getMessage());
                        btnAddComment.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
        iniRvComment();
    }

    private void iniRvComment() {

        RvComment.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference commentRef = firebaseDatabase.getReference(COMMENT_KEY).child(id);
        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listModelComment = new ArrayList<>();
                for (DataSnapshot snap:dataSnapshot.getChildren()) {

                    Model_Comment modelComment = snap.getValue(Model_Comment.class);
                    listModelComment.add(modelComment) ;
                }
                adapterComment = new Adapter_Comment(getApplicationContext(), listModelComment);
                RvComment.setAdapter(adapterComment);
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
                        binding.floatingActionButton.setImageResource(R.drawable.liked);  // 已点赞状态图标
                    } else {
                        binding.floatingActionButton.setImageResource(R.drawable.like);  // 未点赞状态图标
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


    private void showData() {
        id = getIntent().getStringExtra("id");

        checkLikeStatus();  // 检查并设置点赞状态

        FirebaseFirestore.getInstance().collection("POSTs").document(id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null && value.exists()) {
                    Glide.with(getApplicationContext()).load(value.getString("img")).into(binding.imageView3);
                    binding.textView4.setText(Html.fromHtml("<font color='B7B7B7'>By </font> <font color='#000000'>" + value.getString("nickname")));
                    binding.textView5.setText(value.getString("title"));
                    binding.textView6.setText(value.getString("content"));
                    title = value.getString("title");
                    content = value.getString("content");
                }
            }
        });

        binding.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference postRef = db.collection("POSTs").document(id);

                postRef.update("likes", FieldValue.increment(1), "likedBy", FieldValue.arrayUnion(firebaseUser.getUid()))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                binding.floatingActionButton.setImageResource(R.drawable.liked);  // 更新为已点赞图标
                                Toast.makeText(PostDetail.this, "Liked!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(PostDetail.this, "Error liking post: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        binding.imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}