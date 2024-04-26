package com.example.petvillage.Fragments;

import static androidx.core.content.ContextCompat.getSystemService;

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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.BaseColumns;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.example.petvillage.Others.IOnBackPressed;
import com.example.petvillage.Others.Nearby;
import com.example.petvillage.R;
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
    private ImageView notificationBtu;

    private static final String[] SUGGESTIONS = {"Service&Shopping", "Moments","Post","Open Drawer","Call","Email","Contact"};

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

    private void populateSuggestions(String[] suggestions, CursorAdapter adapter) {
        MatrixCursor cursor = new MatrixCursor(new String[] {BaseColumns._ID, "suggestion"});
        for (int i = 0; i < suggestions.length; i++) {
            cursor.addRow(new Object[] {i, suggestions[i]});
        }
        adapter.changeCursor(cursor);
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
        notificationBtu = Home.findViewById(R.id.notificationBtu);

        ImageCarousel imageCarousel = Home.findViewById(R.id.carousel);
        imageCarousel.registerLifecycle(getLifecycle());
        imageCarousel.setAutoPlay(true);

        List< CarouselItem> list = new ArrayList<>();
        list.add(new CarouselItem("https://images.unsplash.com/photo-1706512990173-91e7797cb3d0?q=80&w=2669&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D","Welcome to Pet Village!"));
        list.add(new CarouselItem("https://images.unsplash.com/photo-1587300003388-59208cc962cb?q=80&w=2670&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D","Try to POST your first post!"));
        list.add(new CarouselItem("https://images.unsplash.com/photo-1554692918-08fa0fdc9db3?q=80&w=2670&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D","Online store will be opened soon..."));
        // list.add(new CarouselItem(R.drawable.cat,"Hi"));

        imageCarousel.setData(list);
//------------------------------------------------------------------------------------------------------
        card_dogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogDOG();
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
                showDialogCAT();
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


        SearchView searchView = Home.findViewById(R.id.searchView); // 确保您有一个名为searchView的SearchView在您的fragment_home.xml布局中
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);


        MatrixCursor cursor = new MatrixCursor(new String[] {BaseColumns._ID, "suggestion"});
        for (int i = 0; i < SUGGESTIONS.length; i++) {
            cursor.addRow(new Object[] {i, SUGGESTIONS[i]});
        }

        CursorAdapter suggestionAdapter = new SimpleCursorAdapter(
                getActivity(),
                android.R.layout.simple_list_item_1,
                cursor,
                new String[] {"suggestion"},
                new int[] {android.R.id.text1},
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        );
        searchView.setSuggestionsAdapter(suggestionAdapter);


        // 设置搜索框的监听器
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();

                if ("Service&Shopping".equalsIgnoreCase(query)) {
                    Fragment serviceFragment = new ServiceFragment(); // Replace with your actual fragment class
                    transaction.replace(R.id.frame_layout, serviceFragment);
                    navigateToShoppingFragment();
                } else if ("Moments".equalsIgnoreCase(query)) {
                    Fragment momentsFragment = new MomentsFragment(); // Replace with your actual fragment class
                    transaction.replace(R.id.frame_layout, momentsFragment);
                    navigateToMomentsFragment();
                } else if ("Post".equalsIgnoreCase(query)) {
                    Fragment postFragment = new Post(); // Replace with your actual fragment class
                    transaction.replace(R.id.frame_layout, postFragment);
                } else if ("Open Drawer".equalsIgnoreCase(query)|| "Call".equalsIgnoreCase(query)|| "Email".equalsIgnoreCase(query)|| "Contact".equalsIgnoreCase(query)) {
                    openDrawer();
                    return true;
                } else {
                    // If no special keyword is matched, indicate no action was taken
                    return false;
                }

                transaction.addToBackStack(null);
                transaction.commit();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // 始终显示建议，即使 newText 为空，也显示所有建议
                if (newText.isEmpty()) {
                    populateSuggestions(SUGGESTIONS, suggestionAdapter);
                } else {
                    // 根据输入 newText 过滤建议
                    List<String> filteredSuggestions = new ArrayList<>();
                    for (String suggestion : SUGGESTIONS) {
                        if (suggestion.toLowerCase().contains(newText.toLowerCase())) { // 改为 contains 实现模糊搜索
                            filteredSuggestions.add(suggestion);
                        }
                    }
                    populateSuggestions(filteredSuggestions.toArray(new String[0]), suggestionAdapter);
                }
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
                    hideKeyboard(searchView);
                    searchView.setQuery("", false);
                }
                return true;
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

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.onActionViewExpanded(); // 手动扩展 SearchView
                populateSuggestions(SUGGESTIONS, suggestionAdapter); // 显示所有建议
            }
        });

        notificationBtu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogNOTIFY();
            }
        });
        return Home;
    }
    //End of Search

    @Override
    public boolean onBackPressed() {
        return !drawerLayout.isDrawerOpen(GravityCompat.START);
    }

    private void showDialogCAT() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setIcon(R.drawable.cat);
        builder.setTitle("Oops!");
        builder.setMessage("Sorry, SERVER MAINTENANCE,\nEstimated time: more than 24 hours.\n\nIf you have any questions, please call or email us through the contact information in the NAVIGATION DRAWER.");
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

    private void showDialogDOG() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setIcon(R.drawable.dog);
        builder.setTitle("Oops!");
        builder.setMessage("Sorry, SERVER MAINTENANCE,\nEstimated time: more than 24 hours.\n\nIf you have any questions, please call or email us through the contact information in the NAVIGATION DRAWER.");
        builder.setPositiveButton("Done",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {

                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showDialogNOTIFY() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setIcon(R.drawable.bell);
        builder.setTitle("Notification");
        builder.setMessage("Notifications feature coming soon...");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void navigateToShoppingFragment() {
        if (isAdded() && getActivity() != null) {
            // 设置底部导航栏的当前选中项
            BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
            bottomNavigationView.setSelectedItemId(R.id.shopping); // 替换为导航到 MomentsFragment 对应的菜单项ID
        }
    }
    private void navigateToMomentsFragment() {
        if (isAdded() && getActivity() != null) {
            // 设置底部导航栏的当前选中项
            BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
            bottomNavigationView.setSelectedItemId(R.id.moments); // 替换为导航到 MomentsFragment 对应的菜单项ID
        }
    }

    private void openDrawer() {
        if (drawerLayout != null) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}