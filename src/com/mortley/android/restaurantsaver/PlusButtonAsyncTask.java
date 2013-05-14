package com.mortley.android.restaurantsaver;


import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

public class PlusButtonAsyncTask extends AsyncTask<Void, Void, Void> {
	private String address;
	private String contact;
	private String website;
	private String nameOfRes;
	private String resId;
	private Context context;
	private DatabaseOpenHelper rd;

	public PlusButtonAsyncTask(Context context,	String address, String contact, String website, String nameOfRes, String resId) {
		this.address = address;
		this.contact = contact;
		this.website = website;
		this.nameOfRes = nameOfRes;
		this.resId = resId;
		this.context = context;
	}

	

	@Override
	protected void onPreExecute() {

	}

	@Override
	protected Void doInBackground(Void... params) {
		rd = DatabaseOpenHelper.getOrCreateInstance(context, "restaurantSaver.db", null, 0);
		Cursor c = rd.check_restaurant_visited_inDatabase(resId);
		try{
				
			if (c != null) {
				c.moveToFirst();
				if (c.isFirst()) {
					int firstNameColumn = c.getColumnIndex("_id");
					String firstName = c.getString(firstNameColumn);								
					int timesNameColumn = c.getColumnIndex("NoOfTimes");
					String timesName = c.getString(timesNameColumn);
					c.moveToFirst();
					int num = Integer.parseInt(timesName);
					num = num + 1;
					rd.updateTimesInDatabase(resId, num); //???if the restaurant was already visited in the past increase the number of visits by 1
				}
				else{ //create a new entry for the restaurant

					rd.insert_mostVisited(resId, nameOfRes, 1, address, contact, website);
				}
			}	
		} 
		finally{
			c.close();
		}
		return null;
	}


	protected void onPostExecute() {
		
	}


}
