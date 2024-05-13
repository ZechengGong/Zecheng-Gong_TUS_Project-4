package com.example.petvillage.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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

// Post Fragment
public class PostFragment extends Fragment {

    public FragmentPostBinding binding; //View Binding to facilitate the operation of view components
    public Uri filepath; // Used to store the image path selected from the gallery

    public PostFragment() {
        // Required empty public constructor
    }

    // Register a listener for startup activity results, used to handle callbacks after selecting pictures from the gallery
    private ActivityResultLauncher<Intent> activityResultLauncher;

    private AlertDialog progressDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Register activity result launcher to process gallery image selection results
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null && data.getData() != null) {
                                filepath = data.getData();
                                // Show the selected image and hide the select button
                                binding.imgThumbnail.setVisibility(View.VISIBLE);
                                binding.imgThumbnail.setImageURI(filepath);
                                binding.view2.setVisibility(View.INVISIBLE);
                                binding.bSelectImage.setVisibility(View.INVISIBLE);

                                // Prompt the user to long press the image to delete it
                                Toast.makeText(getContext(), "Tap and hold the image to delete it.", Toast.LENGTH_LONG).show();
                                binding.imgThumbnail.setOnLongClickListener(v -> {
                                    deleteSelectedImage();
                                    return true;
                                });
                            }
                        }
                    }
                }
        );

        createProgressDialog();
    }

    private void createProgressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.progress_dialog, null);
        builder.setView(dialogView);
        builder.setCancelable(false);

        progressDialog = builder.create();
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

    private void showProgressDialog() {
        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPostBinding.inflate(inflater, container, false);

        //Listen for post button
        binding.btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filepath != null) {   // Make sure the image is selected
                    uploadData(filepath); // Only upload on click
                } else {
                    Toast.makeText(getContext(), "Please select an image first.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return binding.getRoot();
    }

    // Set other view events after the view is created
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        selectImage();

        // Set the current user nickname/user instance
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Get the user's display name
            String displayName = user.getDisplayName();
            // If displayName is not empty, set it to et_nickname
            if (displayName != null && !displayName.isEmpty()) {
                binding.etNickname.setText(displayName);
            } else {
                // If the display name is not set, prompt the user or leave it blank
                binding.etNickname.setText(""); // Or prompt the user "Nickname not set"
            }
        }
    }

    // Select Image
    private void selectImage() {
        binding.view2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                activityResultLauncher.launch(Intent.createChooser(intent, "Select a image"));  // called in Fragment
            }
        });
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == 101 && resultCode == RESULT_OK && data != null && data.getData() != null) {
//            filepath = data.getData(); // Save the image's Uri to upload later
//            binding.imgThumbnail.setVisibility(View.VISIBLE);
//            binding.imgThumbnail.setImageURI(filepath); // Display the image preview
//            binding.view2.setVisibility(View.INVISIBLE);
//            binding.bSelectImage.setVisibility(View.INVISIBLE);
//
//            // Inform the user that they can long-press to delete the image
//            Toast.makeText(getContext(), "Tap and hold the image to delete it.", Toast.LENGTH_LONG).show();
//
//            // Set long click listener to delete the selected image
//            binding.imgThumbnail.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    deleteSelectedImage();
//                    return true; // Indicates the callback consumed the long click
//                }
//            });
//        }
//    }

    // Delete selected images
    private void deleteSelectedImage() {
        filepath = null; // Remove the reference to the image
        binding.imgThumbnail.setImageURI(null); // Clear the image view
        binding.imgThumbnail.setVisibility(View.GONE);
        binding.view2.setVisibility(View.VISIBLE);
        binding.bSelectImage.setVisibility(View.VISIBLE);
        Toast.makeText(getContext(), "Image removed. You can select a new one.", Toast.LENGTH_SHORT).show();
    }

    // Upload post data
    private void uploadData(Uri filepath) {
        String title = binding.etTitle.getText().toString().trim();
        String content = binding.etContent.getText().toString().trim();
        String nickname = binding.etNickname.getText().toString().trim();
        String currentDate = DateFormat.format("dd MMM yyyy", new Date()).toString(); //Format current date

        // Check if the input field is empty
        if (title.isEmpty() || content.isEmpty() || nickname.isEmpty()) {
            if (title.isEmpty()) binding.etTitle.setError("Title is required");
            if (content.isEmpty()) binding.etContent.setError("Content is required");
            if (nickname.isEmpty()) binding.etNickname.setError("Nickname is required");
            return;
        }

        // Get user ID and avatar URL
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser != null ? currentUser.getUid() : null;
        String userImgUrl = currentUser != null && currentUser.getPhotoUrl() != null ? currentUser.getPhotoUrl().toString() : null;
        // String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        showProgressDialog(); // Before uploading
        // Upload files to Firebase Storage and add post data to Firestore
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
                    postMap.put("userImg", userImgUrl);  // Add user avatar URL
                    postMap.put("date", currentDate);  // Add current date
                    postMap.put("timestamp", System.currentTimeMillis());

                    FirebaseFirestore.getInstance().collection("POSTs").add(postMap)
                            .addOnSuccessListener(documentReference -> {
                                progressDialog.dismiss();
                                Toast.makeText(getContext(), "Post uploaded successfully", Toast.LENGTH_SHORT).show();
                                resetPublishForm(); // Reset form
                                navigateToMomentsFragment(); // Navigate to MomentsFragment
                                hideProgressDialog(); // Hide on success
                            })
                            .addOnFailureListener(e -> {
                                progressDialog.dismiss();
                                Toast.makeText(getContext(), "ERROR: Failed to upload post: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                hideProgressDialog(); // Hide on failure
                            });
                }))
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "ERROR: Failed to upload image: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    // Reset the posting form
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
        hideProgressDialog(); // Ensure dialog is hidden to prevent leaks
        binding = null; // Clear bindings to avoid memory leaks
    }

    // Navigate to MomentsFragment
    private void navigateToMomentsFragment() {
        if (isAdded() && getActivity() != null) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            MomentsFragment momentsFragment = new MomentsFragment();
            fragmentTransaction.replace(R.id.frame_layout, momentsFragment); // Make sure 'container' is the ID of your FrameLayout
            fragmentTransaction.commit();

            // Set the currently selected item in the bottom navigation bar
            BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
            bottomNavigationView.setSelectedItemId(R.id.moments); // Replace with the menu item ID corresponding to the navigation to MomentsFragment
        }
    }
}