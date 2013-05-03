package com.example.ProjectRestaurantSaver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.util.Log;

public class RestaurantHttpAsyncTask extends AsyncTask<Void, Void, ArrayList<RestaurantReference>> {
	public ProgressDialog progressDialog;
	private ListActivity m_activity;
	private RestaurantAdapter restaurantAdapter;
	private String[] keywords;

	protected RestaurantHttpAsyncTask(ListActivity activity, String[] keywords) {
		m_activity = activity;
		this.keywords = keywords;
	}

	@Override
	protected void onPreExecute() {
		progressDialog = new ProgressDialog(m_activity.getParent());
		progressDialog.setMessage("Wait ...");
		progressDialog.setCancelable(false);
		progressDialog.setIndeterminate(true);
		progressDialog.show();
	}

	@Override
	protected ArrayList<RestaurantReference> doInBackground(Void... params) {
		ArrayList<RestaurantReference> listItems;
		listItems = new ArrayList<RestaurantReference>();

		double lat = 0.0;
		double lon = 0.0;
		LocationManager lm = (LocationManager) m_activity.getSystemService(Context.LOCATION_SERVICE);    
		Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);  

		if(location == null){
			lat = 0.0;
			lon = 0.0;
		}
		else{
			Log.v("RestaurantHttpAsyncTask", "Latitude = "+Double.toString(location.getLatitude()));
			Log.v("RestaurantHttpAsyncTask", "Longitude = "+Double.toString(location.getLongitude())); 		
			lat = location.getLatitude();
			lon = location.getLongitude();
		}
		try{
			String urldef = "https://maps.googleapis.com/maps/api/place/search/json?location="+lat+","+lon+"&radius=700&types=restaurant&sensor=false&key=AIzaSyBsxN3NdPnzp4X4QDkh1R1tBDPQQ30lD6s";
			Log.v("URLdef", urldef);
			//String urldef = "https://maps.googleapis.com/maps/api/place/search/json?location=37.391351,-122.04566&radius=700&types=restaurant&sensor=false&key=AIzaSyBsxN3NdPnzp4X4QDkh1R1tBDPQQ30lD6s";
			for(String keyword: keywords) {
				URLConnection tc = null;
				BufferedReader in = null;
				try{
					//String urldef = "https://maps.googleapis.com/maps/api/place/search/json?location=37.387413,-122.046046&radius=700&types=restaurant&keyword="+keyword+"&sensor=false&key=AIzaSyBsxN3NdPnzp4X4QDkh1R1tBDPQQ30lD6s";
					//String urldef = "https://maps.googleapis.com/maps/api/place/search/json?location=37.387413,-122.046046&radius=2700&types=restaurant&keyword=subway"+"&sensor=false&key=AIzaSyBsxN3NdPnzp4X4QDkh1R1tBDPQQ30lD6s";
					//String urldef = "https://maps.googleapis.com/maps/api/place/search/json?location="+lat+","+lon+"&radius=700&types=restaurant&sensor=false&key=AIzaSyBsxN3NdPnzp4X4QDkh1R1tBDPQQ30lD6s";
					Log.v("URLdef", urldef);
					URL urlPlace = new URL(urldef);    		
					tc = urlPlace.openConnection();
					in = new BufferedReader(new InputStreamReader(tc.getInputStream()));
					String line, finalLine = "";
					while ((line = in.readLine()) != null) {
						finalLine += line;
					}
					//System.out.println("Nearby Restaurant JSON \n"+ finalLine);

					JSONObject obj = new JSONObject(finalLine);
					JSONArray ja = obj.getJSONArray("results");
					//Parse the received JSON file           
					for (int i = 0; i < ja.length(); i++) {
						JSONObject jo = (JSONObject) ja.get(i);
						RestaurantReference ref = new RestaurantReference();
						ref.setId((jo.getString("id")));
						ref.setName((jo.getString("name")));
						ref.setAddress(jo.getString("vicinity"));
						//Log.v("RestaurantHTTP Task", " vicinity = "+jo.getString("vicinity"));
						JSONObject jb = jo.getJSONObject("geometry");
						JSONObject jbo = jb.getJSONObject("location");
						//Log.v("Latitude", jbo.getString("lat"));
						//Log.v("Longitude", jbo.getString("lng"));
						//if(!restaurantMap.containsKey((jo.getString("id")))){
							//restaurantMap.put((jo.getString("id")), (jo.getString("name")));
							//Log.v("res_id", (jo.getString("id")));
							//Log.v("res_name", (jo.getString("name")));
							ref.setReferenceKey(jo.getString("reference"));
							ref.setLatitude(Double.parseDouble(jbo.getString("lat")));
							ref.setLongitude(Double.parseDouble(jbo.getString("lng")));
							listItems.add(ref);
						//}
					}
				} finally {
					if(in != null) {
						in.close();
					}
				}
			} 
			
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException("Error while getting data from Google Places API",e);
		}
		//Log.v("NearbyRestaurantsSearch", "Got " + listItems.size() + " restaurants");
		return listItems;
	}


	@Override
	protected void onPostExecute(ArrayList<RestaurantReference> listItemsresult) {
		//Check if the ArrayList contains objects
		if(listItemsresult != null &&
				listItemsresult.size() > 0) {
			//Loop through the objects of the ListArray and update each row in the RestaurantAdapter class.
			for(int i = 0; i< listItemsresult.size();i++){
				//this.getRestaurantAdapter().add(list.get(i));
				this.getRestaurantAdapter().add(listItemsresult.get(i));
			}
			//Notify the Restaurant Adapter that the data has changed and the ListView needs to be refreshed.
			this.getRestaurantAdapter().notifyDataSetChanged();
		}
		//m_activity.setListAdapter(new CustomRestaurantArrayAdapter(m_activity, nameArray));		
		progressDialog.dismiss();
	}

	public void setRestaurantAdapter(RestaurantAdapter restaurantAdapter) {
		this.restaurantAdapter = restaurantAdapter;
	}

	public RestaurantAdapter getRestaurantAdapter() {
		return restaurantAdapter;
	}

}
