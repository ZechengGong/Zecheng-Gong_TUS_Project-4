package com.example.petvillage;

import static android.content.Context.MODE_PRIVATE;
import static android.media.CamcorderProfile.get;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ServiceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ServiceFragment extends Fragment {

    private ListView list_service;
    private ArrayList<String> ls_service_text;
    private ArrayList<String> ls_service_fee;
    private ArrayList<Integer> ls_service_images;
    private String msg = "";
    private FloatingActionButton floating_submit;
    private SharedPreferences database;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ServiceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ServiceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ServiceFragment newInstance(String param1, String param2) {
        ServiceFragment fragment = new ServiceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        View Service = inflater.inflate(R.layout.fragment_service, container, false);

        list_service = Service.findViewById(R.id.list_service);
        floating_submit = Service.findViewById(R.id.floating_submit);

        ls_service_text = new ArrayList<>();
        ls_service_text.add("Cleaning Service");
        ls_service_text.add("Shearing Service");
        ls_service_text.add("Pet Checkup");
        ls_service_text.add("Pet Vaccines");
        ls_service_text.add("Pet Sterilization");

        ls_service_fee = new ArrayList<>();
        ls_service_fee.add("€15 Once");
        ls_service_fee.add("€19 Once");
        ls_service_fee.add("Starting at\n€89");
        ls_service_fee.add("Starting at\n€129");
        ls_service_fee.add("Starting at\n€699");

        ls_service_images = new ArrayList<>();
        ls_service_images.add(R.drawable.bathing_dog);
        ls_service_images.add(R.drawable.dog_haircut);
        ls_service_images.add(R.drawable.medical_checkup);
        ls_service_images.add(R.drawable.pet_vaccines);
        ls_service_images.add(R.drawable.sterilization);

        MyAdapter adapter = new MyAdapter();
        list_service.setAdapter(adapter);

//////////////////////////////////////////////////////////////////////////////////////////////
        list_service.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                String myText = ls_service_text.get(i);
                msg = msg + myText + "," + "\n";
                Toast.makeText(getActivity(), ls_service_text.get(i) + "is added to list", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        list_service.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getActivity(), "Please LONG Press the item to select", Toast.LENGTH_SHORT).show();
            }
        });

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        floating_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent send = new Intent(getActivity(), Service_received.class);

                send.putExtra("ItemsToSend", msg + "\n");
                startActivity(send);
                msg = "";
                getActivity().overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
            }
        });
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        return Service;
    }

    public class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return ls_service_images.size();  //added this
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            view = getLayoutInflater().inflate(R.layout.list_service_cards, viewGroup, false);
            return view;
        }
    }
}