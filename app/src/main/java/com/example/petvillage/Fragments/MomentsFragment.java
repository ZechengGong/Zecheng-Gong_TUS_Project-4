package com.example.petvillage.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.petvillage.Adapters.Adapter_Post;
import com.example.petvillage.R;
import com.example.petvillage.databinding.FragmentMomentsBinding;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import com.example.petvillage.Models.Model_Post;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MomentsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

// MomentsFragment class
public class MomentsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FragmentMomentsBinding binding; // Fragment binding class instance, used to conveniently operate the views in the layout
    private ArrayList<Model_Post> list; // Post data list
    private Adapter_Post adapterPost; // Post list adapter
    // private Model_Post modelPost;

    public MomentsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MomentsFragment.
     */
    // TODO: Rename and change types and number of parameters

    // Create instances & initialization parameters
    public static MomentsFragment newInstance(String param1, String param2) {
        MomentsFragment fragment = new MomentsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    // private ActivityResultLauncher<String> mGetContent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get initialization parameters from Bundle
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private static final int REQUEST_CODE_IMAGE = 101;

    // private DatabaseReference DataRef;
    // private StorageReference StorageRef;

    // Create and return the Fragment view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        View Moments = inflater.inflate(R.layout.fragment_moments, container, false);
//        return Moments;

        // Use the Binding class to initialize the layout
        binding = FragmentMomentsBinding.inflate(inflater,container,false);
        return binding.getRoot(); // Return to the root view
    }

    // Callback after the view is created
    @Override
    public void onViewCreated(@NonNull View view, @androidx.annotation.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRv(); // Set up RecyclerView
        setSearchView(); // Set the search view

        // Click the help icon to display the dialog box
        ImageView imgRightIcon = view.findViewById(R.id.imgICQuestion);
        imgRightIcon.setOnClickListener(v -> showIconDialog());

//        imgRightIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showIconDialog();
//            }
//        });
    }

    // Set search view logic
    private void setSearchView() {
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Processing when text is submitted
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Processing when text changes, calling filtering method
                filter(newText);
                return false;
            }
        });
    }

    // Filter the post list
    private void filter(String newText) {
        ArrayList<Model_Post> filtered_list = new ArrayList<>();
        for(Model_Post item : list) {
            if ((item.getTitle() != null && item.getTitle().toLowerCase().contains(newText.toLowerCase())) ||
                    (item.getNickname() != null && item.getNickname().toLowerCase().contains(newText.toLowerCase()))) {
                filtered_list.add(item);
            }
        }

        // Handle the case where the filter result is empty
        if (filtered_list.isEmpty()) {
            Toast.makeText(getContext(), "Found nothing related...", Toast.LENGTH_SHORT).show();
        } else {
            adapterPost.filter_list(filtered_list); // Update adapter data
        }
    }

    // Initialize RecyclerView
    private void setupRv() {
        list = new ArrayList<>();
        FirebaseFirestore.getInstance().collection("POSTs").orderBy("timestamp").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@androidx.annotation.Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore Error", error.getMessage(), error);
                    return;
                }

                if (value != null) {
                    list.clear();
                    for (DocumentSnapshot snapshot : value.getDocuments()) {
                        Model_Post modelPost = snapshot.toObject(Model_Post.class);
                        if (modelPost != null) { // Make sure the conversion is successful
                            modelPost.setId(snapshot.getId());
                            list.add(modelPost);
                        }
                    }
                    adapterPost.notifyDataSetChanged(); // Notify data changes
                } else {
                    Log.d("Firestore Info", "No data found");
                }
            }
        });

        adapterPost = new Adapter_Post(list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        binding.rvBlogs.setLayoutManager(linearLayoutManager);
        binding.rvBlogs.setAdapter(adapterPost);
    }

    // Show the edit prompt dialog box
    private void showIconDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setIcon(R.drawable.ic_hint_3)
                .setTitle("Editing tips")
                .setMessage("LONG PRESS\non a post or a comment to edit.")
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding=null; // Clean up Binding references to avoid memory leaks
    }
}