package com.example.petvillage;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.MatrixCursor;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.BaseColumns;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import android.database.Cursor;

import org.imaginativeworld.whynotimagecarousel.ImageCarousel;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;

import java.util.ArrayList;
import java.util.List;

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

    private static final String[] SUGGESTIONS = {"Service", "Moments", "Login", "About"};

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

        ImageCarousel imageCarousel = Home.findViewById(R.id.carousel);
        imageCarousel.registerLifecycle(getLifecycle());
        imageCarousel.setAutoPlay(true);

        List< CarouselItem> list = new ArrayList<>();
        list.add(new CarouselItem("https://images.unsplash.com/photo-1706430201079-7ca144fbe95d?q=80&w=2574&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D","Hi"));
        list.add(new CarouselItem("https://images.unsplash.com/photo-1682685796467-89a6f149f07a?q=80&w=2670&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDF8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"));
        list.add(new CarouselItem(R.drawable.cat,"Hi"));

        imageCarousel.setData(list);
//////////////////////////////////////////////////////////////////////////

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
            public void onClick(View view) {
                showDialog();
            }
        });

        card_nearby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getActivity(), Nearby.class);
                startActivity(i);
                getActivity().overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);

            }
        });

        card_cats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
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
                if (item.getItemId() == R.id.home) {
                    Toast.makeText(getActivity(), "home", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });






        //Add searchView

        SearchView searchView = Home.findViewById(R.id.searchView); // 确保您有一个名为searchView的SearchView在您的fragment_home.xml布局中
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);

        // 设置搜索框建议的 CursorAdapter
        final String[] from = new String[] {"suggestion"};
        final int[] to = new int[] {android.R.id.text1};

        MatrixCursor cursor = new MatrixCursor(new String[] {BaseColumns._ID, "suggestion"});
        for (int i = 0; i < SUGGESTIONS.length; i++) {
            cursor.addRow(new Object[] {i, SUGGESTIONS[i]});
        }

        CursorAdapter suggestionAdapter = new SimpleCursorAdapter(getActivity(),
                android.R.layout.simple_list_item_1, cursor, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        searchView.setSuggestionsAdapter(suggestionAdapter);



        // 设置搜索框的监听器
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if ("Service".equalsIgnoreCase(query)) {
                    Fragment serviceFragment = new ServiceFragment(); // Assuming ServiceFragment is a Fragment
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame_layout, serviceFragment); // 'container' is your FrameLayout or the id of the Fragment container
                    transaction.addToBackStack(null);
                    transaction.commit();
                    bottomNavigationView.setSelectedItemId(R.id.service); // 使用您 Service Fragment 对应的菜单项 ID
                    return true;
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Implement search suggestions logic here if needed
                return true;
            }
        });

        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Cursor cursor = (Cursor) suggestionAdapter.getItem(position);
                int columnIndex = cursor.getColumnIndex("suggestion");
                if (columnIndex >= 0) { // 确保列索引是有效的
                    String suggestion = cursor.getString(columnIndex);

                    // 设置搜索框内容并提交
                    searchView.setQuery(suggestion, true);
                    return true;
                }
                return false;
            }
        });


        // 设置根视图的触摸监听器
        Home.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Rect outRect = new Rect();
                    searchView.getGlobalVisibleRect(outRect);
                    if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                        searchView.clearFocus();

                        // 获取 InputMethodManager 实例
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                    }
                }
                return false;
            }
        });


        return Home;
    }


    @Override
    public boolean onBackPressed() {
        return !drawerLayout.isDrawerOpen(GravityCompat.START);
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setIcon(R.drawable.cat);
        builder.setTitle("Oops!");
        builder.setMessage("Sorry, SERVER MAINTENANCE,\nEstimated time: more than 24 hours.\n\nIf you have any questions, you can contact us through the contact information in the DRAWER BAR MENU.");
        builder.setPositiveButton("Done",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {

                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    //Add


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