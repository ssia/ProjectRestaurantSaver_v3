package com.example.ProjectRestaurantSaver;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

public class StatsActivity extends ListActivity{
	
	private DatabaseOpenHelper rd;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.statslayout);
		rd = DatabaseOpenHelper.getOrCreateInstance(this, "restaurantSaver.db", null, 0);

		Cursor c = rd.getStats();
		if (c.moveToFirst()){
				
			   do{
				  int nameColumn = c.getColumnIndex("RName");
				  int noOfTimesColumn = c.getColumnIndex("NoOfTimes");
				  int ratingColumn = c.getColumnIndex("Rrating");
			      noOfTimesColumn = 1;
			      ratingColumn =2;
			      String name = c.getString(nameColumn);
			      Log.v("Stats Data name= ", name);
			      String noOfTimes = c.getString(noOfTimesColumn);
			      if(noOfTimes != null){
			    	  	Log.v("Stats Data noOfTimes = ", noOfTimes);
			      }
			      String rating = c.getString(ratingColumn);
			      if(rating != null){
			    	  Log.v("Stats Data rating= ", rating);
			    	  Log.v("Stats Data = ", name+ noOfTimes + rating);
			      }
			   }while(c.moveToNext());
			}
			c.close();
		
	}
}
