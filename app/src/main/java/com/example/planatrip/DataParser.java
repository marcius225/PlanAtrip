package com.example.planatrip;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataParser {

    private HashMap<String, String> getSingleNearbyPlace(JSONObject googlePlaceJson){
        HashMap<String, String> googlePlaceMap = new HashMap<>();
        String NameOfPlace = "-NA-";
        String vicinity = "-NA-";
        List<String> typesList = new ArrayList<>();
        String latitude = "";
        String longitude = "";
        String reference = "";

        try {
            if(!googlePlaceJson.isNull("name"))
            {
                NameOfPlace = googlePlaceJson.getString("name");
            }
            if(!googlePlaceJson.isNull("vicinity"))
            {
                vicinity = googlePlaceJson.getString("vicinity");
            }
            if(!googlePlaceJson.isNull("types"))
            {
                JSONArray typesArray = googlePlaceJson.getJSONArray("types");
                for (int i = 0; i < typesArray.length(); i++) {
                    typesList.add(typesArray.getString(i));
                }
            }
            latitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat");

            longitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng");

            reference = googlePlaceJson.getString("reference");

            googlePlaceMap.put("place_name", NameOfPlace);
            googlePlaceMap.put("vicinity", vicinity);
            googlePlaceMap.put("types", typesList.toString());
            googlePlaceMap.put("lat", latitude);
            googlePlaceMap.put("lng", longitude);
            googlePlaceMap.put("reference", reference);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return googlePlaceMap;
    }

    private List<HashMap<String, String >> getAllNearbyPlaces(JSONArray jsonArray)
    {
        int counter = jsonArray.length();

        List<HashMap<String, String >> NearbyPlacesList = new ArrayList<>();

        HashMap<String, String> NearbyPlaceMap = null;

        for (int i = 0; i< counter; i++)
        {
            try {
                NearbyPlaceMap = getSingleNearbyPlace((JSONObject) jsonArray.get(i));
                NearbyPlacesList.add(NearbyPlaceMap);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return NearbyPlacesList;
    }


    public List<HashMap<String, String>> parse(String jsonData)
    {
        JSONArray jsonArray = null;
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getAllNearbyPlaces(jsonArray);
    }
}
