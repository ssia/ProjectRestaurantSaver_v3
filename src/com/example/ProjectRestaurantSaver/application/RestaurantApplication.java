package com.example.ProjectRestaurantSaver.application;

//Class to store references globally in the application
import android.app.Application;

import com.example.ProjectRestaurantSaver.RestaurantAdapter;

public class RestaurantApplication extends Application{
	private RestaurantAdapter restaurantAdapter;

	public void setRestaurantAdapter(RestaurantAdapter restaurantAdapter) {
		this.restaurantAdapter = restaurantAdapter;
	}

	public RestaurantAdapter getRestaurantAdapter() {
		return restaurantAdapter;
	}
	
}
