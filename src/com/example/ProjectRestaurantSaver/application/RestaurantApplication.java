package com.example.ProjectRestaurantSaver.application;

//Class to store references globally in the application
import android.app.Application;
import android.widget.ListAdapter;

import com.example.ProjectRestaurantSaver.MostVisitedActivity;
import com.example.ProjectRestaurantSaver.MostVisitedAdapter;
import com.example.ProjectRestaurantSaver.RestaurantAdapter;

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
