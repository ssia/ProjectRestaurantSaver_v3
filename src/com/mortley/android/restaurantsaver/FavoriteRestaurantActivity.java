package com.mortley.android.restaurantsaver;

import java.util.ArrayList;
import java.util.List;

import com.mortley.android.restaurantsaver.util.RestaurantHelper;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class FavoriteRestaurantActivity extends ListActivity implements OnClickListener, LocationListener{
	private DatabaseOpenHelper rd;
	private Button publishButton,sortByRating, sortByName;
	private ArrayList<FavoriteRestaurantObject> favListItems;
	private double[] lastKnownLocation;
	private LocationManager locManager;
	private FavoriteRestaurantAdapter listAdapter;
	private int ratingGlobalVar, nameGlobalVar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.favoriterestaurants);
		ratingGlobalVar = 0; nameGlobalVar = 0;
		lastKnownLocation = RestaurantHelper.getLastKnownLocation(this);
		publishButton = (Button)findViewById(R.id.favPublishButton);
		publishButton.setOnClickListener(this);
		sortByRating = (Button)findViewById(R.id.fVisitedSortByRating);
		sortByRating.setOnClickListener(this);
		sortByName = (Button)findViewById(R.id.fVisitedSortByName);
		sortByName.setOnClickListener(this);
		
		locManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);//request location updates
		locManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 1000, 100, this);
		
		listAdapter = new FavoriteRestaurantAdapter(this, R.layout.favoriterow,  R.id.favName, this.fetchFavRestaurantsList());
		listAdapter.setLastKnownLocation(lastKnownLocation);
		listAdapter.notifyDataSetChanged();
		setListAdapter(listAdapter);
	}

	private List<FavoriteRestaurantObject> fetchFavRestaurantsList() {
		// Get Restaurant name and rating visited from database sorted by rating
		favListItems = new ArrayList<FavoriteRestaurantObject>();
		favListItems = sortRestaurantsByRating(favListItems);
		return favListItems;
	}
	private ArrayList<FavoriteRestaurantObject> sortRestaurantsByRating(
			ArrayList<FavoriteRestaurantObject> favListItems2) {
		try{
			rd = DatabaseOpenHelper.getOrCreateInstance(getApplicationContext(), "restaurantSaver.db", null, 0);
			Cursor c;
			if( ratingGlobalVar == 0){
				c = rd.get_restaurantNameRatingDesc();
				ratingGlobalVar = 1;
			}
			else{
				c = rd.get_restaurantNameRatingAsc();
				ratingGlobalVar = 0;
			}

			if (c != null) {
				c.moveToFirst();
				int i = 0;
				while (!c.isAfterLast()) {
					String favName = c.getString(c.getColumnIndex("RName"));
					String ratings = c.getString(c.getColumnIndex("Rrating"));
					String id = c.getString(c.getColumnIndex("_id"));// Set the Restaurant_Id for the FavoriteRestaurantObject
					FavoriteRestaurantObject obj = new FavoriteRestaurantObject();
					obj.setName(favName);
					obj.setId(id);
					if(ratings != null)
						obj.setRating(Float.parseFloat(ratings));
					else obj.setRating(0);
					favListItems.add(obj);
					c.moveToNext();
				}
			}
		}catch(Throwable th){
			th.printStackTrace();
			throw new RuntimeException("Query for favorite restaurants sorted by rating failed", th);

		}
		return favListItems;
	}

	private List<FavoriteRestaurantObject> fetchRestaurantsListByName() {
		// Get Restaurant name and rating visited from database sorted by name
		favListItems = new ArrayList<FavoriteRestaurantObject>();
		favListItems = sortRestaurantsByName(favListItems);
		return favListItems;
	}

	private ArrayList<FavoriteRestaurantObject> sortRestaurantsByName(
			ArrayList<FavoriteRestaurantObject> favListItems2) {
		try{
			rd = DatabaseOpenHelper.getOrCreateInstance(getApplicationContext(), "restaurantSaver.db", null, 0);
			Cursor c;
			if(nameGlobalVar == 0){
				c = rd.sortFavRestaurantsByNameAsc();
				nameGlobalVar = 1;
			}
			else{
				c = rd.sortFavRestaurantsByNameDesc();
				nameGlobalVar = 0;
			}

			if (c != null) {
				c.moveToFirst();
				int i = 0;
				while (!c.isAfterLast()) {
					String favName = c.getString(c.getColumnIndex("RName"));
					String ratings = c.getString(c.getColumnIndex("Rrating"));
					String id = c.getString(c.getColumnIndex("_id"));
					FavoriteRestaurantObject obj = new FavoriteRestaurantObject();
					obj.setName(favName);
					obj.setId(id);
					if(ratings != null)
						obj.setRating(Float.parseFloat(ratings));
					else obj.setRating(0);
					favListItems.add(obj);
					c.moveToNext();
				}
			}
		}catch(Throwable th){
			th.printStackTrace();
			throw new RuntimeException("Query for favorite restaurants sorted by name failed", th);

		}
		return favListItems;

	}


	@Override
	public void onClick(View v) {
		if(v.getId() == publishButton.getId()){
			String restaurantList = " ";
			String postMessage = "";
			int j = 1;
			Intent postOnFacebookWallIntent = new Intent(this, ShareOnFacebook.class);
			rd = DatabaseOpenHelper.getOrCreateInstance(getApplicationContext(), "restaurantSaver.db", null, 0);
			Cursor c = rd.getTopTenFavoriteRestaurantNames();
			if (c != null) {
				String resName = "";
				c.moveToFirst();
				while (!c.isAfterLast()) {
				    resName = c.getString(c.getColumnIndex("RName"));
				
					restaurantList= restaurantList+ j+ "."+ resName+"\n";
					j++;
					c.moveToNext();
				}
				if(resName.length() < 1){
					postMessage = "No Restaurants to post";
				}
				else 
					postMessage = "My Favorite Restaurants are \n\n"+restaurantList;
			}
			
			postOnFacebookWallIntent.putExtra("facebookMessage", postMessage);
			startActivity(postOnFacebookWallIntent);		
		}	

		if(v.getId() == sortByName.getId()){
			listAdapter = new FavoriteRestaurantAdapter(this, R.layout.favoriterow,  R.id.favName, this.fetchRestaurantsListByName());
			listAdapter.setLastKnownLocation(lastKnownLocation);
			listAdapter.notifyDataSetChanged();
			setListAdapter(listAdapter);
		}

		if(v.getId() == sortByRating.getId()){
			listAdapter = new FavoriteRestaurantAdapter(this, R.layout.favoriterow,  R.id.favName, this.fetchFavRestaurantsList());
			listAdapter.setLastKnownLocation(lastKnownLocation);
			listAdapter.notifyDataSetChanged();
			setListAdapter(listAdapter);
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 100, this); 
		//Log.v("FavoriteRestaurantActivity", "In OnResume()");

	}

	@Override
	protected void onPause() {
		super.onPause();
		locManager.removeUpdates(this); 
		//Log.v("FavoriteRestaurantActivity", "In onPause()");

	}
}