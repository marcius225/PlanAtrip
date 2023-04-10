package com.example.planatrip;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.MODE_PRIVATE;

import static com.google.maps.android.Context.getApplicationContext;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.GeoPoint;
import com.google.gson.Gson;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executor;

public class TestMapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private CameraPosition mCameraPosition;
    private boolean mMapLoaded;
    private static String TAG = "MapFragment";
    private int STORAGE_PERMISSION_CODE = 1;
    ActivityResultLauncher<String[]> mPermissionLauncher;
    private boolean isReadingGranted = false;
    private boolean isWritingGranted = false;
    private static final String PREFS_NAME = "MyPrefs";
    private static final String CAMERA_POSITION_KEY = "camera_position";
    private SharedPreferences mPrefs;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private FusedLocationProviderClient fusedLocationClient;
    double lat, lng;
    private double latitude, longitude;
    List<Address> user = null;
    Location myLocation=null;
    private int radius = 20000;
    private MyDatabaseHelper mDbHelper;

    private GeoApiContext mGeoApiContext;

    private String tripName = null;

    private EditText mSearchtext;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        //mSearchtext = (EditText) rootView.findViewById(R.id.input_search);

        if(savedInstanceState!=null){
            String value = getArguments().getString("sudas");
            Log.d(TAG, "onCreateView: sudas"+value);
        }
        return rootView;
    }

    public void setTripName(String tripName1){
        tripName = tripName1;
    }

    private void requsestPermission() {
        Log.d(TAG, "requsestPermission: Ziurim sita gaidy");

        isReadingGranted = ContextCompat.checkSelfPermission(
                getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED;

        isWritingGranted = ContextCompat.checkSelfPermission(
                getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED;

        List<String> permissionRequest = new ArrayList<>();

        if (!isReadingGranted) {
            permissionRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (!isWritingGranted) {
            permissionRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!permissionRequest.isEmpty()) {
            mPermissionLauncher.launch(permissionRequest.toArray(new String[0]));
        }
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPrefs = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);



        LatLng origin = new LatLng(54.6623909, 25.3066289); //nupiest linijai is
        LatLng destination = new LatLng(54.9670172, 24.0738475); //nupiest linijai i
        //LatLng origin = new LatLng(0,0);



        mDbHelper = new MyDatabaseHelper(getActivity());
        //init();

        if(mGeoApiContext == null){
            mGeoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_maps_api_key))
                    .build();
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        //mapFragment.getMapAsync(this);
        Log.d(TAG, "onViewCreated: Kvieciam on create view");
        //getLocation();
        //getUserLocation(); ///kvieciam


        getUserLocation2(new LocationCallback() {
            @Override
            public void onNewLocationAvailable(double[] location) {
                // Use location array here
                double latitude = location[0];
                double longitude = location[1];
                Log.d(TAG, "Latitude is array: " + latitude);
                Log.d(TAG, "Longitude is array: " + longitude);
/*                double[] locationArray = geoLocate2();
                double latitude2 = locationArray[0];
                double longitude2 = locationArray[1];
                LatLng origin = new LatLng(latitude, longitude);
                LatLng destination = new LatLng(latitude2, longitude2);
                // Do something with the latitude and longitude values, e.g. update UI or make API calls
                drawRouteOnMap(origin,destination);*/
            }
        });

        //drawRouteOnMap(origin,destination);

/*        EditText coordinatesEditText = (EditText) getView().findViewById(R.id.coordinates_edittext);
        Button addMarkerButton = (Button) getView().findViewById(R.id.add_marker_button);
        addMarkerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Parse the coordinates from the text field
                String coordinates = coordinatesEditText.getText().toString();
                String[] latlng = coordinates.split(",");
                double lat = Double.parseDouble(latlng[0].trim());
                double lng = Double.parseDouble(latlng[1].trim());

                // Add a marker to the map at the specified coordinates
                LatLng position = new LatLng(lat, lng);
                MarkerOptions markerOptions = new MarkerOptions().position(position).title("Marker");
                mMap.addMarker(markerOptions);
            }
        });

        Button testButton = (Button) getView().findViewById(R.id.myButton);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.clear();
                String hospital = "hospital", school = "school", restaurant ="restaurant";
                String url = getUrl(latitude, longitude, restaurant);
                Object transferData[] = new Object[2];
                GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces();
                transferData[0] = mMap;
                transferData[1] = url;

                getNearbyPlaces.execute(transferData);
                Toast.makeText(getActivity(), "Searching ....", Toast.LENGTH_SHORT).show();
                Toast.makeText(getActivity(), "Showing ....", Toast.LENGTH_SHORT).show();

                getUserLocation2(new LocationCallback() {
                    @Override
                    public void onNewLocationAvailable(double[] location) {
                        // Use location array here
                        double latitude = location[0];
                        double longitude = location[1];
                        Log.d(TAG, "Latitude is array: " + latitude);
                        Log.d(TAG, "Longitude is array: " + longitude);
              double[] locationArray = geoLocate2();
                double latitude2 = locationArray[0];
                double longitude2 = locationArray[1];
                LatLng origin = new LatLng(latitude, longitude);
                LatLng destination = new LatLng(latitude2, longitude2);
                // Do something with the latitude and longitude values, e.g. update UI or make API calls
                //drawRouteOnMap(origin,destination);
                    }
                });
            }
        });*/


        mPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
            @Override
            public void onActivityResult(Map<String, Boolean> result) {

                if (result.get(Manifest.permission.READ_EXTERNAL_STORAGE) != null) {
                    isReadingGranted = result.get(Manifest.permission.READ_EXTERNAL_STORAGE);
                }

                if (result.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) != null)
                    isWritingGranted = result.get(Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (result.get(Manifest.permission.ACCESS_FINE_LOCATION)!=null){

                }
            }
        });

        requsestPermission();

    }

    private String getUrl(double latitude, double longitude, String nearbyPlace)
    {
        //D/MapFragment: onSuccess: Latitude48.8566133
        //D/MapFragment: onSuccess: Longitude2.3522217
        StringBuilder googleURL = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googleURL.append("location=" + latitude + "," + longitude); //54.66239 25.3066283
        googleURL.append("&radius=" + radius);
        googleURL.append("&type=" + nearbyPlace);
        googleURL.append("&sensor=true");
        googleURL.append("&key=" + "AIzaSyCd2tdGHYf-wYHhnnBJxlO_klDQsAFWo-U");

        Log.d(TAG, "getUrl: url = "+ googleURL.toString());

        return googleURL.toString();

    }

