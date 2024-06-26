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
import android.widget.Toast;

import com.example.petvillage.Main.MainActivity;
import com.example.petvillage.R;
import com.google.android.material.navigation.NavigationView;

public class About extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;

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
        setContentView(R.layout.activity_about);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.drawer_view);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Options
                switch (item.getItemId()) {
                    case R.id.home: {
                        Intent go = new Intent(About.this, MainActivity.class);
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
                        i.putExtra(Intent.EXTRA_SUBJECT, "From");
                        i.putExtra(Intent.EXTRA_TEXT, "To Pet Village Team: ");
                        i.setType("message/rfc822");
                        startActivity(Intent.createChooser(i, "Send Mail Using :"));
                        break;
                    }
                    case R.id.about: {
                        Toast.makeText(About.this, "You are already on the About Page", Toast.LENGTH_SHORT).show();
                        break;
                    }

                    case R.id.login: {
                        Intent gologin = new Intent(About.this, MainActivity.class);
                        startActivity(gologin);
                        overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
                        Toast.makeText(About.this, "Please return to the homepage and try to log in again.", Toast.LENGTH_SHORT).show();
                        break;
                    }

                    case R.id.logout: {
                        Intent gologout = new Intent(About.this, MainActivity.class);
                        startActivity(gologout);
                        overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
                        Toast.makeText(About.this, "Please return to the homepage and try to log out again.", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
                return false;
            }
        });
    }

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