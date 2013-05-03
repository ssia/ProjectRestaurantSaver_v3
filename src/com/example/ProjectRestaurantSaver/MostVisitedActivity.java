package com.example.ProjectRestaurantSaver;

import java.util.ArrayList;
import java.util.List;

import com.example.ProjectRestaurantSaver.application.RestaurantApplication;
import com.example.ProjectRestaurantSaver.util.RestaurantHelper;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;

public class MostVisitedActivity  extends ListActivity implements OnClickListener{
	private DatabaseOpenHelper rd;
	private Button publishButton, sortByName, sortByTimes;
	private ArrayList<MostVisitedResturantObject> listItems;
	private double[] lastKnownLocation;
	private MostVisitedAdapter listAdapter;
	private int timesGlobalVar, nameGlobalVar;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mostvisited);
		timesGlobalVar = 0; nameGlobalVar = 0;
		lastKnownLocation = RestaurantHelper.getLastKnownLocation(this);
		publishButton = (Button)findViewById(R.id.mVisistedPublishButton);
		publishButton.setOnClickListener(this);
		sortByName = (Button)findViewById(R.id.mVisitedSortByName);
		sortByName.setOnClickListener(this);
		sortByTimes = (Button)findViewById(R.id.mVisitedSortByTimes);
		sortByTimes.setOnClickListener(this);
		
		RestaurantApplication application1 = (RestaurantApplication) this.getApplication();
		listAdapter = new MostVisitedAdapter(this, R.layout.mostvisitedrow,  R.id.mVisited_name, this.fetchRestaurantsList());
		listAdapter.setLastKnownLocation(lastKnownLocation);
		listAdapter.notifyDataSetChanged();
		this.setListAdapter(listAdapter);
		application1.setmVisitedActivity(this);

		setListAdapter1(listAdapter);

	}
	private List<MostVisitedResturantObject> fetchRestaurantsList() {
		// Get Restaurant name and no of times visited from database
		listItems = new ArrayList<MostVisitedResturantObject>();
		listItems = getRestaurantsSortedbyTimes(listItems);
		return listItems;
	}
	private List<MostVisitedResturantObject> fetchRestaurantsListByName() {
		// Get Restaurant name and no of times visited from database
		listItems = new ArrayList<MostVisitedResturantObject>();
		listItems = getRestaurantsSortedbyName(listItems);
		return listItems;
	}
	private ArrayList<MostVisitedResturantObject> getRestaurantsSortedbyTimes(
			ArrayList<MostVisitedResturantObject> listItems2) {
		try{
			rd = DatabaseOpenHelper.getOrCreateInstance(getApplicationContext(), "restaurantSaver.db", null, 0);
			Cursor c;
			if(timesGlobalVar == 0){
				c = rd.get_restaurantName_timesVisitedAsc();
				timesGlobalVar = 1;
			}
			else{
				c = rd.get_restaurantName_timesVisitedDesc();
				timesGlobalVar = 0;
			}
			
			if (c != null && c.getCount() > 0) {
				c.moveToFirst();
				while (!c.isAfterLast()) {
					String timesName = c.getString(c.getColumnIndex("RName"));
					int noOfTimes = c.getInt(c.getColumnIndex("NoOfTimes"));
					String id = c.getString(c.getColumnIndex("_id"));
					MostVisitedResturantObject obj = new MostVisitedResturantObject();
					//Log.v("MostVisited", timesName);
					obj.setName(timesName);
					obj.setId(id);// Set the Restaurant_Id for the MostVisitedResturantObject
					obj.setNoOfTimes(noOfTimes);
					listItems.add(obj);
					c.moveToNext();
				}
			}
		}catch(Throwable th){
			th.printStackTrace();
			throw new RuntimeException("Query for restaurants sorted by number of times failed", th);

		}
		return listItems;

	}
	
	@SuppressWarnings("unused")
	private ArrayList<MostVisitedResturantObject> getRestaurantsSortedbyName(
			ArrayList<MostVisitedResturantObject> listItems2) {
		try{
			rd = DatabaseOpenHelper.getOrCreateInstance(getApplicationContext(), "restaurantSaver.db", null, 0);
			Cursor c;
			if(nameGlobalVar == 0){
				c = rd.sortVisitedRestaurantsByNameAsc();
				nameGlobalVar = 1;
			}
			else{
				c = rd.sortVisitedRestaurantsByNameDesc();
				nameGlobalVar = 0;
			}
	
			if (c != null && c.getCount() > 0) {
				c.moveToFirst();
				while (!c.isAfterLast()) {
					String timesName = c.getString(c.getColumnIndex("RName"));
					int noOfTimes = c.getInt(c.getColumnIndex("NoOfTimes"));
					String id = c.getString(c.getColumnIndex("_id"));
					MostVisitedResturantObject obj = new MostVisitedResturantObject();
					obj.setName(timesName);
					//Log.v("MostVisited By Name", timesName);

					obj.setId(id);// Set the Restaurant_Id for the MostVisitedResturantObject
					obj.setNoOfTimes(noOfTimes);
					listItems.add(obj);
					c.moveToNext();
				}
			}
		}catch(Throwable th){
			th.printStackTrace();
			throw new RuntimeException("Query for restaurants sorted by number of times failed", th);

		}
		return listItems;

	}
	@Override
	public void onClick(View v) {
		if(v.getId() == publishButton.getId()){
			String restaurantList = " ";
			int j = 1;
			Intent postOnFacebookWallIntent = new Intent(this, ShareOnFacebook.class);
			rd = DatabaseOpenHelper.getOrCreateInstance(getApplicationContext(), "restaurantSaver.db", null, 0);
			Cursor c = rd.getTopTenMostVisitedRestaurantNames();
			if (c != null) {
				c.moveToFirst();
				int i = 0;
				while (!c.isAfterLast()) {
					String resName = c.getString(c.getColumnIndex("RName"));
					restaurantList= restaurantList+ j+ "."+ resName+"\n";
					j++;
					c.moveToNext();
				}
			}
			String postMessage = "My Top Ten Most Visited Restaurants are \n\n"+restaurantList;
			//Log.v("facebook message", postMessage);
			postOnFacebookWallIntent.putExtra("facebookMessage", postMessage);
			startActivity(postOnFacebookWallIntent);		
		}
		
		if(v.getId() == sortByName.getId()){
			listAdapter = new MostVisitedAdapter(this, R.layout.mostvisitedrow,  R.id.mVisited_name, this.fetchRestaurantsListByName());
			listAdapter.setLastKnownLocation(lastKnownLocation);
			listAdapter.notifyDataSetChanged();
			setListAdapter(listAdapter);
		}
	
		if(v.getId() == sortByTimes.getId()){
			listAdapter = new MostVisitedAdapter(this, R.layout.mostvisitedrow,  R.id.mVisited_name, this.fetchRestaurantsList());
			listAdapter.setLastKnownLocation(lastKnownLocation);
			listAdapter.notifyDataSetChanged();
			setListAdapter(listAdapter);
		}
	}
	public MostVisitedAdapter getListAdapter() {
		return listAdapter;
	}
	public void setListAdapter1(MostVisitedAdapter listAdapter1) {
		this.listAdapter = listAdapter1;
		Log.v("Most Visted Activity", ""+listAdapter1);
	}
}
