package com.example.richa_764947_androidlab;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ParseData {

    //get detail of a single place

    private HashMap<String,String> getNearByPlace(JSONObject jsonObject) {

        HashMap<String,String>  googleplace = new HashMap<>();
        String placeName = "-NA-";
        String  vicinity = "-NA-";
        String lat = "";
        String lng = "";
        String refernce = "";

        try {
        if (!jsonObject.isNull("name")){

                placeName = jsonObject.getString("name");
            }
        if(!jsonObject.isNull("vicinity"))
        {
            vicinity = jsonObject.getString("vicinity");
        }
        lat = jsonObject.getJSONObject("geometry").getJSONObject("location").getString("lat");
        lng = jsonObject.getJSONObject("geometry").getJSONObject("location").getString("lng");
        refernce = jsonObject.getString("reference");

        googleplace.put("place_name",placeName);
        googleplace.put("vicinity",vicinity);
        googleplace.put("lat",lat);
        googleplace.put("lng",lng);
        googleplace.put("reference",refernce);

        }catch (JSONException e) {
            e.printStackTrace();
        }

        return googleplace;
    }


    //to get list of near by place
    private List<HashMap<String,String>> getNearByPlaces(JSONArray jsonArray){
        int count = jsonArray.length();

        List<HashMap<String,String>> placseList = new ArrayList<>();
        HashMap<String,String> places = null;

        for (int i = 0;i<count;i++){
            try {
                places = getNearByPlace((JSONObject) jsonArray.get(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            placseList.add(places);
        }

        return placseList;
    }

    //parse a json data and make a list

    public List<HashMap<String,String>> parse(String jsonDta){
        JSONArray jsonArray = null;
        try {
            JSONObject jsonObject =  new JSONObject(jsonDta);
            jsonArray = jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getNearByPlaces(jsonArray);
    }
}
