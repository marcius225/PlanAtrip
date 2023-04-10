package com.example.planatrip;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestFragment extends Fragment {

    private static String TAG = "TestFragment";

    private EditText mLatitudeEditText;
    private EditText mLongitudeEditText;
    private Button mSaveButton;
    private Button deleteButton;
    private CheckBox disableFrom;
    private CheckBox checkBoxHotel;
    private CheckBox checkBoxRestaurant;
    private CheckBox checkBoxSights;
    private CheckBox checkBoxShop;
    private CheckBox checkBoxFuel;

    private FusedLocationProviderClient fusedLocationClient;

    private MyDatabaseHelper mDbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_test,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        mLatitudeEditText = view.findViewById(R.id.editText1);
        mLongitudeEditText = view.findViewById(R.id.editText2);
        mSaveButton = view.findViewById(R.id.myButton);
        deleteButton = view.findViewById(R.id.myButton2);
        disableFrom = view.findViewById(R.id.checkBoxLocation);
        checkBoxHotel = view.findViewById(R.id.checkBoxHotel);
        checkBoxRestaurant = view.findViewById(R.id.checkBoxRestaurants);
        checkBoxSights = view.findViewById(R.id.checkBoxSights);
        checkBoxShop = view.findViewById(R.id.checkBoxShop);
        checkBoxFuel = view.findViewById(R.id.checkBoxFuel);

        mSaveButton.setEnabled(false);

        mDbHelper = new MyDatabaseHelper(getActivity());

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: sitas du kartai mSaveButton?");
                // Save the entered coordinates to the database
                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                //eoLocate2(mLatitudeEditText.getText().toString());

                if (checkBoxHotel.isChecked()) {
                    // Get the text value of the checked checkbox and add it to the ContentValues object
                    String searchString = "lodging";
                    values.put(MyDatabaseHelper.COLUMN_SEARCH_STRING, searchString);
                }
                else if (checkBoxRestaurant.isChecked()) {
                    // Get the text value of the checked checkbox and add it to the ContentValues object
                    String searchString = "restaurant";
                    values.put(MyDatabaseHelper.COLUMN_SEARCH_STRING, searchString);
                }
                else if (checkBoxSights.isChecked()) {
                    // Get the text value of the checked checkbox and add it to the ContentValues object
                    String searchString = "tourist_attraction"; //need different
                    values.put(MyDatabaseHelper.COLUMN_SEARCH_STRING, searchString);
                }
                else if (checkBoxShop.isChecked()) {
                    // Get the text value of the checked checkbox and add it to the ContentValues object
                    String searchString = "supermarket"; //need different store gal?
                    values.put(MyDatabaseHelper.COLUMN_SEARCH_STRING, searchString);
                }
                else if (checkBoxFuel.isChecked()) {
                    Log.d(TAG, "onClick: sitas du kartai checkBoxFuel?");
                    // Get the text value of the checked checkbox and add it to the ContentValues object
                    String searchString = "gas_station"; //need different
                    values.put(MyDatabaseHelper.COLUMN_SEARCH_STRING, searchString);
                }

                if(disableFrom.isChecked()) {
                    Log.d(TAG, "onClick: sitas du kartai disableFrom?");
                    getUserLocation2(new TestFragment.LocationCallback() {
                        @Override
                        public void onNewLocationAvailable(double[] location) {
                            Log.d(TAG, "onNewLocationAvailable: du kartai ane");
                            // Use location array here
                            double latitudeFrom = location[0];
                            double longitudeFrom = location[1];

                            Log.d(TAG, "Latitude is array checked: " + latitudeFrom);
                            Log.d(TAG, "Longitude is array checked: " + longitudeFrom);
                            values.put(MyDatabaseHelper.COLUMN_LATITUDE_FROM, latitudeFrom);
                            values.put(MyDatabaseHelper.COLUMN_LONGITUDE_FROM, longitudeFrom);

                            double[] locationArrayTo = geoLocate2(mLongitudeEditText.getText().toString());
                            double latitudeTo = locationArrayTo[0];
                            double longitudeTo = locationArrayTo[1];
                            String tripTo = mLongitudeEditText.getText().toString();
                            Log.d(TAG, "onNewLocationAvailable: ar gaunam stringa" + tripTo);

                            values.put(MyDatabaseHelper.COLUMN_LATITUDE_TO, latitudeTo);
                            values.put(MyDatabaseHelper.COLUMN_LONGITUDE_TO, longitudeTo);

                            values.put(MyDatabaseHelper.COLUMN_NAMEOFTRIP_STRING,tripTo);

                            long newRowId = db.insert(MyDatabaseHelper.TABLE_NAME, null, values);

                            ContentValues contentValues = new ContentValues();
                            contentValues.put("currenttrip_string", tripTo);
                            db.update("CURRENT_TRIP",contentValues,"_id=?",new String[]{"0"});
                        }
                    });
                }
                else{
                    double[] locationArrayFrom = geoLocate2(mLatitudeEditText.getText().toString());
                    double latitudeFrom = locationArrayFrom[0];
                    double longitudeFrom = locationArrayFrom[1];

                    values.put(MyDatabaseHelper.COLUMN_LATITUDE_FROM, latitudeFrom);
                    values.put(MyDatabaseHelper.COLUMN_LONGITUDE_FROM, longitudeFrom);

                    double[] locationArrayTo = geoLocate2(mLongitudeEditText.getText().toString());
                    double latitudeTo = locationArrayTo[0];
                    double longitudeTo = locationArrayTo[1];
                    String tripTo = mLongitudeEditText.getText().toString();

                    Log.d(TAG, "onNewLocationAvailable: ar gaunam stringa" + tripTo);

                    values.put(MyDatabaseHelper.COLUMN_LATITUDE_TO, latitudeTo);
                    values.put(MyDatabaseHelper.COLUMN_LONGITUDE_TO, longitudeTo);

                    values.put(MyDatabaseHelper.COLUMN_NAMEOFTRIP_STRING,tripTo);

                    long newRowId = db.insert(MyDatabaseHelper.TABLE_NAME, null, values);

                    ContentValues contentValues = new ContentValues();
                    contentValues.put("currenttrip_string", tripTo);
                    db.update("CURRENT_TRIP",contentValues,"_id=?",new String[]{"0"});
                }

/*                double[] locationArrayFrom = geoLocate2(mLatitudeEditText.getText().toString());
                double latitudeFrom = locationArrayFrom[0];
                double longitudeFrom = locationArrayFrom[1];*/

/*                double[] locationArrayTo = geoLocate2(mLongitudeEditText.getText().toString());
                double latitudeTo = locationArrayTo[0];
                double longitudeTo = locationArrayTo[1];

*//*                values.put(MyDatabaseHelper.COLUMN_LATITUDE_FROM, latitudeFrom);
                values.put(MyDatabaseHelper.COLUMN_LONGITUDE_FROM, longitudeFrom);*//*

                values.put(MyDatabaseHelper.COLUMN_LATITUDE_TO, latitudeTo);
                values.put(MyDatabaseHelper.COLUMN_LONGITUDE_TO, longitudeTo);

*//*                values.put(MyDatabaseHelper.COLUMN_LATITUDE, Double.parseDouble(mLatitudeEditText.getText().toString()));
                values.put(MyDatabaseHelper.COLUMN_LONGITUDE, Double.parseDouble(mLongitudeEditText.getText().toString()));*//*
                long newRowId = db.insert(MyDatabaseHelper.TABLE_NAME, null, values);*/
            }
        });

        // Set up TextWatchers for latitude and longitude fields
        mLatitudeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateSaveButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing
            }
        });

        mLongitudeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateSaveButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                db.delete(MyDatabaseHelper.TABLE_NAME, null, null);
                db.close();
                // Show a toast message to indicate the deletion was successful
                Toast.makeText(getActivity(), "Old coordinates deleted", Toast.LENGTH_SHORT).show();
            }
        });

        disableFrom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateSaveButtonState();
                if (isChecked) {
                    mLatitudeEditText.setEnabled(false);
                    Log.d(TAG, "onCheckedChanged: Using current location");
                } else {
                    mLatitudeEditText.setEnabled(true);
                    Log.d(TAG, "onCheckedChanged: using custom location");
                }
            }
        });

        checkBoxHotel.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkBoxRestaurant.setEnabled(false);
                    checkBoxSights.setEnabled(false);
                    checkBoxShop.setEnabled(false);
                    checkBoxFuel.setEnabled(false);
                } else {
                    checkBoxRestaurant.setEnabled(true);
                    checkBoxSights.setEnabled(true);
                    checkBoxShop.setEnabled(true);
                    checkBoxFuel.setEnabled(true);
                }
            }
        });

        checkBoxRestaurant.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkBoxHotel.setEnabled(false);
                    checkBoxSights.setEnabled(false);
                    checkBoxShop.setEnabled(false);
                    checkBoxFuel.setEnabled(false);
                } else {
                    checkBoxHotel.setEnabled(true);
                    checkBoxSights.setEnabled(true);
                    checkBoxShop.setEnabled(true);
                    checkBoxFuel.setEnabled(true);
                }
            }
        });

        checkBoxSights.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkBoxHotel.setEnabled(false);
                    checkBoxRestaurant.setEnabled(false);
                    checkBoxShop.setEnabled(false);
                    checkBoxFuel.setEnabled(false);
                } else {
                    checkBoxHotel.setEnabled(true);
                    checkBoxRestaurant.setEnabled(true);
                    checkBoxShop.setEnabled(true);
                    checkBoxFuel.setEnabled(true);
                }
            }
        });

        checkBoxShop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkBoxHotel.setEnabled(false);
                    checkBoxRestaurant.setEnabled(false);
                    checkBoxSights.setEnabled(false);
                    checkBoxFuel.setEnabled(false);
                } else {
                    checkBoxHotel.setEnabled(true);
                    checkBoxRestaurant.setEnabled(true);
                    checkBoxSights.setEnabled(true);
                    checkBoxFuel.setEnabled(true);
                }
            }
        });

        checkBoxFuel.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkBoxHotel.setEnabled(false);
                    checkBoxRestaurant.setEnabled(false);
                    checkBoxSights.setEnabled(false);
                    checkBoxShop.setEnabled(false);
                } else {
                    checkBoxHotel.setEnabled(true);
                    checkBoxRestaurant.setEnabled(true);
                    checkBoxSights.setEnabled(true);
                    checkBoxShop.setEnabled(true);
                }
            }
        });

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


    private void updateSaveButtonState() {
        if (!TextUtils.isEmpty(mLatitudeEditText.getText()) && !TextUtils.isEmpty(mLongitudeEditText.getText()) && !disableFrom.isChecked()) {
            mSaveButton.setEnabled(true);
        }
        else if (TextUtils.isEmpty(mLatitudeEditText.getText()) && !TextUtils.isEmpty(mLongitudeEditText.getText()) && disableFrom.isChecked()) {
            mSaveButton.setEnabled(true);
        }
        else {
            mSaveButton.setEnabled(false);
        }

    }

    public interface LocationCallback {
        void onNewLocationAvailable(double[] location);
    }
    private void getUserLocation2(TestFragment.LocationCallback callback) {
        Log.d(TAG, "getUserLocation2: test");
        double[] locationArray = new double[2];
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();
                                Log.d(TAG, "onSuccess: Latitude"+latitude);
                                Log.d(TAG, "onSuccess: Longitude"+longitude);
                                locationArray[0] = latitude;
                                Log.d(TAG, "onSuccess: array0 "+locationArray[0]);
                                locationArray[1] = longitude;
                                Log.d(TAG, "onSuccess: array1 "+locationArray[1]);
                                callback.onNewLocationAvailable(locationArray);
                            }
                        }
                    })
                    .addOnFailureListener(getActivity(), new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "onFailure: fail",e );
                        }
                    });
        } else {
            Log.d(TAG, "getUserLocation: gaidysas");
        }
    }



}
