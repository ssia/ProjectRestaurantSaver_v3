package com.example.ProjectRestaurantSaver;

import java.util.ArrayList;
import java.util.List;

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
	private Button publishButton;
	private ArrayList<MostVisitedResturantObject> listItems;
	private double[] lastKnownLocation;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mostvisited);

		lastKnownLocation = RestaurantHelper.getLastKnownLocation(this);
		publishButton = (Button)findViewById(R.id.mVisistedPublishButton);
		publishButton.setOnClickListener(this);
		MostVisitedAdapter listAdapter = new MostVisitedAdapter(this, R.layout.mostvisitedrow,  R.id.mVisited_name, this.fetchRestaurantsList());
		listAdapter.setLastKnownLocation(lastKnownLocation);
		listAdapter.notifyDataSetChanged();
		setListAdapter(listAdapter);

	}
	private List<MostVisitedResturantObject> fetchRestaurantsList() {
		// Get Restaurant name and no of times visited from database
		try{
			listItems = new ArrayList<MostVisitedResturantObject>();

			rd = DatabaseOpenHelper.getOrCreateInstance(getApplicationContext(), "restaurantSaver.db", null, 0);
			Cursor c = rd.get_restaurantName_timesVisited();
			@SuppressWarnings("rawtypes")
			ArrayList listOfRestaurantDetails = new ArrayList();

			if (c != null && c.getCount() > 0) {
				c.moveToFirst();
				int i = 0;
				while (!c.isAfterLast()) {
					String timesName = c.getString(c.getColumnIndex("RName"));
					int noOfTimes = c.getInt(c.getColumnIndex("NoOfTimes"));
					String id = c.getString(c.getColumnIndex("RID"));
					MostVisitedResturantObject obj = new MostVisitedResturantObject();
					obj.setName(timesName);
					obj.setId(id);// Set the Restaurant_Id for the MostVisitedResturantObject
					obj.setNoOfTimes(noOfTimes);
					listItems.add(obj);
					c.moveToNext();
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
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
	}
}
