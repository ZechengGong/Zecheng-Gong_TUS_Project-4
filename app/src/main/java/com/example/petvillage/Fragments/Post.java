package com.example.petvillage.Fragments;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.petvillage.Fragments.MomentsFragment;
import com.example.petvillage.R;
import com.example.petvillage.databinding.FragmentPostBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Date;
import java.util.HashMap;

public class Post extends Fragment {

    public FragmentPostBinding binding;
    public Uri filepath;

    public Post() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPostBinding.inflate(inflater, container, false);
        binding.btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filepath != null) { // 确保图片已选择
                    uploadData(filepath); // 仅在点击时上传
                } else {
                    Toast.makeText(getContext(), "Please select an image first.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        selectImage();

        // 获取当前用户实例
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // 获取用户的显示名称
            String displayName = user.getDisplayName();
            // 如果displayName不为空，则设置到et_nickname
            if (displayName != null && !displayName.isEmpty()) {
                binding.etNickname.setText(displayName);
            } else {
                // 如果没有设置显示名称，可以提示用户或者留空
                binding.etNickname.setText(""); // 或者提示用户 "未设置昵称"
            }
        }

    }

    private void selectImage() {
        binding.view2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Your Image"), 101);  // 注意这里是在Fragment中调用
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filepath = data.getData(); // Save the image's Uri to upload later
            binding.imgThumbnail.setVisibility(View.VISIBLE);
            binding.imgThumbnail.setImageURI(filepath); // Display the image preview
            binding.view2.setVisibility(View.INVISIBLE);
            binding.bSelectImage.setVisibility(View.INVISIBLE);

            // Inform the user that they can long-press to delete the image
            Toast.makeText(getContext(), "Tap and hold the image to delete it.", Toast.LENGTH_LONG).show();

            // Set long click listener to delete the selected image
            binding.imgThumbnail.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    deleteSelectedImage();
                    return true; // Indicates the callback consumed the long click
                }
            });
        }
    }

    private void deleteSelectedImage() {
        filepath = null; // Remove the reference to the image
        binding.imgThumbnail.setImageURI(null); // Clear the image view
        binding.imgThumbnail.setVisibility(View.GONE);
        binding.view2.setVisibility(View.VISIBLE);
        binding.bSelectImage.setVisibility(View.VISIBLE);
        Toast.makeText(getContext(), "Image removed. You can select a new one.", Toast.LENGTH_SHORT).show();
    }


    private void uploadData(Uri filepath) {
        String title = binding.etTitle.getText().toString().trim();
        String content = binding.etContent.getText().toString().trim();
        String nickname = binding.etNickname.getText().toString().trim();
        String currentDate = DateFormat.format("dd MMM yyyy", new Date()).toString();  // 格式化当前日期

        if (title.isEmpty() || content.isEmpty() || nickname.isEmpty()) {
            if (title.isEmpty()) binding.etTitle.setError("Title is required");
            if (content.isEmpty()) binding.etContent.setError("Content is required");
            if (nickname.isEmpty()) binding.etNickname.setError("Nickname is required");
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Loading...");
        progressDialog.setMessage("Please wait, uploading post...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser != null ? currentUser.getUid() : null;
        String userImgUrl = currentUser != null && currentUser.getPhotoUrl() != null ? currentUser.getPhotoUrl().toString() : null;

        // String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();  // 获取用户ID

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("post_images/" + System.currentTimeMillis() + ".jpg");
        storageRef.putFile(filepath)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    HashMap<String, Object> postMap = new HashMap<>();
                    postMap.put("title", title);
                    postMap.put("content", content);
                    postMap.put("nickname", nickname);
                    postMap.put("userId", userId);
                    postMap.put("img", uri.toString());
                    postMap.put("userImg", userImgUrl);  // 添加用户头像URL
                    postMap.put("date", currentDate);  // 添加当前日期
                    postMap.put("timestamp", System.currentTimeMillis());

                    FirebaseFirestore.getInstance().collection("POSTs").add(postMap)
                            .addOnSuccessListener(documentReference -> {
                                progressDialog.dismiss();
                                Toast.makeText(getContext(), "Post uploaded successfully", Toast.LENGTH_LONG).show();
                                resetPublishForm();
                                navigateToMomentsFragment();
                            })
                            .addOnFailureListener(e -> {
                                progressDialog.dismiss();
                                Toast.makeText(getContext(), "Failed to upload post: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                }))
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Failed to upload image: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void resetPublishForm() {
        binding.etTitle.setText("");
        binding.etContent.setText("");
        binding.etNickname.setText("");
        binding.imgThumbnail.setVisibility(View.INVISIBLE);
        binding.bSelectImage.setVisibility(View.VISIBLE);
    }

//    private void showSettingdialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//        builder.setTitle("Need Permission");
//        builder.setMessage("This app needs permission to use this feature. You can grant us these permission manually by clicking on below button");
//        builder.setPositiveButton("Next", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                Uri uri = Uri.fromParts("package", getActivity().getPackageName(),null);
//                intent.setData(uri);
//                startActivityForResult(intent, 101);
//            }
//        });
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//                getActivity().finish();
//            }
//        });
//        builder.show();
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    private void navigateToMomentsFragment() {
        if (isAdded() && getActivity() != null) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            MomentsFragment momentsFragment = new MomentsFragment();
            fragmentTransaction.replace(R.id.frame_layout, momentsFragment); // 确保 'container' 是你的FrameLayout的ID
            fragmentTransaction.commit();

            // 设置底部导航栏的当前选中项
            BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
            bottomNavigationView.setSelectedItemId(R.id.moments); // 替换为导航到 MomentsFragment 对应的菜单项ID
        }
    }

}