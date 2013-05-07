package com.example.ProjectRestaurantSaver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONObject;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.util.Log;

public class RestaurantAsyncTaskFetchDetails extends AsyncTask<Void, Void, RestaurantDetails> {
	protected RestaurantReference item;
	protected RestaurantAdapter restaurantAdapter;

	public RestaurantAsyncTaskFetchDetails(
			RestaurantAdapter restaurantAdapter2, RestaurantReference resItem) {
		this.item = resItem;
		this.restaurantAdapter = restaurantAdapter2;
		Log.v("RestaurantAsyncTaskFetchDetails", "restaurant adapter = "+ this.restaurantAdapter);
		Log.v("RestaurantAsyncTaskFetchDetails", "resItem = "+ this.item);
	}

	@Override
	protected void onPreExecute() {

	}

	@Override
	protected RestaurantDetails doInBackground(Void... params) {
		String ref_key = item.getReferenceKey();
		RestaurantDetails res_detail = new RestaurantDetails();
		URL urlPlace = null;
		BufferedReader in = null;
		try{
			try{
				urlPlace = new URL("https://maps.googleapis.com/maps/api/place/details/json?reference="+ref_key+"&sensor=true&key=AIzaSyBsxN3NdPnzp4X4QDkh1R1tBDPQQ30lD6s");
				Log.v("URL", urlPlace.toString());
				URLConnection tc = urlPlace.openConnection();
				in = new BufferedReader(new InputStreamReader(tc.getInputStream()));
				String line, finalLine = "";
				while ((line = in.readLine()) != null) {
					finalLine += line;
				}
				JSONObject obj = new JSONObject(finalLine);
				JSONObject ja = obj.getJSONObject("result");
				res_detail.setAddress(ja.getString("formatted_address"));
				res_detail.setPhoneNumber(ja.getString("formatted_phone_number"));
				res_detail.setWebsite(ja.getString("website"));
				Log.v("Restaurant Adapter, Restaurant Address,PhNo,Lat, Lon, Website frm JSON =", res_detail.getAddress() + "  "+res_detail.getPhoneNumber()+ res_detail.getLatitude() +" "+res_detail.getLongitude()+" "+res_detail.getWebsite());
			}
			 finally {
				if(in != null) {
					in.close();
				}
			 }
		}catch(Throwable th){
			th.printStackTrace();
			throw new RuntimeException("Restaurant async task for getting restaurant details failed!!", th);
		}
					 
	    	
		Log.v("RestaurantAsyncTaskFetchDetails", "website = " + res_detail.getWebsite()+ "address"+res_detail.getAddress());
		return res_detail;
		
	}


	@Override
	protected  void onPostExecute(RestaurantDetails resDetails) {
	}


}
