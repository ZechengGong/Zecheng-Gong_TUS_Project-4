package com.example.petvillage.Google;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.petvillage.Main.MainActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import android.widget.Toast;

// Google_Login class, used to implement the function of users logging in through Google account
public class Google_Login extends AppCompatActivity {
    private GoogleSignInClient mGoogleSignInClient; // Google login client
    private ActivityResultLauncher<Intent> signInLauncher; // Result launcher for launching login intent
    private static final int RC_SIGN_IN = 100; // Login request code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configure Google login options, request user ID, email address and basic personal data
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Create a GoogleSignInClient instance using the specified configuration
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Initialize ActivityResultLauncher, used to process login results
        signInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        handleSignInResult(task); // Process login results
                    }
                }
        );
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Get login intent and start
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        signInLauncher.launch(signInIntent);  // Use launcher to start login activity
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handling when results are returned from an intent initiated by GoogleSignInClient.getSignInIntent()
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task); // Process login results
        }
    }

    // Handle Google login results
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Login successful, update the UI to display the interface where the user has logged in
            updateUI(account);
        } catch (ApiException e) {
            // ApiException status code indicates the specific failure reason
            updateUI(null);
        }
    }

    private void updateUI(GoogleSignInAccount account) {
        if (account != null) {
            // After successful login, jump to MainActivity or other activities
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish(); //End
        } else {
            // Show login failed message
            Toast.makeText(this, "Login failed. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }
}
