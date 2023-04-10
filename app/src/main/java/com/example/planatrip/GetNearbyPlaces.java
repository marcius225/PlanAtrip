package com.example.planatrip;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class GetNearbyPlaces extends AsyncTask<Object, String, String>
{
    private String googlePlaceData, url;
    private GoogleMap mMap;
    private static String TAG = "GetNearbyPlaces";
    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap) objects[0];
        url = (String) objects[1];

        DownloadUrl downloadUrl = new DownloadUrl();
        try {
            googlePlaceData = downloadUrl.ReadTheURL(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return googlePlaceData;
    }

    @Override
    protected void onPostExecute(String s) {
        List<HashMap<String,String>> nearbyPlacesList = null;
        DataParser dataParser = new DataParser();
        nearbyPlacesList = dataParser.parse(s);

        DisplayNearbyPlaces(nearbyPlacesList);
    }

    private void DisplayNearbyPlaces(List<HashMap<String,String>> nearbyPlacesList)
    {
        for (int i = 0; i<nearbyPlacesList.size(); i++)
        {
            MarkerOptions markerOptions = new MarkerOptions();

            HashMap<String,String> googleNearbyPlace = nearbyPlacesList.get(i);
            String nameOfPlace = googleNearbyPlace.get("place_name");
            Log.d(TAG, "DisplayNearbyPlaces: name"+nameOfPlace);
            String vicinity = googleNearbyPlace.get("vicinity");
            String types = googleNearbyPlace.get("types");
            Log.d(TAG, "DisplayNearbyPlaces: types "+types);
            if(types.contains("lodging")==true){
                Log.d(TAG, "DisplayNearbyPlaces: yra?");
            }
            double lat = Double.parseDouble(googleNearbyPlace.get("lat"));
            double lng = Double.parseDouble(googleNearbyPlace.get("lng"));

            LatLng latLng  =new LatLng(lat, lng);
            markerOptions.position(latLng);
            markerOptions.title(nameOfPlace + " : " + vicinity);

            LatLng markerLatLng = markerOptions.getPosition();
            double markerLat = markerLatLng.latitude;
            double markerLng = markerLatLng.longitude;

            Log.d(TAG, "onMarkerClick: marker coordinates" + markerLat +" " + markerLng);

            Log.d(TAG, "DisplayNearbyPlaces: kazka cia darom"+ markerOptions.getPosition());
/*            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            mMap.addMarker(markerOptions);*/

            if(types.contains("lodging")==true)
            {
                //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.hotel_icon));
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
            }
            else if(types.contains("restaurant")==true)
            {
                //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.restaurant_icon));
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
            }
            else if(types.contains("gas_station")==true){
                //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.gas_icon));
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            }
            else if(types.contains("supermarket")==true){
                //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.gas_icon));
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            }
            else if(types.contains("tourist_attraction")==true){
                //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.gas_icon));
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            }
            else markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

            mMap.addMarker(markerOptions);
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            //mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
        }

    }
}
