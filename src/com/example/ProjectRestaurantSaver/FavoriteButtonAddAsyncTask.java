package com.example.ProjectRestaurantSaver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.util.Log;
import android.widget.TextView;

public class FavoriteButtonAddAsyncTask extends AsyncTask<Void, Void, Void> {
	private String address;
	private String contact;
	private String website;
	private String nameOfRes;
	private String resId;
	private Context context;
	private DatabaseOpenHelper rd;
	private boolean checkIfFav;
	private RestaurantReference item;

	public FavoriteButtonAddAsyncTask(Context context,	String address, String contact, String website, String nameOfRes, String resId, boolean checkIfFav, RestaurantReference item) {
		Log.v("FavoriteButtonAddAsyncTask", "HI There!!! :))");
		this.address = address;
		this.contact = contact;
		this.website = website;
		this.nameOfRes = nameOfRes;
		this.resId = resId;
		this.context = context;
		this.checkIfFav = checkIfFav;
		this.item = item;
	}

	

	@Override
	protected void onPreExecute() {

	}
	
	
	@Override
	protected  Void doInBackground(Void... params) {
		rd = DatabaseOpenHelper.getOrCreateInstance(context, "restaurantSaver.db", null, 0);
		Log.v("FavoriteButtonAsyncTask", "RestaurantAdapter, Restaurant Address and Phone No ="+nameOfRes+" resId="+resId +" address ="+address+" "+contact+ checkIfFav);
		Log.v("FavoriteButtonAsyncTask", "Item.Name"+item.getName()+" item.id="+item.getId());

		if(!checkIfFav) {//if the restaurant is not in favorites make it a favorite
			Cursor c = rd.check_restaurant_favorite_inDatabase(resId);
			Log.v("PlusButtonAsyncTask", "c = "+c);

			if (c != null) {
				c.moveToFirst();
				if (c.isFirst()) {
					int firstNameColumn = c.getColumnIndex("_id");	
					String firstName = c.getString(firstNameColumn);
					Log.v("FavoriteButtonAsyncTask", "firstName= "+firstName);

					int favNameColumn = c.getColumnIndex("RFavorite");

					String favName = c.getString(favNameColumn);
					Log.v("FavoriteButtonAsyncTask", "favName= "+favName);

					c.moveToFirst();
					rd.favTimesInDatabase(resId);
				} 
				else{
					rd.insert_fav(resId, nameOfRes, address, contact, website);
				}
			}

		} else {//if the restaurant is already marked as a Favorite, remove it from Favorite's.
		
			Cursor c = rd.check_restaurant_visited_inDatabase(resId);
			try {
				boolean exists = c.moveToFirst();
				Log.v("FavoriteButtonAsyncTask", "exists= "+exists);
				if(exists) {
					int numVisited = c.getInt(c.getColumnIndex("NoOfTimes"));
					Log.v("FavoriteButtonAsyncTask", "numVisited= "+numVisited);

					if(numVisited > 0) {
						rd.favTimesInDatabase(resId, false);
						rd.removeRratingFromRestaurantRow(resId);
					} else {
						rd.deleteRowInList(resId);
					}

					//item.setInFavorites(false);
				}
			} finally {
				c.close();
			}
		}
		return null;
	}


	protected void onPostExecute() {
		
	}


}
