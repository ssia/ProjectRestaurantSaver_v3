package com.example.ProjectRestaurantSaver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONArray;
import org.json.JSONObject;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

public class RestaurantHttpAsyncTaskTextSearch extends AsyncTask<Void, Void, ArrayList<RestaurantReference>> {
	public ProgressDialog progressDialog;
	private ListActivity m_activity;
	private RestaurantAdapter restaurantAdapter;
	private String keywords;

	protected RestaurantHttpAsyncTaskTextSearch(ListActivity activity, String keywords) {
		m_activity = activity;
		this.keywords = keywords;
	}

	@Override
	protected void onPreExecute() {
		progressDialog = new ProgressDialog(m_activity.getParent());
		progressDialog.setMessage("Wait ..."+"\n"+ "Powered by Google**");
		progressDialog.setCancelable(false);
		progressDialog.setIndeterminate(true);
		progressDialog.show();
	}

	@Override
	protected ArrayList<RestaurantReference> doInBackground(Void... params) {
		ArrayList<RestaurantReference> listItems, listReturn;
		listItems = new ArrayList<RestaurantReference>();
		BufferedReader in = null;
		try{
				//String urldef = "https://maps.googleapis.com/maps/api/place/search/json?location=37.391156,-122.080564&radius=700&types=restaurant&keyword="+keyword+"&sensor=false&key=AIzaSyBsxN3NdPnzp4X4QDkh1R1tBDPQQ30lD6s";
				
				String urldef = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=restaurants%20"+keywords+"&sensor=true&key=AIzaSyBsxN3NdPnzp4X4QDkh1R1tBDPQQ30lD6s";
				//Log.v("***URL", urldef);
				URL urlPlace = new URL(urldef);    		
				URLConnection tc = urlPlace.openConnection();
				in = new BufferedReader(new InputStreamReader(tc.getInputStream()));
				String line, finalLine = "";
				while ((line = in.readLine()) != null) {
					finalLine += line;
				}
				Log.v("RestaurantAsyncTask","Nearby Restaurant JSON= \n"+ finalLine);

				JSONObject obj = new JSONObject(finalLine);
				JSONArray ja = obj.getJSONArray("results");
				//Parse the received JSON file           
				for (int i = 0; i < ja.length(); i++) {
					JSONObject jo = (JSONObject) ja.get(i);
					RestaurantReference ref = new RestaurantReference();
					ref.setId((jo.getString("id")));
					ref.setName((jo.getString("name")));
					ref.setAddress(jo.getString("formatted_address"));
					JSONObject jb = jo.getJSONObject("geometry");
					JSONObject jbo = jb.getJSONObject("location");
					//Log.v("Latitude", jbo.getString("lat"));
					//Log.v("Longitude", jbo.getString("lng"));
					//if(!restaurantMap.containsKey((jo.getString("name")))){
					//	restaurantMap.put((jo.getString("name")), (jo.getString("name")));
						//Log.v("res_name", (jo.getString("name")));
						ref.setReferenceKey(jo.getString("reference"));
						ref.setLatitude(Double.parseDouble(jbo.getString("lat")));
						ref.setLongitude(Double.parseDouble(jbo.getString("lng")));
						listItems.add(ref);
					//}
				}
			
		}
		catch(Throwable e) {
			e.printStackTrace();
			
			throw new RuntimeException("Failed to get restaurants using text search from google api." , e);
		}finally{
			if(in != null){
				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		//Log.v("RestaurantsTextSearch", "Got " + listItems.size() + " restaurants");
		
		//Remove any duplicating restaurant value
		HashMap<String, RestaurantReference> resMap = new HashMap <String, RestaurantReference>();
		for(int i = 0; i < listItems.size(); i++){
			resMap.put(listItems.get(i).getId(), listItems.get(i));
			
		}
		Log.v("RestaurantHttpAsyncTaskTextSearch", "resMap = " + resMap.size());
		ArrayList<RestaurantReference> valuesList = new ArrayList<RestaurantReference>(resMap.values());
		Log.v("RestaurantHttpAsyncTaskTextSearch", "valuesList.size() = " + valuesList.size());

		for(int i = 0; i < valuesList.size(); i++){
			Log.v("RestaurantHttpAsyncTaskTextSearch", "valuesList = " + valuesList.get(i).getName() + " "+  valuesList.get(i).getId());
		}
		/*listReturn = new ArrayList<RestaurantReference>();
		for(int i = 0; i < resMap.size() ; i++){
			listReturn.add(resMap.);
			Log.v("RestaurantHttpAsyncTaskTextSearch", "getName()" + listReturn.get(i).getName());
		}*/
		
		return valuesList;
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
