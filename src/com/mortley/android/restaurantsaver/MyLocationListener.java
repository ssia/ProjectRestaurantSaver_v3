package com.mortley.android.restaurantsaver;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MyLocationListener implements LocationListener{

	@Override
	public void onLocationChanged(Location loc) {
		loc.getLatitude();

		loc.getLongitude();

		String text = "My current location is: " +"Latitud = " + loc.getLatitude() +"Longitud = " + loc.getLongitude();
		//Log.v("location", text);

		//Toast.makeText( getApplicationContext(),text,Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

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
