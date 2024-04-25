package com.example.petvillage;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
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

import com.example.petvillage.databinding.FragmentPostBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
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
        binding.btnPublish.setOnClickListener(new View.OnClickListener() {
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
        selectImage();
        super.onViewCreated(view, savedInstanceState);
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
            filepath = data.getData(); // 保存图片的Uri，稍后上传
            binding.imgThumbnail.setVisibility(View.VISIBLE);
            binding.imgThumbnail.setImageURI(filepath); // 显示图片预览
            binding.view2.setVisibility(View.INVISIBLE);
            binding.bSelectImage.setVisibility(View.INVISIBLE);
        }
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
        progressDialog.setTitle("Uploading...");
        progressDialog.setMessage("Please wait, uploading post");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();  // 获取用户ID

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