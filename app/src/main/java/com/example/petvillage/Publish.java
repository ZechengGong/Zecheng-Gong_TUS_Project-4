package com.example.petvillage;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.example.petvillage.databinding.FragmentPublishBinding;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Publish extends Fragment {

    public FragmentPublishBinding binding;
    public Uri filepath;

    public Publish() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPublishBinding.inflate(inflater, container, false);
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
        String title = binding.bTitle.getText().toString().trim();
        String content = binding.bDesc.getText().toString().trim();
        String nickName = binding.bAuthor.getText().toString().trim();
        String currentDate = DateFormat.format("dd MMM yyyy", new Date()).toString();  // 格式化当前日期

        if (title.isEmpty() || content.isEmpty() || nickName.isEmpty()) {
            if (title.isEmpty()) binding.bTitle.setError("Title is required");
            if (content.isEmpty()) binding.bDesc.setError("Content is required");
            if (nickName.isEmpty()) binding.bAuthor.setError("Nickname is required");
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
                    postMap.put("nickName", nickName);
                    postMap.put("userId", userId);
                    postMap.put("img", uri.toString());
                    postMap.put("date", currentDate);  // 添加当前日期
                    postMap.put("timestamp", System.currentTimeMillis());

                    FirebaseFirestore.getInstance().collection("POSTs").add(postMap)
                            .addOnSuccessListener(documentReference -> {
                                progressDialog.dismiss();
                                Toast.makeText(getContext(), "Post uploaded successfully!", Toast.LENGTH_LONG).show();
                                resetPublishForm();
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
        binding.bTitle.setText("");
        binding.bDesc.setText("");
        binding.bAuthor.setText("");
        binding.imgThumbnail.setVisibility(View.INVISIBLE);
        binding.bSelectImage.setVisibility(View.VISIBLE);
    }

    private void showSettingdialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Need Permission");
        builder.setMessage("This app needs permission to use this feature. You can grant us these permission manually by clicking on below button");
        builder.setPositiveButton("Next", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getActivity().getPackageName(),null);
                intent.setData(uri);
                startActivityForResult(intent, 101);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                getActivity().finish();
            }
        });
        builder.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}