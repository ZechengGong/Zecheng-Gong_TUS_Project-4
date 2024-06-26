package com.example.petvillage.Google;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.petvillage.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Maps_Dublin extends AppCompatActivity
        implements OnMapReadyCallback {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps_dublin);

        // Get the SupportMapFragment and request notification when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng dublin = new LatLng(53.35601034056712, -6.329813302011234);
        googleMap.addMarker(new MarkerOptions()
                .position(dublin)
                .title("Marker in Dublin Store"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(dublin));
        googleMap.setMinZoomPreference(11);
    }
}