/*    private void init() {
        Log.d(TAG, "init: init");


        mSearchtext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionID, KeyEvent keyEvent) {
                if (actionID == EditorInfo.IME_ACTION_SEARCH || actionID == EditorInfo.IME_ACTION_NONE || keyEvent.getAction() == KeyEvent.ACTION_DOWN || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                    double[] locationArray = geoLocate2();
                    double latitude = locationArray[0];
                    double longitude = locationArray[1];
                    Log.d(TAG, "onEditorAction: latas"+latitude);
                    Log.d(TAG, "onEditorAction: longas"+longitude);
                }
                return false;
            }
        });
    }*/

    private void geoLocate() {
        Log.d(TAG, "geoLocate: geolocation");
        String searchString = mSearchtext.getText().toString();

        Geocoder geocoder = new Geocoder(getContext());

        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString, 1);

        } catch (IOException e) {
            Log.e(TAG, "geoLocate: IOException" + e.getMessage());
        }

        if (list.size() > 0) {
            Address address = list.get(0);

            Log.d(TAG, "geoLocate: found " + address.toString());

            double latitude = address.getLatitude();
            double longitude = address.getLongitude();

            Log.d(TAG, "geoLocate: latitude " + latitude); //gaunam rastos lokacijos lat
            Log.d(TAG, "geoLocate: longitude " + longitude); //gaunam rastos lokacicjos lgt
            //Toast.makeText(getActivity(), address.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private double[] geoLocate2() {
        double[] locationArray = new double[2];
        Log.d(TAG, "geoLocate2: geolocation");
        String searchString = mSearchtext.getText().toString();

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
            //Toast.makeText(getActivity(), address.toString(), Toast.LENGTH_SHORT).show();
        }
        return locationArray;
    }

   /* private void getUserLocation() {
        // Check if location permissions are granted
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getActivity(), "Waaaaaaaaaaa", Toast.LENGTH_SHORT).show();

            // Request the user's location
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                // Use the location
                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();
                                Log.d(TAG, "onSuccess: Latitude"+latitude);
                                Log.d(TAG, "onSuccess: Longitude"+longitude);
                                // Do something with latitude and longitude
                                Toast.makeText(getActivity(), "Lat"+latitude+"Lat"+longitude, Toast.LENGTH_LONG).show();
                            }
                        }
                    })
                    .addOnFailureListener(getActivity(), new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "onFailure: fail",e );
                            Toast.makeText(getActivity(), "faillllllllllllllllllll", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Log.d(TAG, "getUserLocation: gaidysas");
        }
    }*/

