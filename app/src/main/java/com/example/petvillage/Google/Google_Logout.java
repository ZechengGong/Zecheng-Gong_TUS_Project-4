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

public class Google_Logout extends AppCompatActivity {

    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);



        // Sign out from Google first
        googleSignInClient.signOut().addOnCompleteListener(this, task -> {
            // Now sign out from FirebaseAuth
            FirebaseAuth.getInstance().signOut();
            clearApplicationData();
            clearAllSharedPreferences();

            // Redirect to WelcomeActivity after sign out
            Intent intent = new Intent(this, WelcomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();  // Close the current activity
        });
    }

    private void clearApplicationData() {
        // Your logic to clear app data
        SharedPreferences preferences = getSharedPreferences("AppName", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

    private void clearAllSharedPreferences() {
        // Additional SharedPreferences to clear if necessary
        SharedPreferences preferences = getSharedPreferences("OtherPreferences", Context.MODE_PRIVATE);
        preferences.edit().clear().apply();
    }
}
