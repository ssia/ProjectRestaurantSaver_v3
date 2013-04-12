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
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MostVisitedAdapter extends ArrayAdapter<MostVisitedResturantObject>{
	private List<MostVisitedResturantObject> dataObjects;
	private Button dialButton;
	private DatabaseOpenHelper rd;
	private Button directionsButton;
	private Button deleteButton;
	private double[] lastKnownLocation;

	@SuppressWarnings("unused")
	private Geocoder geocoder = null;

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
		dialButton = (Button) v.findViewById(R.id.mVisitedContactButton);
		directionsButton = (Button) v.findViewById(R.id.mVisitedDirectionsButton);
		deleteButton = (Button) v.findViewById(R.id.mVisitedDeleteButton);
		MostVisitedResturantObject ref = dataObjects.get(position);
		if (ref != null) {
			TextView tt = (TextView) v.findViewById(R.id.mVisited_name);
			TextView tv = (TextView) v.findViewById(R.id.noOfTimes);
			TextView ta = (TextView) v.findViewById(R.id.mVisited_address);
			if (tt != null) {
				tt.setText(ref.getName());  
				//Get address for the restaurant by querying the database
				rd = DatabaseOpenHelper.getOrCreateInstance(getContext(), "restaurantSaver.db", null, 0);
				Cursor c = rd.check_restaurant_address_inDatabase(ref.getName());
				int contactColumn = c.getColumnIndex("RAddress");	
				@SuppressWarnings("unused")
				String mVisitedAddress = "";
				if (c != null) {
					c.moveToFirst();
					mVisitedAddress = c.getString(contactColumn);
				}
				//System.out.println("mVisitedAddress = "+ mVisitedAddress);
				ta.setText(mVisitedAddress);
				System.out.println("ref = "+ref);
			    //Set number of times in the row
				int tvs = ref.getNoOfTimes();
				String tvss = Integer.toString(tvs) + " visits";
				tv.setText(tvss);
			}
			/*
			 * Method to be implemented if the Contact button is clicked by the user. The method queries the database to
			 * get the contact number of the restaurant and put it in the dialer for the user to call the restaurant.
			 */
			dialButton.setOnClickListener(new ButtonClickListener(ref){
				@SuppressWarnings("unused")
				@Override
				public void onClick(View v){
					rd = DatabaseOpenHelper.getOrCreateInstance(getContext(), "restaurantSaver.db", null, 0);
					Cursor c = rd.check_restaurant_contact_inDatabase(item.getName());
					int contactColumn = c.getColumnIndex("RContact");	
					String favContact;
					if (c != null) {
						c.moveToFirst();
						favContact = c.getString(contactColumn);
					}
					else {
						favContact = "";
						Toast.makeText(getContext(), "Contact Number is Not Available", Toast.LENGTH_SHORT).show();
					}
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
					Cursor c = rd.check_restaurant_address_inDatabase(item.getName());
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
					geocoder = new Geocoder(getContext());
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
					rd = DatabaseOpenHelper.getOrCreateInstance(getContext(), "restaurantSaver.db", null, 0);

					Cursor all = rd.check_restaurant_favorite_inDatabase(item.getName());
					int favColumn = all.getColumnIndex("RFavorite");	
					/*check if the restaurant is marked as Favorties. If yes, then only remove entry from NoOfTimes Column in the database
					If the restaurant is not marked as Favorites, then remove the entry for the restaurant from the databse*/
					if(favColumn == 0){
						boolean c = rd.deleteRowInList(item.getName());
					}
					else{
						rd.removeMVisitedInDatabase(item.getName());
					}
					MostVisitedAdapter.this.remove(item);//inner class accessing the parent to remove just the particular row of the list
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

		HttpGet httpGet = new HttpGet("http://maps.google."
				+ "com/maps/api/geocode/json?address=" + address
				+ "ka&sensor=false");
		HttpClient client = new DefaultHttpClient();
		HttpResponse response;
		StringBuilder stringBuilder = new StringBuilder();

		try {
			response = client.execute(httpGet);
			HttpEntity entity = response.getEntity();
			InputStream stream = entity.getContent();
			int b;
			while ((b = stream.read()) != -1) {
				stringBuilder.append((char) b);
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		}

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject = new JSONObject(stringBuilder.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
}
