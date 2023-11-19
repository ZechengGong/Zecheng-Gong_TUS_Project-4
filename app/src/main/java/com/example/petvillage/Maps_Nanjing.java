package com.example.petvillage;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Maps_Nanjing extends AppCompatActivity
            implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps_nanjing);

        // Get the SupportMapFragment and request notification when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
        @Override
        public void onMapReady(GoogleMap googleMap) {
            LatLng nanjing = new LatLng(32.026332730000625, 118.80490457347275);
            googleMap.addMarker(new MarkerOptions()
                    .position(nanjing)
                    .title("Marker in Nanjing Head Store"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(nanjing));
            googleMap.setMinZoomPreference(11);
        }
    }
