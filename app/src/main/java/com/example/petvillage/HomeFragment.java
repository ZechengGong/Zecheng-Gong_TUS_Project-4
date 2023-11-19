package com.example.petvillage;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements IOnBackPressed {

    private ImageSlider imageSlider;
    private CardView card_dogs;
    private CardView card_nearby;
    private CardView card_cats;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View Home = inflater.inflate(R.layout.fragment_home, container, false);

        //button = getView().findViewById(R.id.button);
        card_dogs = Home.findViewById(R.id.card_dogs);
        card_cats = Home.findViewById(R.id.card_cats);
        card_nearby = Home.findViewById(R.id.card_nearby);

//        card_dogs.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Intent i = new Intent(getActivity(), DOGS.class);
//                startActivity(i);
//                getActivity().overridePendingTransition(R.transition.slide_in_right,R.transition.slide_out_left);
//
//            }
//        });
        card_dogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {showDialog();
            }
        });

        card_nearby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getActivity(), Nearby.class);
                startActivity(i);
                getActivity().overridePendingTransition(R.transition.slide_in_right,R.transition.slide_out_left);

            }
        });

        card_cats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {showDialog();
            }
        });
//////////////////////////////////////////////////////////////////////////////////////////////

        drawerLayout = Home.findViewById(R.id.drawer_layout);
        navigationView = Home.findViewById(R.id.drawer_view);
        drawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, R.string.open, R.string.close);

        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home: {
                        Toast.makeText(getActivity(), "home", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
                return false;
            }
        });
        return Home;
    }

        @Override
        public boolean onBackPressed() {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                return false;
            } else {
                return true;
            }
        }

        private void showDialog(){
            AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
            builder.setIcon(R.drawable.cat);
            builder.setTitle("Oops!");
            builder.setMessage("Sorry, SERVER MAINTENANCE,\nEstimated time: more than 24 hours.\n\nIf you have any questions, you can contact us through the contact information in the DRAWER BAR MENU.");
            builder.setPositiveButton("Done",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {

                        }
                    });
            AlertDialog dialog=builder.create();
            dialog.show();
        }

//    @Override
//    public void onBackPressed() {
//        if(drawerLayout.isDrawerOpen(GravityCompat.START))
//        {
//            drawerLayout.closeDrawer(GravityCompat.START);
//        }
//        else {
//            super.onBackPressed();
//        }
//    }
}