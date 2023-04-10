package com.example.planatrip;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FuelCalculatorFragment extends Fragment {

    private EditText distanceEditText;
    private EditText fuelEfficiencyEditText;
    private Button calculateButton;
    private TextView resultTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fuel_calculator, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        distanceEditText = view.findViewById(R.id.distance_edit_text);
        fuelEfficiencyEditText = view.findViewById(R.id.fuel_efficiency_edit_text);
        calculateButton = view.findViewById(R.id.calculate_button);
        resultTextView = view.findViewById(R.id.result_text_view);

        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculateFuel();
            }
        });
    }

    private void calculateFuel() {
        double distance = Double.parseDouble(distanceEditText.getText().toString());
        double fuelEfficiency = Double.parseDouble(fuelEfficiencyEditText.getText().toString());
        double fuelNeeded = (distance / 100) * fuelEfficiency;

        resultTextView.setText("Fuel needed: " + String.format("%.2f", fuelNeeded) + " liters");
    }
}