/*    private double[] getUserLocation2() {
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

        return locationArray;
    }*/


    public interface LocationCallback {
        void onNewLocationAvailable(double[] location);
    }

    private void getUserLocation2(LocationCallback callback) {
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

        private void setupMapIfNeeded() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        if (mMap == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: alio");
        mMap = googleMap;
        getFromDB();
        //Log.d(TAG, "onMapReady: sudas"+value);

        MapStateManager mgr = new MapStateManager(getActivity());
        CameraPosition position = mgr.getSavedCameraPosition();
        if (position != null) {
            CameraUpdate update = CameraUpdateFactory.newCameraPosition(position);
            Toast.makeText(getActivity(), "entering Resume State", Toast.LENGTH_SHORT).show();
            mMap.moveCamera(update);

            mMap.setMapType(mgr.getSavedMapType());
        }

/*        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                LatLng markerLatLng = marker.getPosition();
                double markerLat = markerLatLng.latitude;
                double markerLng = markerLatLng.longitude;

                Log.d(TAG, "onMarkerClick: marker coordinates" + markerLat +" " + markerLng);
                return true;
            }
        });*/
    }

    public void getFromDB(){
        Log.d(TAG, "getFromDB: sudas");
        // Query the database to retrieve all saved coordinates
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor;
        Cursor cursor2;

        String[] projection2 = {
                MyDatabaseHelper.COLUMN_CURRENTTRIP_STRING
        };

        cursor2 = db.query(
                MyDatabaseHelper.TABLE_NAME2,
                projection2,
                MyDatabaseHelper.COLUMN_ID + "=?",
                new String[]{"0"},
                null,
                null,
                null
        );

         cursor2.moveToNext();
         setTripName(cursor2.getString(cursor2.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_CURRENTTRIP_STRING)));


        String[] projection = {
                MyDatabaseHelper.COLUMN_LATITUDE_FROM,
                MyDatabaseHelper.COLUMN_LONGITUDE_FROM,
                MyDatabaseHelper.COLUMN_LONGITUDE_TO,
                MyDatabaseHelper.COLUMN_LATITUDE_TO,
                MyDatabaseHelper.COLUMN_SEARCH_STRING
        };
        if(tripName!=null) {
            cursor = db.query(
                    MyDatabaseHelper.TABLE_NAME,
                    projection,
                    MyDatabaseHelper.COLUMN_NAMEOFTRIP_STRING + "=?",
                    new String[]{tripName},
                    null,
                    null,
                    null
            );
        }
        else{
            cursor = db.query(
                    MyDatabaseHelper.TABLE_NAME,
                    projection,
                    null,
                    null,
                    null,
                    null,
                    MyDatabaseHelper.COLUMN_ID + " DESC",
                    "1"
            );
        }

        while (cursor.moveToNext()) {
            double latitudeFrom = cursor.getDouble(cursor.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_LATITUDE_FROM));
            double longitudeFrom = cursor.getDouble(cursor.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_LONGITUDE_FROM));
            LatLng coordinateFrom = new LatLng(latitudeFrom, longitudeFrom);
            mMap.addMarker(new MarkerOptions().position(coordinateFrom));

            double latitudeTo = cursor.getDouble(cursor.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_LATITUDE_TO));
            double longitudeTo = cursor.getDouble(cursor.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_LONGITUDE_TO));
            LatLng coordinateTo = new LatLng(latitudeTo, longitudeTo);
            mMap.addMarker(new MarkerOptions().position(coordinateTo));

            String searchString = cursor.getString(cursor.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_SEARCH_STRING));

            //String nameOfTrip = cursor.getString(cursor.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_NAMEOFTRIP_STRING));

            drawRouteOnMap(coordinateFrom, coordinateTo, new OnRouteReadyCallback() {
                @Override
                public void onRouteReady(List<LatLng> routePoints) {
                    Log.d(TAG, "onRouteReady: routepoints krw " + searchString);
                    for (LatLng point : routePoints) {
                        double latitude = point.latitude;
                        double longitude = point.longitude;
                        String url = getUrl(latitude, longitude, searchString);
                        Object transferData[] = new Object[2];
                        GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces();
                        transferData[0] = mMap;
                        transferData[1] = url;
                        getNearbyPlaces.execute(transferData);
                    }

                }
            });
        }

        cursor.close();
        db.close();
    }

    @Override
    public void onPause() {
        super.onPause();
        MapStateManager mgr = new MapStateManager(getActivity());
        mgr.saveMapState(mMap);
        Toast.makeText(getActivity(), "Map State has been save?", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        setupMapIfNeeded();
    }

    public void drawRouteOnMap2(LatLng origin, LatLng destination) {
        Log.d(TAG, "drawRouteOnMap: lattas"+latitude);
        // Create a new Directions API request.
        Log.d(TAG, "drawRouteOnMap: calling this");
        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);
        directions.origin(new com.google.maps.model.LatLng(origin.latitude, origin.longitude));
        directions.destination(new com.google.maps.model.LatLng(destination.latitude, destination.longitude));

        // Call the API asynchronously to get the route.
        directions.setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                // Get the first route from the response.
                DirectionsRoute route = result.routes[0];
                Log.d(TAG, "onResult: calling this");

                // Convert the com.google.maps.model.LatLng objects to com.google.android.gms.maps.model.LatLng objects.
                List<com.google.maps.model.LatLng> path = route.overviewPolyline.decodePath();
                List<LatLng> points = new ArrayList<>();
                for (com.google.maps.model.LatLng latLng : path) {
                    points.add(new LatLng(latLng.lat, latLng.lng));
                }

                List<LatLng> sparsePoints = new ArrayList<>();
                for (int i = 0; i < points.size(); i += 30) {
                    sparsePoints.add(points.get(i));
                }

                // Draw the route on the map.
                Log.d(TAG, "onResult: pointai"+points);
                PolylineOptions polylineOptions = new PolylineOptions()
                        .addAll(points)
                        .color(Color.RED)
                        .width(5f);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mMap.addPolyline(polylineOptions);
                    }
                });
            }

            @Override
            public void onFailure(Throwable e) {
                // Handle the failure.
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "Directions API error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }


    public interface OnRouteReadyCallback {
        void onRouteReady(List<LatLng> routePoints);
    }

/*    public void drawRouteOnMap(LatLng origin, LatLng destination, OnRouteReadyCallback callback) {
        // Create a new Directions API request.
        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);
        directions.origin(new com.google.maps.model.LatLng(origin.latitude, origin.longitude));
        directions.destination(new com.google.maps.model.LatLng(destination.latitude, destination.longitude));

        // Call the API asynchronously to get the route.
        directions.setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                // Get the first route from the response.
                DirectionsRoute route = result.routes[0];

                // Convert the com.google.maps.model.LatLng objects to com.google.android.gms.maps.model.LatLng objects.
                List<com.google.maps.model.LatLng> path = route.overviewPolyline.decodePath();
                List<LatLng> points = new ArrayList<>();
                for (com.google.maps.model.LatLng latLng : path) {
                    points.add(new LatLng(latLng.lat, latLng.lng));
                }

                List<LatLng> sparsePoints = new ArrayList<>();
                for (int i = 0; i < points.size(); i += 30) {
                    sparsePoints.add(points.get(i));
                }

                // Call the callback with the result.
                callback.onRouteReady(sparsePoints);

                // Draw the route on the map.
                PolylineOptions polylineOptions = new PolylineOptions()
                        .addAll(points)
                        .color(Color.RED)
                        .width(5f);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mMap.addPolyline(polylineOptions);
                    }
                });

                for (LatLng point : points) {
                    Log.d(TAG, "onResult: kas cia nx"+point);
                }


            }

            @Override
            public void onFailure(Throwable e) {
                // Handle the failure.
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "Directions API error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }*/





    public void drawRouteOnMap(LatLng origin, LatLng destination, OnRouteReadyCallback callback) {
        Log.d(TAG, "drawRouteOnMap: kviecia sita");
        // Create a new Directions API request.
        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);
        directions.origin(new com.google.maps.model.LatLng(origin.latitude, origin.longitude));
        directions.destination(new com.google.maps.model.LatLng(destination.latitude, destination.longitude));

        // Call the API asynchronously to get the route.
        directions.setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                // Get the first route from the response.
                DirectionsRoute route = result.routes[0];

                // Create a list to hold all the points on the route.
                List<LatLng> points = new ArrayList<>();

                //points.add(new LatLng(54.6631369, 25.2378419));

                // Loop through all the legs of the route.
                for (DirectionsLeg leg : route.legs) {
                    // Loop through all the steps of each leg.
                    for (DirectionsStep step : leg.steps) {
                        // Decode the polyline of each step to get the points on that step.
                        List<com.google.maps.model.LatLng> path = PolylineEncoding.decode(step.polyline.getEncodedPath());
                        for (com.google.maps.model.LatLng latLng : path) {
                            points.add(new LatLng(latLng.lat, latLng.lng));
                        }
                    }
                }

                // Call the callback with the result.
                //callback.onRouteReady(points);

                List<LatLng> sparsePoints = new ArrayList<>();
                for (int i = 0; i < points.size(); i += 300) {
                    sparsePoints.add(points.get(i));
                }

                // Call the callback with the result.
                callback.onRouteReady(sparsePoints);

                // Draw the route on the map.
                PolylineOptions polylineOptions = new PolylineOptions()
                        .addAll(points)
                        .color(Color.RED)
                        .width(5f);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mMap.addPolyline(polylineOptions);
/*                        for (int i = 0; i < points.size(); i++) {
                            if (i % 60 == 0) {
                                MarkerOptions markerOptions = new MarkerOptions().position(points.get(i));
                                mMap.addMarker(markerOptions);
                            }
                            polylineOptions.add(points.get(i));
                        }*/
                    }
                });

                for (LatLng point : points) {
                    Log.d(TAG, "onResult: kas cia nx"+point);
                }
            }

            @Override
            public void onFailure(Throwable e) {
                // Handle the failure.
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "Directions API error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }



    //WITH ADDITIONAL SEARCH FOR
/*    public void drawRouteOnMap(LatLng origin, LatLng destination,String placeType ,OnRouteReadyCallback callback) {
        // Create a new Directions API request.
        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);
        directions.origin(new com.google.maps.model.LatLng(origin.latitude, origin.longitude));
        directions.destination(new com.google.maps.model.LatLng(destination.latitude, destination.longitude));

        // Call the API asynchronously to get the route.
        directions.setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                // Get the first route from the response.
                DirectionsRoute route = result.routes[0];

                // Convert the com.google.maps.model.LatLng objects to com.google.android.gms.maps.model.LatLng objects.
                List<com.google.maps.model.LatLng> path = route.overviewPolyline.decodePath();
                List<LatLng> points = new ArrayList<>();
                for (com.google.maps.model.LatLng latLng : path) {
                    points.add(new LatLng(latLng.lat, latLng.lng));
                }

                List<LatLng> sparsePoints = new ArrayList<>();
                for (int i = 0; i < points.size(); i += 30) {
                    sparsePoints.add(points.get(i));
                }

                // Call the callback with the result.
                callback.onRouteReady(sparsePoints);

                // Draw the route on the map.
                PolylineOptions polylineOptions = new PolylineOptions()
                        .addAll(points)
                        .color(Color.RED)
                        .width(5f);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mMap.addPolyline(polylineOptions);
                    }
                });

                // Query the Places API for nearby places along the route.
                for (LatLng point : points) {
                    String url = getUrl(point.latitude, point.longitude, placeType);
                    Object transferData[] = new Object[2];
                    GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces();
                    transferData[0] = mMap;
                    transferData[1] = url;
                    getNearbyPlaces.execute(transferData);
                    Log.d(TAG, "onResult: kas cia nx");
                }


            }

            @Override
            public void onFailure(Throwable e) {
                // Handle the failure.
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "Directions API error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }*/


}