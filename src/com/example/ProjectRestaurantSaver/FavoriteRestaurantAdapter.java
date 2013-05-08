package com.example.ProjectRestaurantSaver;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.example.ProjectRestaurantSaver.application.RestaurantApplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.RatingBar;
import android.widget.Toast;

public class FavoriteRestaurantAdapter extends ArrayAdapter<FavoriteRestaurantObject>{
	private List<FavoriteRestaurantObject> dataObjects;
	private ImageButton dialButton;
	private DatabaseOpenHelper rd;
	private ImageButton directionsButton, websiteButton;
	private RatingBar rating;
	private ImageButton deleteButton;
	private double[] lastKnownLocation;
	private String favwebsite;
	public FavoriteRestaurantAdapter(Context context, int resource,
			int textViewResourceId, List<FavoriteRestaurantObject> objects) {
		super(context, resource, textViewResourceId, objects);
		dataObjects = objects;
	}

	@Override
	public View getView( int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.favoriterow, null);//create instance of the template for the particular row represented by position
		}

		dialButton = (ImageButton) v.findViewById(R.id.favContactButton);
		directionsButton = (ImageButton) v.findViewById(R.id.favDirectionsButton);
		deleteButton = (ImageButton) v.findViewById(R.id.favDelete);
		websiteButton = (ImageButton) v.findViewById(R.id.favWebsiteButton);
		rating= (RatingBar) v.findViewById(R.id.ratingbar);// create RatingBar object
		FavoriteRestaurantObject ref = dataObjects.get(position);
		rating.setStepSize((float)0.5);
		rating.setRating((float) (ref.getRating()));

		if (ref != null) {
			TextView tt = (TextView) v.findViewById(R.id.favName);
			TextView ta = (TextView) v.findViewById(R.id.favVisited_address);
			//Get address for the restaurant by querying the database
			rd = DatabaseOpenHelper.getOrCreateInstance(getContext(), "restaurantSaver.db", null, 0);
			//Log.v("ref.getName() + ID", ref.getName() + ref.getId());
			Cursor c = rd.check_restaurant_address_inDatabase(ref.getId());
			
			
			int contactColumn = c.getColumnIndex("RAddress");	
			@SuppressWarnings("unused")
			String favVisitedAddress = "";
			if (c != null && c.getCount() > 0) {
				c.moveToFirst();
				favVisitedAddress = c.getString(contactColumn);
			}
			//System.out.println("mVisitedAddress = "+ mVisitedAddress);
			ta.setText(favVisitedAddress);
			if (tt != null) {
				tt.setText(ref.getName());  
			}
			
			rd = DatabaseOpenHelper.getOrCreateInstance(getContext(), "restaurantSaver.db", null, 0);
			Cursor c1 = rd.get_website_inDatabase(ref.getId());//Changed the query to find by res_id
			int contactColumn1 = c.getColumnIndex("Rwebsite");	
			favwebsite = "";
			if (c != null && c.getCount() > 0) {
				c.moveToFirst();
				favwebsite = c.getString(contactColumn);
			}
			if(favwebsite.equals("")){
				websiteButton.setVisibility(View.GONE);
			}
			websiteButton.setOnClickListener(new ButtonClickListener(ref){

				@SuppressWarnings("unused")
				@Override
				public void onClick(View v) {
					Context context = getContext();
					Uri uri = Uri.parse(favwebsite);
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					context.startActivity(intent);
				}
				
			});
			dialButton.setOnClickListener(new ButtonClickListener(ref){
				@Override
				public void onClick(View v){
					rd = DatabaseOpenHelper.getOrCreateInstance(getContext(), "restaurantSaver.db", null, 0);
					Cursor c = rd.check_restaurant_contact_inDatabase(item.getId());////Changed the query to find by res_id
					int contactColumn = c.getColumnIndex("RContact");	
					String favContact;
					if (c != null) {
						c.moveToFirst();
						favContact = c.getString(contactColumn);
					}
					else favContact = "";
					Context context = getContext();
					Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+favContact));
					context.startActivity(intent);
				}
			});
			/*
			 * Method to be implemented if the Direction button is clicked by the user. The method queries the database to
			 * get the direction to the restaurant in google maps.
			 */
			directionsButton.setOnClickListener(new ButtonClickListener(ref){
				LocationObject location = new LocationObject();

				@SuppressWarnings("unused")
				@Override
				public void onClick(View arg0) {
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
						Toast.makeText(getContext(), "Contact Number is Not Available", Toast.LENGTH_SHORT).show();
					}
					String newString = mVisitedAddress.replace(",", "");
					newString = newString.replace(" ", "+");
					List<Address> foundGeocode = null;
					JSONObject loc = getLocationInfo(newString);
					LocationObject p = getGeoPoint(loc);

					double lat = p.getLat();
					double lon = p.getLng();
					System.out.println("gropoint ="+p+" "+lat+ " "+lon);

					Intent intent = new Intent(android.content.Intent.ACTION_VIEW, 
							Uri.parse("http://maps.google.com/maps?saddr="+ lastKnownLocation[0] + "," + lastKnownLocation[1] +"&daddr=" + lat +","+lon));
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
							Log.v("Favorite Adapter", "item.getId()  = "+item.getId());

							rd = DatabaseOpenHelper.getOrCreateInstance(getContext(), "restaurantSaver.db", null, 0);
							RestaurantApplication restaurantApplication = (RestaurantApplication) getContext().getApplicationContext();
							Cursor all = rd.check_restaurant_favorite_inDatabase(item.getId());//Changed the query to find by res_id
							int timesColumn = 0;
							String favoriteData = "";
							if (all != null) {
								all.moveToFirst();
								timesColumn = all.getColumnIndex("NoOfTimes");	
								favoriteData = all.getString(timesColumn);
							}
							Log.v("Favorite Restaurant Adapter", "fav timesColumn  = "+favoriteData);

							//Check if the restaurant is present in the MostVistited list.If yes, then only change the Favorites entry, else delete the Restaurant entry from the database.
							if(Integer.parseInt(favoriteData) < 1){
								boolean c = rd.deleteRowInList(item.getId());//Changed the query to find by res_id
								Log.v("Favorite Restaurant Adapter", "Boolean c = "+c);
							}
							else{
								rd.removeRFavoriteInDatabase(item.getId());//Changed the query to find by res_id
								Log.v("Favorite Restaurant Adapter", " else Boolean c = ");

							}
							FavoriteRestaurantAdapter.this.remove(item);//inner class accessing the parent to remove just the particular row of the list
							
							// After deleting the restaurant from the favorites list, we update the change for the STAR in the NearbyRestaurantActivity List
							if(restaurantApplication.getRestaurantAdapter() != null){
								restaurantApplication.getRestaurantAdapter().notifyDataSetChanged();
							}
							Toast.makeText(getContext(), ""+item.getName()+" deleted from Favorites", Toast.LENGTH_SHORT).show();
						}
					})
					.setNegativeButton("No", null)//Do nothing on no
					.show();

				}
			});
			
			OnRatingBarChangeListener barChangeListener = new RatingChangeListener(ref) {	
				
				@Override
				public void onRatingChanged(RatingBar rBar, float fRating, boolean fromUser) {
					if(fromUser){
						//int rating = (int) fRating;
						float rating = fRating;
						Log.v("Rating selected= ", String.valueOf(rating));
						Log.v("Adding In Database", item.getName()+ " "+item.getId()+" "+String.valueOf(rating));
						rd = DatabaseOpenHelper.getOrCreateInstance(getContext(), "restaurantSaver.db", null, 0);
						item.setRating(rating);
						rd.updateRatingInDatabase(item.getId(), rating);//changed from item.getName() to item.getId() due to adding Res_Id as Primary_Key in Database	    				
					}
				}
			};
			rating.setOnRatingBarChangeListener(barChangeListener);// select listener		
		}
		return v;
	}

	protected abstract class ButtonClickListener implements OnClickListener {
		protected FavoriteRestaurantObject item;
		public ButtonClickListener(FavoriteRestaurantObject item) {
			this.item = item;
		}
	}

	protected abstract class RatingChangeListener implements OnRatingBarChangeListener {
		protected FavoriteRestaurantObject item;
		public RatingChangeListener(FavoriteRestaurantObject item) {
			this.item = item;
		}
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

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();  
	}
	public void setLastKnownLocation(double[] lastKnownLocation) {
		this.lastKnownLocation = lastKnownLocation;
	}
	public double[] getLastKnownLocation() {
		return lastKnownLocation;
	}

}
