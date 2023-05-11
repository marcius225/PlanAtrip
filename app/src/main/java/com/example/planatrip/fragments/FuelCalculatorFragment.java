package com.example.planatrip.fragments;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.planatrip.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FuelCalculatorFragment extends Fragment {

    private EditText to;
    private EditText from;
    private EditText fuelEfficiency_text;
    private EditText costas;
    private Button calculateButton;
    private TextView resultTextView;
    private static String TAG = "FuelFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fuel_calculator, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        from = view.findViewById(R.id.From_fuel);
        to = view.findViewById(R.id.To_fuel);
        fuelEfficiency_text = view.findViewById(R.id.efficency);
        costas = view.findViewById(R.id.cost);
        calculateButton = view.findViewById(R.id.calc_button);
        resultTextView = view.findViewById(R.id.result_text_view);

        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculateFuel();
            }
        });
    }

    //double distanceInKm = SphericalUtil.computeDistanceBetween(origin, destination) / 1000;

    private void calculateFuel() {
/*        double distance = Double.parseDouble(distanceEditText.getText().toString());

        double fuelNeeded = (distance / 100) * fuelEfficiency;*/

        double fuelEfficiency = Double.parseDouble(fuelEfficiency_text.getText().toString());
        double fuelPricePerLiter = Double.parseDouble(costas.getText().toString());

        double[] locationArrayFrom = geoLocate2(from.getText().toString());
        double latitudeFrom = locationArrayFrom[0];
        double longitudeFrom = locationArrayFrom[1];

        double[] locationArrayTo = geoLocate2(to.getText().toString());
        double latitudeTo = locationArrayTo[0];
        double longitudeTo = locationArrayTo[1];

        LatLng origin = new LatLng(latitudeFrom, longitudeFrom);
        LatLng destination = new LatLng(latitudeTo, longitudeTo);

        double distanceInKm = SphericalUtil.computeDistanceBetween(origin, destination) / 1000;

        double fuelNeeded = (distanceInKm / 100) * fuelEfficiency;

        double fuelCostPerLiter = fuelPricePerLiter * fuelNeeded;

        resultTextView.setText(getString(R.string.Fuel_needed) + String.format("%.2f", fuelNeeded) + " l\n"+getString(R.string.Fuel_cost_total) + String.format("%.2f", fuelCostPerLiter) + " €");
    }


    private double[] geoLocate2(String searchString) {
        double[] locationArray = new double[2];
        Log.d(TAG, "geoLocate2: geolocation");

        //String searchString = mLatitudeEditText.getText().toString();

        Geocoder geocoder = new Geocoder(getContext());

        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString, 1);

        } catch (IOException e) {
            Log.e(TAG, "geoLocate: IOException" + e.getMessage());
        }

        if (list.size() > 0) {
            Address address = list.get(0);

            Log.d(TAG, "geoLocate2: found " + address.toString());

            double latitude = address.getLatitude();
            double longitude = address.getLongitude();

            locationArray[0] = latitude;
            locationArray[1] = longitude;

            Log.d(TAG, "geoLocate2: latitude " + latitude); //gaunam rastos lokacijos lat
            Log.d(TAG, "geoLocate2: longitude " + longitude); //gaunam rastos lokacicjos lgt
        }

        return locationArray;
    }
}