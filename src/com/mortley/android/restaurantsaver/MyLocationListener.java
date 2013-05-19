package com.mortley.android.restaurantsaver;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MyLocationListener implements LocationListener{
	LocationManager locManager;

	@Override
	public void onLocationChanged(Location loc) {

		if (loc != null) {
			

			loc.getLatitude();

			loc.getLongitude();

			String text = "My current location is: " +"Latitud = " + loc.getLatitude() +"Longitud = " + loc.getLongitude();
			Log.v("locationManager ", text);

		}
	}

	@Override
	public void onProviderDisabled(String arg0) {
		

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

}
