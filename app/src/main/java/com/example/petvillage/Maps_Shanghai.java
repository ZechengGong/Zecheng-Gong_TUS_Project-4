package com.example.petvillage;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Maps_Shanghai extends AppCompatActivity
                       implements OnMapReadyCallback{

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_maps_shanghai);

            // Get the SupportMapFragment and request notification when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map_sh);
            mapFragment.getMapAsync(this);
        }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng shanghai = new LatLng(31.20897925639823, 121.49688018722419);
        googleMap.addMarker(new MarkerOptions()
                .position(shanghai)
                .title("Marker in Shanghai Store"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(shanghai));
        googleMap.setMinZoomPreference(11);
    }
}