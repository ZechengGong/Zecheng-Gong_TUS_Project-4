package com.example.petvillage.Others;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.petvillage.Google.Login;
import com.example.petvillage.Google.Maps_Dublin;
import com.example.petvillage.Google.Maps_Galway;
import com.example.petvillage.Google.SignHomeActivity;
import com.example.petvillage.Main.MainActivity;
import com.example.petvillage.R;
import com.google.android.material.navigation.NavigationView;

public class Nearby extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;
    private ImageView maps_nanjing;
    private ImageView maps_shanghai;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.drawer_view);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home: {
                        Intent go = new Intent(Nearby.this, MainActivity.class);
                        startActivity(go);
                        overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
                        break;
                    }
                    case R.id.contact: {
                        Intent i = new Intent();
                        i.setAction(Intent.ACTION_DIAL);
                        i.setData(Uri.parse("tel:" + 400123456));
                        startActivity(i);
                        break;
                    }
                    case R.id.email: {
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"petvillage@petvilg.ie"});
                        i.putExtra(Intent.EXTRA_SUBJECT, "From Ms./ Mr. / Mx.");
                        i.putExtra(Intent.EXTRA_TEXT, "To Pet Village Team: ");
                        i.setType("message/rfc822");
                        startActivity(Intent.createChooser(i, "Send Mail Using :"));
                        break;
                    }
                    case R.id.about: {
                        Intent goAbout = new Intent(Nearby.this, About.class);
                        startActivity(goAbout);
                        overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
                        break;
                    }

                    case R.id.login: {
                        Intent gologin = new Intent(Nearby.this, Login.class);
                        startActivity(gologin);
                        overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
                        break;
                    }

                    case R.id.logout: {
                        Intent gologout = new Intent(Nearby.this, SignHomeActivity.class);
                        startActivity(gologout);
                        overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
                        break;
                    }
                }
                return false;
            }
        });

        maps_nanjing = findViewById(R.id.maps_nanjing);
        maps_shanghai = findViewById(R.id.maps_shanghai);

        maps_nanjing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Nearby.this, Maps_Dublin.class);
                startActivity(i);
                overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
            }
        });

        maps_shanghai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Nearby.this, Maps_Galway.class);
                startActivity(i);
                overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
            }
        });

    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.transition.slide_in_left, R.transition.slide_out_right);// transition
    }
}