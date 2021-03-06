package com.mortley.android.restaurantsaver;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class RestaurantAdapter extends ArrayAdapter<RestaurantReference> {
	private List<RestaurantReference> dataObjects;
	private DatabaseOpenHelper rd;
	private double[] lastKnownLocation;	
	private RestaurantAsyncTaskFetchDetails detailsAsyncTask;
	private double lat = 0.0;
	private double lon = 0.0;

	private String currentaddress;

	/*
	 * The two main inputs to the adapter are the data source which is the list of items and the template to represent
	 * a row in the list. 
	 */
	public RestaurantAdapter(Context context, int resource,
			int textViewResourceId, List<RestaurantReference> objects) {
		super(context, resource, textViewResourceId, objects);	
		dataObjects = objects;
		calculateCurrentAddress(null );/* we dont know the location yet */
	}

	// We are not calling this at this point to conserve the battery life.
/*	protected void setLocationChangeListener() {

		LocationManager lm = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

		LocationListener locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				RestaurantAdapter.this.calculateCurrentAddress(location);
				//Log.v("RestaurantAdapter", "onLocationChanged: current address 1 = "+ currentaddress);
			}

			public void onProviderEnabled(String provider) {}

			public void onProviderDisabled(String provider) {}

			@Override
			public void onStatusChanged(String provider, int status,Bundle extras) {

			}
		};

		lm.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, locationListener);
	}*/

	protected void calculateCurrentAddress(Location location) {
		lat = 0.0;
		lon = 0.0;
		LocationManager lm = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);    

		if(location == null) {
			location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);  
		}
		if(location == null) {
			location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);  
		}
		if(location == null){
			lat = 0.0;
			lon = 0.0;
		}
		else{

			lat = location.getLatitude();
			lon = location.getLongitude();
			//Log.v("RestaurantAdapter", "lat = "+lat);
			//Log.v("RestaurantAdapter", "lon"+lon); 
			currentaddress = findAddressfromLatLng(lat, lon);
			if(currentaddress == null) {
				currentaddress = "";
			}
			currentaddress = currentaddress.replaceAll("(\\r|\\n)", "");
		}

		//Log.v("RestaurantAdapter", "onLocationChanged: current address 2 = "+ currentaddress);

	}


	/*
	 * (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 * Overriding getView to customize how data is displayed in every row.
	 */
	@SuppressWarnings("unused")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;//Check to see if rows are already present for re-use or if new row needs to be created
		if (v == null) {
			LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.restaurantrow, parent, false);//create instance of the template for the particular row represented by position


		}
		ImageButton addButton = (ImageButton) v.findViewById(R.id.add_button);
		ImageButton dialButton = (ImageButton) v.findViewById(R.id.contactButton);
		ImageButton directionsButton = (ImageButton) v.findViewById(R.id.directionsButton);
		ImageButton favButton = (ImageButton) v.findViewById(R.id.fav_button);

		//check if the restaurant is already marked as favorite
		RestaurantReference ref = dataObjects.get(position);

		if (ref != null) {
			TextView tt = (TextView) v.findViewById(R.id.label);
			TextView addressLabel = (TextView) v.findViewById(R.id.address_label);

			if (ref.getName() != null) {
				tt.setText(ref.getName());                            
			}
			if(ref.getAddress() != null){
				addressLabel.setText(ref.getAddress());
			}

			//String destinationAddress = findAddressfromLatLng(ref.getLatitude(), ref.getLongitude());
			String destinationAddress = ref.getAddress();
			//String destinationAddress = "castro street mountain view";
			destinationAddress = destinationAddress.replaceAll("(\\r|\\n)", "");
			//Log.v("destination address=", destinationAddress);


			//currentaddress = "401%20Castro%20St,%20Mountain%20View,%20CA%2094041,%20USA";
			//Log.v("destination address=", destinationAddress);
			String distanceURL = null;
			if(currentaddress != null) {
				distanceURL = "http://maps.googleapis.com/maps/api/distancematrix/json?origins="+currentaddress+"&destinations="+destinationAddress+"&mode=driving&sensor=false&units=imperial";
				distanceURL =  distanceURL.replaceAll(" ", "%20");
			}
			//String distanceURL = "http://maps.googleapis.com/maps/api/distancematrix/json?origins=401%20Castro%20St,%20Mountain%20View,%20CA%2094041,%20USA&destinations=800%20California%20StMountain%20View,%20CA%2094041&mode=driving&sensor=false&units=imperial";
			//Log.v("distanceURL=", distanceURL);
			RestaurantAsyncTaskGetDistance getDistanceTask;
			TextView distanceLabel = (TextView) v.findViewById(R.id.distance_label);

			getDistanceTask = new RestaurantAsyncTaskGetDistance(this, distanceURL, distanceLabel);
			getDistanceTask.setRestaurantAdapter(this);
			getDistanceTask.execute();

			rd = DatabaseOpenHelper.getOrCreateInstance(getContext(), "restaurantSaver.db", null, 0);
			Cursor c = rd.check_restaurant_favorite_inDatabase(ref.getId());
			int rFavValue = 0;
			try{
				c.moveToFirst();

				if (c.moveToFirst()) {
					int rFavorite = c.getColumnIndex("RFavorite");
					rFavValue = c.getInt(rFavorite);
				}
			} finally{
				c.close();
			}
			//If the restaurant is marked as favorite, then display a green star or else display a grey star

			if(rFavValue == 0 ) {
				favButton.setImageResource(R.drawable.star);
				ref.setInFavorites(false);
			} else {
				favButton.setImageResource(R.drawable.star_green);
				ref.setInFavorites(true);
			}

			/*
			 * If the user clicks the +1 button against a restaurant, the method checks if the restaurant has been added to the
			 * "Most Visited" list before and accordingly updates the NoOfTimes column against each restaurant selected by the user.
			 */
			addButton.setOnClickListener(new ButtonClickListener(ref) {
				@Override
				public void onClick(View v) {
					Toast.makeText(getContext(), item.getName() + " added to Visits", Toast.LENGTH_SHORT).show();
					String nameOfRes = item.getName();
					String resId = item.getId();
					Context context = getContext();

					RestaurantDetails details = fetchRestaurantDetails(item);
					String address = details.getAddress();
					String contact = details.getPhoneNumber();		
					String website = details.getWebsite();//adding website link
					if(website == null)
						website = "";
					//Log.v("Restaurant Adapter", "website= "+website);
					PlusButtonAsyncTask plusAsync = new PlusButtonAsyncTask(context, address, contact, website, nameOfRes, resId);
					plusAsync.execute();

				}
			}); 

			dialButton.setOnClickListener(new ButtonClickListener(ref){
				@Override
				public void onClick(View v){

					RestaurantDetails details = fetchRestaurantDetails(item);
					String contact = details.getPhoneNumber();
					//Log.v("dialButton Phone Number", ""+contact);
					Context context = getContext();
					Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+contact));
					context.startActivity(intent);
				}
			});

			/*
			 * If the user clicks the +1 button against a restaurant, the method checks if the restaurant has been added to the
			 * "Favorite Restaurant" list before and accordingly updates the RFavorite column against each restaurant selected by the user.
			 */
			favButton.setOnClickListener(new ButtonClickListener(ref) {

				@Override
				public void onClick(View v) {
					String nameOfRes = item.getName();
					String resId = item.getId();
					RestaurantDetails details = fetchRestaurantDetails(item);
					String address = details.getAddress();
					String contact = details.getPhoneNumber();
					String website = details.getWebsite();//adding website link
					if(website == null)
						website = "";
					ImageButton btn = (ImageButton)v;

					rd = DatabaseOpenHelper.getOrCreateInstance(getContext(), "restaurantSaver.db", null, 0);
					boolean checkIfFav = item.getInFavorites();

					//Log.v("RestaurantAdapter", "item.getInFavorites()"+item.getInFavorites());

					if(!item.getInFavorites()) {//if the restaurant is not in favorites make it a favorite
						//Log.v("RestaurantAdapter, Restaurant Address and Phone No =", address+" "+contact);
						btn.setImageResource((R.drawable.star_green));
						item.setInFavorites(true);
						Toast.makeText(getContext(), item.getName() +" added to Favorites", Toast.LENGTH_SHORT).show();


					} else {//if the restaurant is already marked as a Favorite, remove it from Favorite's.
						btn.setImageResource((R.drawable.star));
						Toast.makeText(getContext(), item.getName() +" removed from Favorites", Toast.LENGTH_SHORT).show();
						item.setInFavorites(false);

					}
					Context context = getContext();
					FavoriteButtonAddAsyncTask favAsyncTask = new FavoriteButtonAddAsyncTask(context, address, contact, website, nameOfRes, resId, checkIfFav, item);
					favAsyncTask.execute();
				}	

			});

			/* Method to be implemented if the Direction button is clicked by the user. The method queries the database to
			  get the direction to the restaurant in google maps.*/

			directionsButton.setOnClickListener(new ButtonClickListener(ref){
				LocationObject location = new LocationObject();
				@SuppressWarnings("unused")
				@Override
				public void onClick(View arg0) {
					

					rd = DatabaseOpenHelper.getOrCreateInstance(getContext(), "restaurantSaver.db", null, 0);
					RestaurantDetails details = fetchRestaurantDetails(item);
					String mVisitedAddress = details.getAddress();
					//Log.v("RestaurantAdapter", "mVisitedAddress"+ mVisitedAddress);
					String newString = mVisitedAddress.replace(",", "");
					newString = newString.replace(" ", "+");
					//List<Address> foundGeocode = null;
					JSONObject loc = getLocationInfo(newString);
					//Log.v("RestaurantAdapter","loc.toString()"+loc.toString());
					LocationObject p = getGeoPoint(loc);
					/*double lat = p.getLat();
					double lon = p.getLng();*/
					double destLat = p.getLat();
					double destLon = p.getLng();
					//Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr="+lastKnownLocation[0] +","+lastKnownLocation[1]+"&daddr="+lat+","+lon));
					//Log.v("RestaurantAdapter" , "Lat Long =" + lat +lon);
					//Log.v("RestaurantAdapter" , "destLat destLon =" + destLat +destLon);

					Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr="+lat+","+lon+"&daddr="+destLat+","+destLon));

					Context context = getContext();
					context.startActivity(intent);
				}
			});
		}
		return v;
	}


	private abstract class ButtonClickListener implements OnClickListener {

		protected RestaurantReference item;
		public ButtonClickListener(RestaurantReference item) {
			this.item = item;
		}
	}

	public RestaurantDetails fetchRestaurantDetails(RestaurantReference object) {

		RestaurantDetails res_detail = new RestaurantDetails();
		//Log.v("Restaurant Adapter", "restaurant adapter = "+this);
		detailsAsyncTask = new RestaurantAsyncTaskFetchDetails(RestaurantAdapter.this, object);
		try {
			res_detail = detailsAsyncTask.execute().get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Log.v("Restaurant Adapter", " Restaurant Address,PhNo,Lat, Lon, Website frm JSON ="+res_detail.getAddress() + "  "+res_detail.getPhoneNumber()+ res_detail.getLatitude() +" "+res_detail.getLongitude()+" "+res_detail.getWebsite());

		return res_detail;
	}
	public static JSONObject getLocationInfo(String address) {
		JSONObject jsonObject = new JSONObject();
		RestaurantAsyncTaskGetLocationInfo getLocationInfoTask = new RestaurantAsyncTaskGetLocationInfo( address);
		try {
			jsonObject = getLocationInfoTask.execute().get();
		} catch (Throwable th){
			th.printStackTrace();
			throw new RuntimeException("Query for getLocationInfo failed", th);

		}
		return jsonObject;
	}

	/*
	 * Convert the latitude and longitude into a geopoint to access google maps.
	 * Had some errors while calling the Geocoder class and adding this in manifest file resolved the
	 * error:
	 * <uses-library android:required="true" android:name="com.google.android.maps" />
	 * Finally using LocationObject class as I just need the latitude and longitude and not the 
	 * geopoint to get the directions on google maps.
	 */
	public static LocationObject getGeoPoint(JSONObject jsonObject) {

		Double lon = new Double(0);
		Double lat = new Double(0);

		try {

			lon = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
					.getJSONObject("geometry").getJSONObject("location")
					.getDouble("lng");

			lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
					.getJSONObject("geometry").getJSONObject("location")
					.getDouble("lat");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		LocationObject locObject = new LocationObject();
		locObject.setLat(lat);
		locObject.setLng(lon);
		return locObject;

	}
	public void setLastKnownLocation(double[] lastKnownLocation) {
		this.lastKnownLocation = lastKnownLocation;
	}
	public double[] getLastKnownLocation() {
		return lastKnownLocation;
	}
	public String findAddressfromLatLng(double lat, double lng){
		Geocoder geoCoder = new Geocoder(getContext(), Locale 
				.getDefault()); 
		String add = "";
		try { 
			List<Address> addresses = geoCoder.getFromLocation(lat,
					lng, 1);
			if (addresses.size() > 0) {
				for (int i = 0; i < addresses.get(0).getMaxAddressLineIndex(); i++)
					add += addresses.get(0).getAddressLine(i) + "\n";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//Log.v("geocoder addresses", add);
		if(add.equals("")){
			GetAddressAsyncTask addressAsyncTask;
			addressAsyncTask = new GetAddressAsyncTask(lat, lng);
			try {
				add = addressAsyncTask.execute().get();
			} catch (Throwable th) {
				// TODO Auto-generated catch block
				th.printStackTrace();
				throw new RuntimeException("Get address from lat&lon using google api failed!", th);
			} 
		}
		return add;

	}


}