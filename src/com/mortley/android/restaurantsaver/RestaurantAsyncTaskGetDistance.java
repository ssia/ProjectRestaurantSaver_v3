package com.mortley.android.restaurantsaver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

import android.util.Log;
import android.widget.TextView;

public class RestaurantAsyncTaskGetDistance extends AsyncTask<Void, Void, String> {
	private RestaurantAdapter restaurantAdapter;
	String distanceURL;
	TextView distanceLabel;

	public RestaurantAsyncTaskGetDistance(RestaurantAdapter restaurantAdapter2,
			String distanceURL, TextView distanceLabel) {
		this.setRestaurantAdapter(restaurantAdapter2);
		this.distanceURL = distanceURL;
		this.distanceLabel = distanceLabel;
	}



	@Override
	protected void onPreExecute() {

	}

	@Override
	protected String doInBackground(Void... params) {
		URL urlPlace;
		String distanceinMiles = null;
		try {

			urlPlace = new URL(distanceURL);
			//Log.v("RestaurantAsyncTaskGetDistance","urlPlace"+urlPlace );
			//urlPlace = new URL("http://maps.googleapis.com/maps/api/distancematrix/json?origins=401%20Castro%20St,%20Mountain%20View,%20CA%2094041,%20USA&destinations=800%20California%20StMountain%20View,%20CA%2094041&mode=driving&sensor=false&units=imperial");
			URLConnection tc = urlPlace.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(tc.getInputStream()));
			String line, finalLine = "";
			while ((line = in.readLine()) != null) {
				finalLine += line;
			}
			in.close();
			//Log.v("RestaurantAsyncTaskGetDistance", "distance json ="+finalLine);
			JSONObject obj = new JSONObject(finalLine);
			JSONArray ja = obj.getJSONArray("rows");
			JSONObject jo = (JSONObject) ja.get(0);
			JSONArray jb = jo.getJSONArray("elements");
			JSONObject jbo = jb.getJSONObject(0).getJSONObject("distance");
			distanceinMiles= jbo.getString("text");
			//Log.v("RestaurantAsyncTaskGetDistance", "distance in miles = "+distanceinMiles);


		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    		
		return distanceinMiles; 
	}


	@Override
	protected void onPostExecute(String distanceinMiles) {
		this.distanceLabel.setText(distanceinMiles);
	}

	public void setRestaurantAdapter(RestaurantAdapter restaurantAdapter) {
		this.restaurantAdapter = restaurantAdapter;
	}

	public RestaurantAdapter getRestaurantAdapter() {
		return restaurantAdapter;
	}

}
