package com.mortley.android.restaurantsaver;

import android.app.ListActivity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

public class StatsActivity extends ListActivity{

	private DatabaseOpenHelper rd;
	private RatingBar rBar;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.statslayout);
		rd = DatabaseOpenHelper.getOrCreateInstance(this, "restaurantSaver.db", null, 0);
		ListView list = (ListView) findViewById(R.id.list);
		Cursor c = null;
		try {
			c = rd.getStats();
		} catch(Throwable th) {
			th.printStackTrace();
			throw new RuntimeException("Query for stats failed", th);
		}


		if (c.moveToFirst()){

			do{
				int nameColumn = c.getColumnIndex("RName");
				int noOfTimesColumn = c.getColumnIndex("NoOfTimes");
				int ratingColumn = c.getColumnIndex("Rrating");
				noOfTimesColumn = 1;
				ratingColumn =2;
				String name = c.getString(nameColumn);
				String noOfTimes = c.getString(noOfTimesColumn);

				String rating = c.getString(ratingColumn);
				if(rating != null){
					//Log.v("Stats Data rating= ", rating);
					//Log.v("Stats Data = ", name+ noOfTimes + rating);
				}
			}while(c.moveToNext());
		}
		if(c != null){
			c.moveToFirst();
			setListAdapter(new StatListAdapter(this, c));
		}
	}


	private class StatListAdapter extends ResourceCursorAdapter {

		public StatListAdapter(Context context, Cursor cursor) {
			super(context, R.layout.statlistrow, cursor);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {

			int nameColumn = cursor.getColumnIndex("RName");
			int noOfTimesColumn = cursor.getColumnIndex("NoOfTimes");
			int ratingColumn = cursor.getColumnIndex("Rrating");
			rBar= (RatingBar) view.findViewById(R.id.statRatingBar);
			rBar.setFocusable(false);
			rBar.setStepSize((float)0.5);
			String name = cursor.getString(nameColumn);
			String noOfTimes = cursor.getString(noOfTimesColumn);
			String rating = cursor.getString(ratingColumn);

			//Log.v("StatsActivity", "name noOfTmes rating = "+name+ " "+noOfTimes+ " "+rating);
			TextView restaurantName = (TextView) view.findViewById(R.id.statRestaurantName);
			restaurantName.setText(name);

			TextView restaurantRatingAve = (TextView) view.findViewById(R.id.statRestaurantTimes);
			if(noOfTimes != null && (Integer.parseInt(noOfTimes)) == 1){
				restaurantRatingAve.setText(noOfTimes + " Visit");
			}
			else
				restaurantRatingAve.setText(noOfTimes + " Visits");
			//Log.v("RATING", ""+(Float.valueOf(rating)));
			rBar.setRating((Float.valueOf(rating)));	        	

		}
	}

}
