package com.example.petvillage.Main;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.petvillage.Fragments.HomeFragment;
import com.example.petvillage.Fragments.MomentsFragment;
import com.example.petvillage.Fragments.PostFragment;
import com.example.petvillage.Fragments.ServiceFragment;
import com.example.petvillage.Google.Google_Login;
import com.example.petvillage.Google.Google_Logout;
import com.example.petvillage.Others.About;
import com.example.petvillage.Others.IOnBackPressed;
import com.example.petvillage.R;
import com.example.petvillage.databinding.ActivityMainBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

// MainActivity class, as the main interface of the application
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;  // View binding object for easy access to views in the layout
    private DrawerLayout drawerLayout; // DrawerLayout provides a sliding navigation menu
    private NavigationView navigationView; // Navigation view component
    private ActionBarDrawerToggle drawerToggle; // ActionBarDrawerToggle, used to control the drawer
    private GoogleSignInOptions signInOptions; // Google login configuration
    private GoogleSignInClient signInClient; // Google login client
    private FirebaseAuth auth; // Firebase authentication object
    private ActivityResultLauncher<Intent> signInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_main);
        // replaceFragment(new HomeFragment());

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Bottom navigation view settings
        binding.bottomNavigationView.setBackground(null);  // remove background

        // Handle the click event of the bottom navigation bar
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            switch (item.getItemId()) {
                case R.id.home:
                    replaceFragment(new HomeFragment()); // Switch to home page Fragment
                    break;
                case R.id.shopping:
                    replaceFragment(new ServiceFragment()); // Switch to shopping fragment
                    break;
                case R.id.moments:
                    replaceFragment(new MomentsFragment()); // Switch to dynamic Fragment
                    break;
                case R.id.assistant:
                    showAssistantDialog(); // Show assistant dialog
                    break;
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Set the floating button click event to display the bottom dialog box
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomDialog();
            }
        });

        // Initialize DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.drawer_view);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Navigation view item click event handling
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home: {
                        Toast.makeText(MainActivity.this, "You are already on the Home Page", Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    }
                    case R.id.contact: {
                        Intent i = new Intent();
                        i.setAction(Intent.ACTION_DIAL);
                        i.setData(Uri.parse("tel:" + 353123456));
                        startActivity(i);
                        break;
                    }
                    case R.id.email: {
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"puppy@petvillage.ie"});
                        i.putExtra(Intent.EXTRA_SUBJECT, "From: ");
                        i.putExtra(Intent.EXTRA_TEXT, "To Pet Village Team: ");
                        i.setType("message/rfc822");
                        startActivity(Intent.createChooser(i, "Send Mail Using :"));
                        break;
                    }
                    case R.id.about: {
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent goAbout = new Intent(MainActivity.this, About.class);
                        startActivity(goAbout);
                        overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
                        break;
                    }

                    case R.id.login: {
                        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                            Toast.makeText(MainActivity.this, "You are ALREADY logged in.", Toast.LENGTH_LONG).show();
                            drawerLayout.closeDrawer(GravityCompat.START);
                        } else {
                            // The user is not logged in, jump to the login page
                            Intent loginIntent = new Intent(MainActivity.this, Google_Login.class);
                            startActivity(loginIntent);
                            overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
                        }
                        break;
                    }

                    case R.id.logout: {
                        Intent logout = new Intent(MainActivity.this, Google_Logout.class);
                        startActivity(logout);
                        overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
                        break;
                    }
                }
                return true;
            }
        });

        // Initialize Google login and Firebase authentication
        setupSignIn();

        // Set ActivityResultLauncher
        signInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        handleSignInResult(task); // Handle the sign-in result
                    } else {
                        // Optionally handle other result codes
                        Toast.makeText(getApplicationContext(), "Sign-In Cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
        );


        setFragment(new HomeFragment());
    }

    // Check if the user is logged in when the activity starts, if not, try to log in automatically
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            signIn();
        }
    }

    // Behavior definition when the user presses the return key
    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.home);
        if (!(fragment instanceof IOnBackPressed) || !((IOnBackPressed) fragment).onBackPressed()) {
            super.onBackPressed();
        }
    }

    // Handle the click event of the drawer switch when creating the menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupSignIn() {
        auth = FirebaseAuth.getInstance();
        signInOptions=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        signInClient= GoogleSignIn.getClient(this,signInOptions);
    }

    public void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .commit();
    }

    private void displayFragment(Fragment fragment) {
        // Replace and display the specified Fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment); // Make sure frame_layout is your container ID
        fragmentTransaction.commitAllowingStateLoss();  // Use commitAllowingStateLoss to prevent exceptions
    }

    private void signIn() {
        Intent signInIntent = signInClient.getSignInIntent();
        signInLauncher.launch(signInIntent); // Start the sign-in process
    }

    // Method for processing login results
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null) {
                // Get authentication credentials using account information
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                // Use credentials for Firebase authentication
                auth.signInWithCredential(credential)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                // login successful
                                Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_LONG).show();
                                updateUI(auth.getCurrentUser()); //Update UI display
                            } else {
                                // Login failed
                                Toast.makeText(getApplicationContext(), "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                updateUI(null); //Update UI display
                            }
                        });
            }
        } catch (ApiException e) {
            // Handle API exceptions
            Toast.makeText(getApplicationContext(), "Login Failed: " + e.getStatusCode(), Toast.LENGTH_LONG).show();
            updateUI(null); // Update UI display
        }
    }

    // Update the interface based on the user's login status
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // The user logs in successfully and goes to the main interface or other logic
            setFragment(new HomeFragment());
        } else {
            // The user is not logged in or failed to log in, and an error message is displayed.
            Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==100){
            Task<GoogleSignInAccount> task=GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
                auth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_LONG).show();
                            displayFragment(new HomeFragment());  // Display Fragment correctly
                        }else{
                            Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }

    // Replace the currently displayed Fragment
    private void replaceFragment(Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);  // Make sure to add this line of code
        fragmentTransaction.commit();
    }

    // Show the bottom dialog box
    private void showBottomDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet);

        LinearLayout postsLayout = dialog.findViewById(R.id.layoutPosts);
        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);

        postsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPublishFragment();
                dialog.dismiss();

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    // Display the Fragment of the published post
    public void showPublishFragment() {
        PostFragment postFragment = new PostFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, postFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    // Display the assistant dialog box, indicating that the function is not yet open
    private void showAssistantDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.pet_care);
        builder.setTitle("Sorry");
        builder.setMessage("The function is not yet available.");
        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}