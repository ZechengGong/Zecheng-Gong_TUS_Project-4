package com.example.petvillage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.petvillage.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;

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
        setContentView(R.layout.activity_main);
        replaceFragment(new HomeFragment());

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bottomNavigationView.setBackground(null);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            switch (item.getItemId()) {

                case R.id.home:
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.service:
                    replaceFragment(new ServiceFragment());
                    break;
                case R.id.moments:
                    replaceFragment(new MomentsFragment());
                    break;
            }
            return true;
        });

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
                        Toast.makeText(MainActivity.this, "You are already on the Home Page", Toast.LENGTH_SHORT).show();
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
                        Intent goAbout = new Intent(MainActivity.this, About.class);
                        startActivity(goAbout);
                        overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
                        break;
                    }

                    case R.id.login: {
                        Intent gologin = new Intent(MainActivity.this, Login.class);
                        startActivity(gologin);
                        overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
                        break;
                    }

                    case R.id.logout: {
                        Intent gologout = new Intent(MainActivity.this, SignHomeActivity.class);
                        startActivity(gologout);
                        overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
                        break;
                    }
                }
                return false;
            }
        });
    }

    private void replaceFragment(Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();

    }
}