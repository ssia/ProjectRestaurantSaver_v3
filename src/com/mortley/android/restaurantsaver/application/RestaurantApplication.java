package com.mortley.android.restaurantsaver.application;

//Class to store references globally in the application
import android.app.Application;
import android.widget.ListAdapter;

import com.mortley.android.restaurantsaver.MostVisitedActivity;
import com.mortley.android.restaurantsaver.MostVisitedAdapter;
import com.mortley.android.restaurantsaver.RestaurantAdapter;

public class RestaurantApplication extends Application{
	private RestaurantAdapter restaurantAdapter;
	private MostVisitedActivity mVisitedActivity;


	public void setRestaurantAdapter(RestaurantAdapter restaurantAdapter) {
		this.restaurantAdapter = restaurantAdapter;
	}

	public RestaurantAdapter getRestaurantAdapter() {
		return restaurantAdapter;
	}

	public MostVisitedActivity getmVisitedActivity() {
		return mVisitedActivity;
	}

	public void setmVisitedActivity(MostVisitedActivity mVisitedActivity) {
		this.mVisitedActivity = mVisitedActivity;
	}
	
}
