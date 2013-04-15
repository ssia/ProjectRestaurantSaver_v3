package com.example.ProjectRestaurantSaver;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import com.example.ProjectRestaurantSaver.application.RestaurantApplication;
import com.example.ProjectRestaurantSaver.util.RestaurantHelper;
//import com.markupartist.android.widget.ActionBar;
//import com.markupartist.android.widget.ActionBar.Action;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class NearbyRestaurantActivity extends ListActivity implements OnClickListener{
	private Button refreshButton, searchButton;
	private double[] lastKnownLocation;
	ConcurrentHashMap<String, String> restaurantMap;
	//ActionBar action;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nearbyrestaurants);
		refreshButton = (Button)findViewById(R.id.reloadButton);
		refreshButton.setOnClickListener(this);
		
		restaurantMap =  new ConcurrentHashMap<String, String>();
		
		searchButton = (Button)findViewById(R.id.searchButton);
		searchButton.setOnClickListener(this);
		
		//checks network connectivity
		boolean checkConnection = isNetworkAvailable();
		if(!checkConnection){
			Toast.makeText(getApplicationContext(), "Check your Network Connectivity", Toast.LENGTH_LONG).show();
		}
		if(checkConnection){
			//sets current location parameters for the user
			lastKnownLocation = RestaurantHelper.getLastKnownLocation(this);
			System.out.println("network"+lastKnownLocation[0]+ lastKnownLocation[1]);
			RestaurantApplication application = (RestaurantApplication) this.getApplication();
			RestaurantAdapter restaurantAdapter = new RestaurantAdapter(this, R.layout.restaurantrow,  R.id.label, new ArrayList<RestaurantReference>());
			restaurantAdapter.setLastKnownLocation(lastKnownLocation);  
			//set a global variable for the RestaurantAdapter in the RestaurantApplication class.
			application.setRestaurantAdapter(restaurantAdapter);
			//Set the adapter first and then update it when the RestaurantHttpAsyncTask makes a web service call.
			setListAdapter(restaurantAdapter);
			//Make a webservice call in a different thread passing Keyword for URL as a string array.
			RestaurantHttpAsyncTask m_progressTask, m_progressTask1;
			//String[] keywords = {"", "american", "asian", "italian"};
			String[] keywords = {"indian"};
			m_progressTask = new RestaurantHttpAsyncTask(NearbyRestaurantActivity.this, keywords, restaurantMap);
			m_progressTask.setRestaurantAdapter(restaurantAdapter);
			m_progressTask.execute();
		}
	}

	@Override
	public void onClick(View v) {	
		//Refresh button helps to refresh the restaurant list on location change. Again it makes a call to the webservice using Async Task
		if(v.getId() == refreshButton.getId() ){

			//check network connectivity before refresh
			boolean checkConnection = isNetworkAvailable();
			if(!checkConnection){
				Toast.makeText(getApplicationContext(), "Check your Network Connectivity", Toast.LENGTH_LONG).show();
			}
			if(checkConnection){
				/*RestaurantAdapter restaurantAdapter = new RestaurantAdapter(this, R.layout.restaurantrow,  R.id.label, new ArrayList<RestaurantReference>());
				restaurantAdapter.setLastKnownLocation(lastKnownLocation);  
				setListAdapter(restaurantAdapter);
				RestaurantHttpAsyncTask m_progressTask;
				m_progressTask = new RestaurantHttpAsyncTask(NearbyRestaurantActivity.this);
				m_progressTask.setRestaurantAdapter(restaurantAdapter);
				m_progressTask.execute();*/
				
				RestaurantApplication application = (RestaurantApplication) this.getApplication();
				RestaurantAdapter restaurantAdapter = new RestaurantAdapter(this, R.layout.restaurantrow,  R.id.label, new ArrayList<RestaurantReference>());
				restaurantAdapter.setLastKnownLocation(lastKnownLocation);  
				//set a global variable for the RestaurantAdapter in the RestaurantApplication class.
				application.setRestaurantAdapter(restaurantAdapter);
				//Set the adapter first and then update it when the RestaurantHttpAsyncTask makes a web service call.
				setListAdapter(restaurantAdapter);
				//Make a webservice call in a different thread passing Keyword for URL as a string array.
				RestaurantHttpAsyncTask m_progressTask, m_progressTask1;
				String[] keywords = {"", "american", "asian", "italian","mexican"};
				m_progressTask = new RestaurantHttpAsyncTask(NearbyRestaurantActivity.this, keywords, restaurantMap);
				m_progressTask.setRestaurantAdapter(restaurantAdapter);
				m_progressTask.execute();
			}
		}
		
		if(v.getId() == searchButton.getId() ){
			
			Activity child = this;
			while(child.getParent() != null){
				System.out.println("#####child.getParent() = "+child.getParent());
				child = child.getParent();
			}
			System.out.println("#####Parent = "+ getParent());
			TabGroup1Activity parent = (TabGroup1Activity)getParent();
			
	        parent.startChildActivity("Search Restaurants", new Intent(parent, SearchActivity.class));

			//Intent searchIntent = new Intent(this, SearchActivity.class);
			//startActivity(searchIntent); 
		}

	}
	//Method to check network connectivity
	public boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager 
		= (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting()) {
			Log.d("network", "Network available:true");
			return true;
		} else {
			Log.d("network", "Network available:false");
			return false;
		}
	}
}