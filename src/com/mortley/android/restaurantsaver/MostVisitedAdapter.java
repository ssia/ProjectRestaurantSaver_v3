package com.mortley.android.restaurantsaver;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MostVisitedAdapter extends ArrayAdapter<MostVisitedResturantObject>{
	private List<MostVisitedResturantObject> dataObjects;
	private ImageButton dialButton;
	private DatabaseOpenHelper rd;
	private ImageButton directionsButton;
	private ImageButton deleteButton, websiteButton, visitedPlusOne;
	private double[] lastKnownLocation;
	TextView tv;
	String website;
	@SuppressWarnings("unused")
	private Geocoder geocoder = null;
	private double currentLat;
	private double currentLon;

	public MostVisitedAdapter(Context context, int resource,
			int textViewResourceId, List<MostVisitedResturantObject> objects) {
		super(context, resource, textViewResourceId, objects);	
		dataObjects = objects;
	}

	/*
	 * (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 * Overriding getView to customize how data is displayed in every row.
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.mostvisitedrow, null);//create instance of the template for the particular row represented by position
		}
		dialButton = (ImageButton) v.findViewById(R.id.mVisitedContactButton);
		directionsButton = (ImageButton) v.findViewById(R.id.mVisitedDirectionsButton);
		websiteButton = (ImageButton) v.findViewById(R.id.mVisited_website);		
		deleteButton = (ImageButton) v.findViewById(R.id.mVisitedDeleteButton);
		visitedPlusOne = (ImageButton) v.findViewById(R.id.add_button_visited);
		MostVisitedResturantObject ref = dataObjects.get(position);
		if (ref != null) {
			TextView tt = (TextView) v.findViewById(R.id.mVisited_name);
			tv = (TextView) v.findViewById(R.id.noOfTimes);
			TextView ta = (TextView) v.findViewById(R.id.mVisited_address);
			if (tt != null) {
				tt.setText(ref.getName());  
				//Get address for the restaurant by querying the database
				rd = DatabaseOpenHelper.getOrCreateInstance(getContext(), "restaurantSaver.db", null, 0);
				Cursor c = rd.check_restaurant_address_inDatabase(ref.getId());//Changed the query to find by res_id
				int contactColumn = c.getColumnIndex("RAddress");	
				@SuppressWarnings("unused")
				String mVisitedAddress = "";
				if (c != null) {
					c.moveToFirst();
					mVisitedAddress = c.getString(contactColumn);
				}
				ta.setText(mVisitedAddress);
				int tvs = ref.getNoOfTimes();
				if(tvs == 1)
					tv.setText(Integer.toString(tvs) + " visit");
				else
					tv.setText(Integer.toString(tvs) + " visits");
				
				rd = DatabaseOpenHelper.getOrCreateInstance(getContext(), "restaurantSaver.db", null, 0);
				Cursor c1 = rd.get_website_inDatabase(ref.getId());//Changed the query to find by res_id
				int contactColumn1 = c1.getColumnIndex("Rwebsite");	
				String favwebsite = "";
				if (c1 != null && c1.getCount() > 0) {
					c1.moveToFirst();
					favwebsite = c1.getString(contactColumn1);
				}
				if(favwebsite.equals("")){
					websiteButton.setVisibility(View.GONE);
				}
				//Log.v("FavoriteRestaurantAdapter", "website = "+favwebsite);
				this.notifyDataSetChanged();
			}

			/*
			 * Method to be implemented if the Contact button is clicked by the user. The method queries the database to
			 * get the contact number of the restaurant and put it in the dialer for the user to call the restaurant.
			 */
			websiteButton.setOnClickListener(new ButtonClickListener(ref){
				@Override
				public void onClick(View v) {
					
					rd = DatabaseOpenHelper.getOrCreateInstance(getContext(), "restaurantSaver.db", null, 0);
					Cursor c = rd.get_website_inDatabase(item.getId());//Changed the query to find by res_id
					int contactColumn = c.getColumnIndex("Rwebsite");	
					if (c != null && c.getCount() > 0) {
						c.moveToFirst();
						website = c.getString(contactColumn);
					}
					if(website.equals("")){
						websiteButton.setVisibility(View.GONE);
					}
					
					Context context = getContext();
					Uri uri = Uri.parse(website);
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					context.startActivity(intent);	
				}
				
			});
			dialButton.setOnClickListener(new ButtonClickListener(ref){
				@SuppressWarnings("unused")
				@Override
				public void onClick(View v){
					VisitedCallAsnycTask callAsync = new VisitedCallAsnycTask(getContext(), item);
					callAsync.execute();
				}
			});
			
			//Increment the number of visits to restaurant which is already in the visited list.
			visitedPlusOne.setOnClickListener(new ButtonClickListener(ref) {
				@Override
				public void onClick(View v) {
					Toast.makeText(getContext(), item.getName()+ " added to Visits", Toast.LENGTH_SHORT).show();
					String nameOfRes = item.getName();
					String resId = item.getId();
					int tvs = item.getNoOfTimes() + 1;
					
					if(tvs == 1){
						tv.setText(Integer.toString(tvs) + " visit");
					}
					else{
						tv.setText(Integer.toString(tvs) + " visits");
					}
					rd = DatabaseOpenHelper.getOrCreateInstance(getContext(), "restaurantSaver.db", null, 0);
					Cursor c = rd.check_restaurant_visited_inDatabase(resId);
					try{
						if (c != null) {
							c.moveToFirst();
							if (c.isFirst()) {
								int firstNameColumn = c.getColumnIndex("_id");
								String firstName = c.getString(firstNameColumn);								
								int timesNameColumn = c.getColumnIndex("NoOfTimes");
								String timesName = c.getString(timesNameColumn);
								c.moveToFirst();
								int num = Integer.parseInt(timesName);
								num = num + 1;
								rd.updateTimesInDatabase(resId, num); //if the restaurant was already visited in the past increase the number of visits by 1
								item.setNoOfTimes((item.getNoOfTimes() + 1));
							}
							
						}
						
					} 
					finally{
						c.close();
					}
				}
			}); 
			

			
			/*
			 * Method to be implemented if the Direction button is clicked by the user. The method queries the database to
			 * get the direction to the restaurant in google maps.
			 */
			directionsButton.setOnClickListener(new ButtonClickListener(ref){

				@SuppressWarnings("unused")
				@Override
				public void onClick(View arg0) {
					calculateCurrentAddress(null);
					rd = DatabaseOpenHelper.getOrCreateInstance(getContext(), "restaurantSaver.db", null, 0);
					Cursor c = rd.check_restaurant_address_inDatabase(item.getId());//Changed the query to find by res_id
					int contactColumn = c.getColumnIndex("RAddress");	
					String mVisitedAddress;
					if (c != null) {
						c.moveToFirst();
						mVisitedAddress = c.getString(contactColumn);
					}
					else {
						mVisitedAddress = "";
						Toast.makeText(getContext(), "Contact Address is Not Available", Toast.LENGTH_SHORT).show();
					}
					String newString = mVisitedAddress.replace(",", "");
					newString = newString.replace(" ", "+");
					List<Address> foundGeocode = null;
					geocoder = new Geocoder(getContext());
					JSONObject loc = getLocationInfo(newString);
					LocationObject p = getGeoPoint(loc);

					double destLat = p.getLat();
					double destLon = p.getLng();

					Intent intent = new Intent(android.content.Intent.ACTION_VIEW, 
							Uri.parse("http://maps.google.com/maps?saddr="+ currentLat + "," + currentLon +"&daddr=" + destLat +","+destLon));
					Context context = getContext();
					context.startActivity(intent);
				}
			});

			deleteButton.setOnClickListener(new ButtonClickListener(ref){
				@Override
				public void onClick(View arg0) {
					//Put up the Yes/No message box before deleting the restaurant from database
					AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
					builder
					.setTitle("Delete Restaurant")
					.setMessage("Are you sure?")
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {			      	
							//Yes button clicked, do something										
							rd = DatabaseOpenHelper.getOrCreateInstance(getContext(), "restaurantSaver.db", null, 0);
							//Log.v("MostVisited Adapter", "item.getId()  = "+item.getId());
							int favColumn = 0;
							String favoriteData = "";
							Cursor all = rd.check_restaurant_favorite_inDatabase(item.getId());
							if (all != null && all.getCount() > 0) {
								all.moveToFirst();
								favColumn = all.getColumnIndex("RFavorite");
								favoriteData = all.getString(favColumn);

							}
							
							/*check if the restaurant is marked as Favorties. If yes, then only remove entry from NoOfTimes Column in the database
							If the restaurant is not marked as Favorites, then remove the entry for the restaurant from the database*/
							//Log.v("MostVisited Adapter", " favColumn  = "+favoriteData);

							if(Integer.parseInt(favoriteData) < 1){
								boolean c = rd.deleteRowInList(item.getId());
							}
							else{
								rd.removeMVisitedInDatabase(item.getId());
							}
							MostVisitedAdapter.this.remove(item);//inner class accessing the parent to remove just the particular row of the list
							Toast.makeText(getContext(), ""+item.getName()+" deleted from Visits", Toast.LENGTH_SHORT).show();
						}
					})
					.setNegativeButton("No", null)//Do nothing on no
					.show();
				}
			});
		}
		return v;
	}


	private abstract class ButtonClickListener implements OnClickListener {

		protected MostVisitedResturantObject item;
		public ButtonClickListener(MostVisitedResturantObject item) {
			this.item = item;
		}
	}


	/*
	 * Get the location information using address.
	 */
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
	
	protected void calculateCurrentAddress(Location location) {
		currentLat = 0.0;
		currentLon = 0.0;
		LocationManager lm = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);    

		if(location == null) {
			location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);  
		}
		if(location == null) {
			location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);  
		}
		if(location == null){
			currentLat = 0.0;
			currentLon = 0.0;
		}
		else{
			currentLat = location.getLatitude();
			currentLon = location.getLongitude();
			//Log.v("RestaurantAdapter", "lat = "+currentLat);
			//Log.v("RestaurantAdapter", "lon"+currentLon); 			
		}
	}
}
