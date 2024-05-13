package com.example.petvillage.Google;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.petvillage.Others.WelcomeActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

// Google_Logout class, used to implement the user's logout function
public class Google_Logout extends AppCompatActivity {
    private GoogleSignInClient googleSignInClient; // Google login client object

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configure Google login options and request the user's email
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Construct a GoogleSignInClient based on the given Google login options
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Log out of Google first
        googleSignInClient.signOut().addOnCompleteListener(this, task -> {
            // After successfully logging out of Google, log out from FirebaseAuth
            FirebaseAuth.getInstance().signOut();

            // Clean up application data and SharedPreferences
            clearApplicationData();
            clearAllSharedPreferences();

            // Redirect to WelcomeActivity after logging out
            Intent intent = new Intent(this, WelcomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();  // End the current activity and remove it from the return stack
        });
    }

    // Clear application data
    private void clearApplicationData() {
        // Clear specific SharedPreferences data stored in the application
        SharedPreferences preferences = getSharedPreferences("AppName", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply(); // Apply changes
    }

    // Clear all SharedPreferences
    private void clearAllSharedPreferences() {
        // Additional SharedPreferences to clear if necessary
        SharedPreferences preferences = getSharedPreferences("OtherPreferences", Context.MODE_PRIVATE);
        preferences.edit().clear().apply(); // Apply changes
    }
}
