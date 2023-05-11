package com.example.planatrip;

import static java.security.AccessController.getContext;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.planatrip.fragments.TestMapFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.security.AccessControlContext;
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
            //MarkerOptions markerOptions = new MarkerOptions().icon(bitmapDescriptorFromVector(, R.drawable.gas_icon));

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

                //markerOptions.icon(bitmapDescriptorFromVector(context, R.drawable.gas_icon));

                //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.gas_icon));
                //MarkerOptions().icon(bitmapDescriptorFromVector(Context, R.drawable.gas_icon));

                //markerOptions.icon(bitmapDescriptorFromVector(R.drawable.gas_icon));
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

/*    private BitmapDescriptor bitmapDescriptorFromVector(int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(mContext, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }*/
}
