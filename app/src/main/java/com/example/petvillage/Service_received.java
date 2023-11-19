package com.example.petvillage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Arrays;

public class Service_received extends AppCompatActivity {

    private ListView list_service_received;
    private SharedPreferences database;
    private ArrayList<String> ls_received_text = null;
    private String saved_text = "";
    //private Button button;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(drawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_received);

        list_service_received = findViewById(R.id.list_service_received);
        //button = findViewById(R.id.button);

        try {
            Bundle extras = getIntent().getExtras();
            String receivedText = extras.getString("ItemsToSend");
            ls_received_text = converToArrayList(receivedText);

        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        ArrayAdapter<String> receivedAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                ls_received_text
        );
        list_service_received.setAdapter(receivedAdapter);

//        database = getApplicationContext().getSharedPreferences("service_data_store", MODE_PRIVATE);
//        String getSavedData = database.getString("key_saved_text", null);
//        ls_received_text = converToArrayList(getSavedData);
//        receivedAdapter.notifyDataSetChanged();

        list_service_received.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                ls_received_text.remove(i);
                receivedAdapter.notifyDataSetChanged();

//                saved_text = ls_received_text.toString();
                saved_text = converToString(ls_received_text);
                Toast.makeText(Service_received.this, saved_text, Toast.LENGTH_SHORT).show();

                database = getApplicationContext().getSharedPreferences("service_data_store",MODE_PRIVATE);
                SharedPreferences.Editor editor = database.edit();
                editor.putString("key_saved_text", saved_text);
                editor.commit();

                return true;
            }
        });

//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                database = getApplicationContext().getSharedPreferences("service_data_store", MODE_PRIVATE);
//                String getSavedData = database.getString("key_saved_text", null);
//                Toast.makeText(Service_received.this, getSavedData, Toast.LENGTH_SHORT).show();
////                        ls_received_text = converToArrayList(getSavedData);
////                        receivedAdapter.notifyDataSetChanged();
//            }
//        });
// /////////////////////////////////////////////////////////////////////////////////
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.drawer_view);
        drawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.home:
                    {
                        Intent go = new Intent(Service_received.this, MainActivity.class);
                        startActivity(go);
                        overridePendingTransition(R.transition.slide_in_right,R.transition.slide_out_left);
                        break;
                    }
                    case R.id.contact:
                    {
                        Intent i = new Intent();
                        i.setAction(Intent.ACTION_DIAL);
                        i.setData(Uri.parse("tel:" + 400123456));
                        startActivity(i);
                        break;
                    }
                    case R.id.email:
                    {
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"petvillage@petvilg.ie"});
                        i.putExtra(Intent.EXTRA_SUBJECT, "From Ms./ Mr. / Mx.");
                        i.putExtra(Intent.EXTRA_TEXT, "To Pet Village Team: ");
                        i.setType("message/rfc822");
                        startActivity(Intent.createChooser(i, "Send Mail Using :"));
                        break;
                    }
                    case R.id.about:
                    {
                        Intent goAbout = new Intent(Service_received.this, About.class);
                        startActivity(goAbout);
                        overridePendingTransition(R.transition.slide_in_right,R.transition.slide_out_left);
                        break;
                    }

                    case R.id.login:
                    {
                        Intent gologin = new Intent(Service_received.this, Login.class);
                        startActivity(gologin);
                        overridePendingTransition(R.transition.slide_in_right,R.transition.slide_out_left);
                        break;
                    }

                    case R.id.logout:
                    {
                        Intent gologout = new Intent(Service_received.this, SignHomeActivity.class);
                        startActivity(gologout);
                        overridePendingTransition(R.transition.slide_in_right,R.transition.slide_out_left);
                        break;
                    }
                }
                return false;
            }
        });
    }
        public ArrayList<String> converToArrayList(String str)
        {

            String[] strSplit = str.split(",");

            ArrayList<String> strList = new ArrayList<String>(
                    Arrays.asList(strSplit));

            return strList;
        }

        public String converToString(ArrayList<String> arr)
        {
            StringBuffer sb = new StringBuffer();
            for (String s : arr) {
                        sb.append(s);
                        sb.append(",");
            }
            String str = sb.toString();
            str = str.substring(0, str.length() -1);
            return str;
        }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

        @Override
        public void finish() {
            super.finish();
            overridePendingTransition(R.transition.slide_in_left,R.transition.slide_out_right);// transition
    }
}
