package com.example.petvillage.Others;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.petvillage.R;

import java.util.ArrayList;

// Drawer bar header
public class Header extends AppCompatActivity {
    private ListView list_service_received;
    // private SharedPreferences database;
    private ArrayList<String> ls_received_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_header);
    }
}


