package com.mortley.android.restaurantsaver;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

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

		if(!checkIfFav) {//if the restaurant is not in favorites make it a favorite
			Cursor c = rd.check_restaurant_favorite_inDatabase(resId);

			if (c != null) {
				c.moveToFirst();
				if (c.isFirst()) {
					int firstNameColumn = c.getColumnIndex("_id");	
					String firstName = c.getString(firstNameColumn);
					int favNameColumn = c.getColumnIndex("RFavorite");
					String favName = c.getString(favNameColumn);

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
				if(exists) {
					int numVisited = c.getInt(c.getColumnIndex("NoOfTimes"));
					if(numVisited > 0) {
						rd.favTimesInDatabase(resId, false);
						rd.removeRratingFromRestaurantRow(resId);
					} else {
						rd.deleteRowInList(resId);
					}
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
