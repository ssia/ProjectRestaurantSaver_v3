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
	private double appLatitude;
	private double appLongitide;

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

	public double getAppLatitude() {
		return appLatitude;
	}

	public void setAppLatitude(double appLatitude) {
		this.appLatitude = appLatitude;
	}

	public double getAppLongitide() {
		return appLongitide;
	}

	public void setAppLongitide(double appLongitide) {
		this.appLongitide = appLongitide;
	}
	
}
