package com.example.petvillage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.net.URL;

public class SignHomeActivity extends AppCompatActivity {

    private TextView name,mail;
    private Button logout;
    private ImageView photo;
    private CardView cardView;

    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_home);

        name = findViewById(R.id.name);
        mail = findViewById(R.id.mail);
        logout = findViewById(R.id.logout);
        photo = findViewById(R.id.photo);
        cardView = findViewById(R.id.card_homepage);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();

        gsc = GoogleSignIn.getClient(this,gso);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account!=null){

            String Name = account.getDisplayName();
            String Mail = account.getEmail();
//            Uri Photo = account.getPhotoUrl();
//
//            if(account.getPhotoUrl() == null){
//                photo.setImageResource(R.drawable.dog);
//            } else {
//                Uri photo_url = account.getPhotoUrl(); //photo_url is String
//                photo.setImageURI(photo_url);
//            }
            name.setText("Dear " + Name);
            mail.setText(Mail);

        }

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignOut();
            }
        });

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent go = new Intent(SignHomeActivity.this,MainActivity.class);
                startActivity(go);
                overridePendingTransition(R.transition.slide_in_left,R.transition.slide_out_right);// transition

            }
        });
    }

    private void SignOut() {

        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                finish();
                startActivity(new Intent(getApplicationContext(),Login.class));

            }
        });

    }
}