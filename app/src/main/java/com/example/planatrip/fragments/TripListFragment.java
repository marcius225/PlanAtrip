package com.example.planatrip.fragments;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.planatrip.MyAdapter;
import com.example.planatrip.MyDatabaseHelper;
import com.example.planatrip.CurrentTripObject;
import com.example.planatrip.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class TripListFragment extends Fragment {

    private MyDatabaseHelper mDbHelper;
    private TestMapFragment mapFragment = new TestMapFragment();

    private static String TAG = "TripListFragment";



    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.triplist_fragment, container, false);
        mDbHelper = new MyDatabaseHelper(getActivity());
        return view;
    }


    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        MyAdapter adapter = new MyAdapter(getContext(), getDataFromDatabase());
        recyclerView.setAdapter(adapter);

/*        adapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(MyObject myObject) {
                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                // Handle the selected item
                String stringas = myObject.getNameOfTrip();
                ContentValues contentValues = new ContentValues();
                contentValues.put("currenttrip_string", stringas);
                //Toast.makeText(getActivity(), "Selected item: " + myObject.getNameOfTrip(), Toast.LENGTH_SHORT).show();
                db.update("CURRENT_TRIP",contentValues,"_id=?",new String[]{"0"});

            }
        });*/

        adapter.setOnItemLongClickListener(new MyAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(CurrentTripObject myObject, View view) {
                PopupMenu popupMenu = new PopupMenu(getContext(), view);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
                //popupMenu.getMenu().add("Edit");

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        Integer tripID = myObject.getTripID();
                        switch (menuItem.getItemId()) {
                            case R.id.action_select:
                                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                                // Handle the selected item
                                //String stringas = myObject.getNameOfTrip();
                                //Integer tripID = myObject.getTripID();
                                ContentValues contentValues = new ContentValues();
                                contentValues.put("currenttrip_id", tripID);
                                //Toast.makeText(getActivity(), "Selected item: " + myObject.getNameOfTrip(), Toast.LENGTH_SHORT).show();
                                db.update("CURRENT_TRIP",contentValues,"_id=?",new String[]{"0"});
                                String toast = getString(R.string.Selected_Trip);
                                Toast.makeText(getActivity(), toast, Toast.LENGTH_SHORT).show();
                                //Toast.makeText(getActivity(), "A trip has been selected", Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.action_delete:
                                //String stringas2 = myObject.getNameOfTrip();
                                //Integer tripID = myObject.getTripID();
                                SQLiteDatabase db2 = mDbHelper.getWritableDatabase();
                                db2.execSQL("DELETE FROM " + MyDatabaseHelper.TABLE_NAME + " WHERE " + MyDatabaseHelper.COLUMN_ID + "='" + tripID + "'");
                                db2.close();
                                String toast2 = getString(R.string.Deleted_Trip);
                                Toast.makeText(getActivity(), toast2, Toast.LENGTH_SHORT).show();
                                //Toast.makeText(getActivity(), "A trip has been deleted", Toast.LENGTH_SHORT).show();
                                refreshFragment();
                                return true;
                            case R.id.action_edit:
                                showEditDialog(myObject);
                                refreshFragment();
                                return true;
                            default:
                                return false;
                        }
                    }
                });

                popupMenu.show();
            }
        });
    }

    private void showEditDialog(CurrentTripObject myObject) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.edit_popup, null);
        builder.setView(view);

        EditText editOrigin = view.findViewById(R.id.edit_origin);
        EditText editDestination = view.findViewById(R.id.edit_destination);
        CheckBox restaurant_edit = view.findViewById(R.id.restaurant_edit);
        CheckBox fuel_edit = view.findViewById(R.id.fuel_edit);
        CheckBox shopping_edit = view.findViewById(R.id.shopping_edit);
        CheckBox sights_edit = view.findViewById(R.id.sights_edit);
        CheckBox hotel_edit = view.findViewById(R.id.hotel_edit);

        hotel_edit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    restaurant_edit.setEnabled(false);
                    sights_edit.setEnabled(false);
                    shopping_edit.setEnabled(false);
                    fuel_edit.setEnabled(false);
                } else {
                    restaurant_edit.setEnabled(true);
                    sights_edit.setEnabled(true);
                    shopping_edit.setEnabled(true);
                    fuel_edit.setEnabled(true);
                }
            }
        });

        restaurant_edit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    hotel_edit.setEnabled(false);
                    sights_edit.setEnabled(false);
                    shopping_edit.setEnabled(false);
                    fuel_edit.setEnabled(false);
                } else {
                    hotel_edit.setEnabled(true);
                    sights_edit.setEnabled(true);
                    shopping_edit.setEnabled(true);
                    fuel_edit.setEnabled(true);
                }
            }
        });

        sights_edit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    hotel_edit.setEnabled(false);
                    restaurant_edit.setEnabled(false);
                    shopping_edit.setEnabled(false);
                    fuel_edit.setEnabled(false);
                } else {
                    hotel_edit.setEnabled(true);
                    restaurant_edit.setEnabled(true);
                    shopping_edit.setEnabled(true);
                    fuel_edit.setEnabled(true);
                }
            }
        });

        shopping_edit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    hotel_edit.setEnabled(false);
                    restaurant_edit.setEnabled(false);
                    sights_edit.setEnabled(false);
                    fuel_edit.setEnabled(false);
                } else {
                    hotel_edit.setEnabled(true);
                    restaurant_edit.setEnabled(true);
                    sights_edit.setEnabled(true);
                    fuel_edit.setEnabled(true);
                }
            }
        });

        fuel_edit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    hotel_edit.setEnabled(false);
                    restaurant_edit.setEnabled(false);
                    sights_edit.setEnabled(false);
                    shopping_edit.setEnabled(false);
                } else {
                    hotel_edit.setEnabled(true);
                    restaurant_edit.setEnabled(true);
                    sights_edit.setEnabled(true);
                    shopping_edit.setEnabled(true);
                }
            }
        });

        builder.setPositiveButton(getString(R.string.Save), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                ContentValues contentValues = new ContentValues();
                String searchStringDB = null;
                String timeStamp = new SimpleDateFormat("yyyy.MM.dd").format(new java.util.Date());

                if (hotel_edit.isChecked()) {
                    // Get the text value of the checked checkbox and add it to the ContentValues object
                    String searchString = "lodging";
                    searchStringDB = getString(R.string.Hotels);
                    contentValues.put("search_string", searchString);
                }
                else if (restaurant_edit.isChecked()) {
                    // Get the text value of the checked checkbox and add it to the ContentValues object
                    String searchString = "restaurant";
                    searchStringDB = getString(R.string.Restaurants);
                    contentValues.put("search_string", searchString);
                }
                else if (sights_edit.isChecked()) {
                    // Get the text value of the checked checkbox and add it to the ContentValues object
                    String searchString = "tourist_attraction"; //need different
                    searchStringDB = getString(R.string.Sights);
                    contentValues.put("search_string", searchString);
                }
                else if (shopping_edit.isChecked()) {
                    // Get the text value of the checked checkbox and add it to the ContentValues object
                    String searchString = "supermarket"; //need different store gal?
                    searchStringDB = getString(R.string.Shopping);
                    contentValues.put("search_string", searchString);
                }
                else if (fuel_edit.isChecked()) {
                    Log.d(TAG, "onClick: sitas du kartai checkBoxFuel?");
                    // Get the text value of the checked checkbox and add it to the ContentValues object
                    String searchString = "gas_station"; //need different
                    searchStringDB = getString(R.string.Fuel);
                    contentValues.put("search_string", searchString);
                }
                double[] locationArrayFrom = geoLocate2(editOrigin.getText().toString());
                double latitudeFrom = locationArrayFrom[0];
                double longitudeFrom = locationArrayFrom[1];

                double[] locationArrayTo = geoLocate2(editDestination.getText().toString());
                double latitudeTo = locationArrayTo[0];
                double longitudeTo = locationArrayTo[1];

                String tripTo = editDestination.getText().toString();

                contentValues.put("latitude_from", latitudeFrom);
                contentValues.put("longitude_from", longitudeFrom);
                contentValues.put("latitude_to", latitudeTo);
                contentValues.put("longitude_to", longitudeTo);
                contentValues.put("nameoftrip_string",tripTo+" "+"("+searchStringDB+")"+" "+timeStamp);

                String whereClause = "_id = ?";
                String[] whereArgs = {String.valueOf(myObject.getTripID())};

                db.update("coordinates", contentValues, whereClause, whereArgs);
                String toast = getString(R.string.Updated_Trip);
                Toast.makeText(getActivity(), toast, Toast.LENGTH_SHORT).show();
                //Toast.makeText(getActivity(), "A trip has been updated", Toast.LENGTH_SHORT).show();
                refreshFragment();
            }
        });
        builder.setNegativeButton(getString(R.string.Cancel), null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private List<CurrentTripObject> getDataFromDatabase() {
        List<CurrentTripObject> myObjects = new ArrayList<>();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                MyDatabaseHelper.COLUMN_NAMEOFTRIP_STRING,
                MyDatabaseHelper.COLUMN_ID
        };

        Cursor cursor = db.query(
                MyDatabaseHelper.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {
            // Get data from cursor and add to myObjects list
            Integer tripID = cursor.getInt(cursor.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_ID));
            String nameOfTrip = cursor.getString(cursor.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_NAMEOFTRIP_STRING));
            CurrentTripObject myObject = new CurrentTripObject(nameOfTrip,tripID);
            myObjects.add(myObject);
        }

        cursor.close();
        db.close();
        return myObjects;
    }

    private void refreshFragment() {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        TripListFragment tripListFragment = new TripListFragment();

        fragmentTransaction.replace(R.id.framgent_container, tripListFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
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
