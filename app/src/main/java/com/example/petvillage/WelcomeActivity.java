package com.example.petvillage;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class WelcomeActivity extends AppCompatActivity {

    private static final int SPLASH_TIME_OUT = 2500;  // Splash screen timer

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser == null) {
                    // No user is signed in
                    Intent loginIntent = new Intent(WelcomeActivity.this, Google_Login.class);
                    startActivity(loginIntent);
                    finish();
                } else {
                    // User is signed in
                    Intent mainIntent = new Intent(WelcomeActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                }
            }
        }, SPLASH_TIME_OUT);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MusicServer.stop(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MusicServer.play(this, R.raw.mixkit_happy_puppy_barks);
    }
}
