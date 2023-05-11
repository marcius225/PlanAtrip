package com.example.planatrip.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.planatrip.ListAdapter;
import com.example.planatrip.ListData;
import com.example.planatrip.R;

import java.util.ArrayList;

public class GuideFragment extends Fragment {

    private ListView listView;
    private ListAdapter listAdapter;
    private ArrayList<ListData> dataArrayList = new ArrayList<>();
    private ListData listData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_guide, container, false);

        listView = rootView.findViewById(R.id.listview);

        //int[] imageList = {R.drawable.pasta, R.drawable.maggi, R.drawable.cake, R.drawable.pancake, R.drawable.pizza, R.drawable.burger, R.drawable.fries};
        //int[] ingredientList = {R.string.maggiIngredients,R.string.cakeIngredients,R.string.pancakeIngredients,R.string.pizzaIngredients};
        int[] descList = {R.string.VilniusDesc, R.string.WarsawDesc, R.string.BerlinDesc,R.string.PragueDesc,R.string.StockholmDesc};
        String[] nameList = {"Vilnius", "Warsaw", "Berlin", "Prague", "Stockholm"};
        String[] timeList = {"2 mins", "2 mins", "3 mins","2 mins", "2 mins"};
        int[] imageList = {R.drawable.vilnius,R.drawable.vilnius,R.drawable.vilnius,R.drawable.vilnius,R.drawable.vilnius};

        for (int i = 0; i < nameList.length; i++){
            listData = new ListData(nameList[i], timeList[i], descList[i], imageList[i]);
            dataArrayList.add(listData);
        }
        listAdapter = new ListAdapter(getActivity(), dataArrayList);
        listView.setAdapter(listAdapter);
        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle args = new Bundle();
                args.putString("name", nameList[i]);
                args.putString("time", timeList[i]);
                args.putInt("image", imageList[i]);
                args.putInt("desc", descList[i]);

                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                DetailedGuideFragment detailedGuideFragment = new DetailedGuideFragment();
                detailedGuideFragment.setArguments(args);

                fragmentTransaction.replace(R.id.framgent_container, detailedGuideFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        return rootView;
    }
}