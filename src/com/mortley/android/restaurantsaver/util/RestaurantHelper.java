package com.mortley.android.restaurantsaver.util;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

public class RestaurantHelper {

	public static double[] getLastKnownLocation(Activity activity){
		double lat = 0.0;
		double lon = 0.0;
		LocationManager lm = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);    
		Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);  

		if(location == null){
			lat = 0.0;
			lon = 0.0;
		}
		else{
			//Log.v("Latitude", Double.toString(location.getLatitude()));
			//Log.v("Longitude", Double.toString(location.getLongitude()));

			lat = location.getLatitude();
			lon = location.getLongitude();
		}
		return new double[]{lat,lon};
	}
}
