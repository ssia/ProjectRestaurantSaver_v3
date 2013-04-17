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
import android.widget.Toast;

public class FavoriteRestaurantActivity extends ListActivity implements OnClickListener{
	private DatabaseOpenHelper rd;
	private Button publishButton;
	private ArrayList<FavoriteRestaurantObject> favListItems;
	private double[] lastKnownLocation;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.favoriterestaurants);

		lastKnownLocation = RestaurantHelper.getLastKnownLocation(this);
		publishButton = (Button)findViewById(R.id.favPublishButton);
		publishButton.setOnClickListener(this);
		@SuppressWarnings("rawtypes")
		FavoriteRestaurantAdapter listAdapter = new FavoriteRestaurantAdapter(this, R.layout.favoriterow,  R.id.favName, this.fetchFavRestaurantsList());
		listAdapter.setLastKnownLocation(lastKnownLocation);
		listAdapter.notifyDataSetChanged();
		setListAdapter(listAdapter);
	}

	private List<FavoriteRestaurantObject> fetchFavRestaurantsList() {
		// Get Restaurant name and no of times visited from database
		try{
			favListItems = new ArrayList<FavoriteRestaurantObject>();

			rd = DatabaseOpenHelper.getOrCreateInstance(getApplicationContext(), "restaurantSaver.db", null, 0);
			Cursor c = rd.getFavoriteRestaurantNames();

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
						obj.setRating(Integer.parseInt(ratings));
					else obj.setRating(0);
					favListItems.add(obj);
					c.moveToNext();
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return favListItems;
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == publishButton.getId()){
			String restaurantList = " ";
			int j = 1;
			Intent postOnFacebookWallIntent = new Intent(this, ShareOnFacebook.class);
			rd = DatabaseOpenHelper.getOrCreateInstance(getApplicationContext(), "restaurantSaver.db", null, 0);
			Cursor c = rd.getTopTenFavoriteRestaurantNames();
			if (c != null) {
				c.moveToFirst();
				while (!c.isAfterLast()) {
					String resName = c.getString(c.getColumnIndex("RName"));
					String resRating = c.getString(c.getColumnIndex("Rrating"));
					if (resRating == null)
						resRating = "0";
					restaurantList= restaurantList+ j+ "."+ resName+" (Rating-"+ resRating+")"+"\n";
					j++;
					c.moveToNext();
				}
			}
			String postMessage = "My Top Ten Favorite Restaurants are \n\n"+restaurantList;
			//Log.v("facebook message", postMessage);
			postOnFacebookWallIntent.putExtra("facebookMessage", postMessage);
			startActivity(postOnFacebookWallIntent);		
		}		
	}
}
