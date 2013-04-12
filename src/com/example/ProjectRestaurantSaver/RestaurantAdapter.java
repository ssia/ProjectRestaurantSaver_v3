package com.example.ProjectRestaurantSaver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Locale;

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
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class RestaurantAdapter extends ArrayAdapter<RestaurantReference> {
	private List<RestaurantReference> dataObjects;
	private DatabaseOpenHelper rd;
	private double[] lastKnownLocation;	

	/*
	 * The two main inputs to the adapter are the data source which is the list of items and the template to represent
	 * a row in the list. 
	 */
	public RestaurantAdapter(Context context, int resource,
			int textViewResourceId, List<RestaurantReference> objects) {
		super(context, resource, textViewResourceId, objects);	
		dataObjects = objects;
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
			v = vi.inflate(R.layout.restaurantrow, null);//create instance of the template for the particular row represented by position
		}
		ImageButton addButton = (ImageButton) v.findViewById(R.id.add_button);
		Button dialButton = (Button) v.findViewById(R.id.contactButton);
		Button directionsButton = (Button) v.findViewById(R.id.directionsButton);
		TextView distanceLabel = (TextView) v.findViewById(R.id.distance_label);

		//check if the restaurant is already marked as favorite
		RestaurantReference ref = dataObjects.get(position);
		if (ref != null) {
			TextView tt = (TextView) v.findViewById(R.id.label);
			if (tt != null) {
				tt.setText(ref.getName());                            
			}
			String destinationAddress = findAddressfromLatLng(ref.getLatitude(), ref.getLongitude());
			destinationAddress = destinationAddress.replaceAll("(\\r|\\n)", "");
			//Log.v("destination address=", destinationAddress);
			String currentaddress = "401 Castro St, Mountain View, CA 94041, USA";
			String distanceURL = "http://maps.googleapis.com/maps/api/distancematrix/json?origins="+currentaddress+"&destinations="+destinationAddress+"&mode=driving&sensor=false&units=imperial";
			distanceURL =  distanceURL.replaceAll(" ", "%20");
			//String distanceURL = "http://maps.googleapis.com/maps/api/distancematrix/json?origins=401%20Castro%20St,%20Mountain%20View,%20CA%2094041,%20USA&destinations=800%20California%20StMountain%20View,%20CA%2094041&mode=driving&sensor=false&units=imperial";
			//Log.v("distanceURL=", distanceURL);
			URL urlPlace;
			try {
				ThreadPolicy tp = ThreadPolicy.LAX;// for dev purposes only
				StrictMode.setThreadPolicy(tp);// for dev purposes only
				urlPlace = new URL(distanceURL);
				URLConnection tc = urlPlace.openConnection();
				BufferedReader in = new BufferedReader(new InputStreamReader(tc.getInputStream()));
				String line, finalLine = "";
				while ((line = in.readLine()) != null) {
					finalLine += line;
				}
				in.close();
				//Log.v("distance=", finalLine);
				JSONObject obj = new JSONObject(finalLine);
				JSONArray ja = obj.getJSONArray("rows");
				JSONObject jo = (JSONObject) ja.get(0);
				JSONArray jb = jo.getJSONArray("elements");
				JSONObject jbo = jb.getJSONObject(0).getJSONObject("distance");
				String distanceinMiles= jbo.getString("text");
				//Log.v("distanceinmiles", distanceinMiles);
				distanceLabel.setText(distanceinMiles);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    		
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
			ImageButton favButton = (ImageButton) v.findViewById(R.id.fav_button);
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
					Toast.makeText(getContext(), item.getName()+ "  "+ item.getId()+ " added to Most Visited Restaurant List", Toast.LENGTH_SHORT).show();
					String nameOfRes = item.getName();
					String resId = item.getId();
					RestaurantDetails details = fetchRestaurantDetails(item);
					String address = details.getAddress();
					String contact = details.getPhoneNumber();					
					rd = DatabaseOpenHelper.getOrCreateInstance(getContext(), "restaurantSaver.db", null, 0);
					Cursor c = rd.check_restaurant_visited_inDatabase(resId);
					try{
							
						if (c != null) {
							c.moveToFirst();
							if (c.isFirst()) {
								int firstNameColumn = c.getColumnIndex("RID");
								String firstName = c.getString(firstNameColumn);								
								int timesNameColumn = c.getColumnIndex("NoOfTimes");
								String timesName = c.getString(timesNameColumn);
								c.moveToFirst();
								int num = Integer.parseInt(timesName);
								num = num + 1;
								rd.updateTimesInDatabase(resId, num); //???if the restaurant was already visited in the past increase the number of visits by 1
							}
							else{ //create a new entry for the restaurant
								rd.insert_mostVisited(resId, nameOfRes, 1, address, contact);
							}
						}	
					} 
					finally{
						c.close();
					}
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

					ImageButton btn = (ImageButton)v;
					rd = DatabaseOpenHelper.getOrCreateInstance(getContext(), "restaurantSaver.db", null, 0);
					System.out.println(item.getName() + " get fav = "+ item + " "+item.isInFavorites());
					if(!item.isInFavorites()) {//if the restaurant is not in favorites make it a favorite
						//Log.v("RestaurantAdapter, Restaurant Address and Phone No =", address+" "+contact);
						btn.setImageResource((R.drawable.star_green));
						item.setInFavorites(true);

						Cursor c = rd.check_restaurant_favorite_inDatabase(resId);

						if (c != null) {
							//Log.v("Restaurant Adapter, Column No. of RName =  ", ""+firstNameColumn);
							c.moveToFirst();
							if (c.isFirst()) {
								int firstNameColumn = c.getColumnIndex("RID");	
								String firstName = c.getString(firstNameColumn);
								int favNameColumn = c.getColumnIndex("RFavorite");
								String favName = c.getString(favNameColumn);
								//Log.v("Restaurant Adapter, RFavorite =", ""+favName);
								c.moveToFirst();
								rd.favTimesInDatabase(resId);
							} else{
								rd.insert_fav(resId, nameOfRes, address, contact);
							}
						}

						Toast.makeText(getContext(), item.getName() +" added to Favorites", Toast.LENGTH_SHORT).show();
					} else {//if the restaurant is already marked as a Favorite, remove it from Favorite's.
						btn.setImageResource((R.drawable.star));

						Cursor c = rd.check_restaurant_visited_inDatabase(item.getId());
						try {
							boolean exists = c.moveToFirst();
							if(exists) {
								int numVisited = c.getInt(c.getColumnIndex("NoOfTimes"));

								if(numVisited > 0) {
									rd.favTimesInDatabase(resId, false);
								} else {
									rd.deleteRowInList(item.getId());
								}

								item.setInFavorites(false);
								Toast.makeText(getContext(), item.getName() +" removed from Favorites", Toast.LENGTH_SHORT).show();
							}
						} finally {
							c.close();
						}
					}
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
					RestaurantDetails details = fetchRestaurantDetails(item);
					String mVisitedAddress = details.getAddress();
					/*Cursor c = rd.check_restaurant_address_inDatabase(item.getName());
					int contactColumn = c.getColumnIndex("RAddress");	
					if (c != null && c.getCount() != 0) {
						c.moveToFirst();
						mVisitedAddress = c.getString(contactColumn);
					}
					else {
						mVisitedAddress = "";
						Toast.makeText(getContext(), "Directions Not Available", Toast.LENGTH_SHORT).show();
					}
					c.close();*/
					String newString = mVisitedAddress.replace(",", "");
					newString = newString.replace(" ", "+");
					List<Address> foundGeocode = null;
					JSONObject loc = getLocationInfo(newString);
					LocationObject p = getGeoPoint(loc);
					double lat = p.getLat();
					double lon = p.getLng();
					System.out.println("http://maps.google.com/maps?saddr="+lastKnownLocation[0] +","+lastKnownLocation[1]+"&daddr="+lat+","+lon);
					Intent intent = new Intent(android.content.Intent.ACTION_VIEW, 
							Uri.parse("http://maps.google.com/maps?saddr="+lastKnownLocation[0] +","+lastKnownLocation[1]+"&daddr="+lat+","+lon));
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

	public RestaurantDetails fetchRestaurantDetails(RestaurantReference object){
		String ref_key = object.getReferenceKey();
		RestaurantDetails res_detail = new RestaurantDetails();
		URL urlPlace = null;
		try {
			urlPlace = new URL("https://maps.googleapis.com/maps/api/place/details/json?reference="+ref_key+"&sensor=true&key=AIzaSyBsxN3NdPnzp4X4QDkh1R1tBDPQQ30lD6s");
			Log.v("URL", urlPlace.toString());
			URLConnection tc = urlPlace.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(tc.getInputStream()));
			String line, finalLine = "";
			while ((line = in.readLine()) != null) {
				finalLine += line;
			}
			JSONObject obj = new JSONObject(finalLine);
			JSONObject ja = obj.getJSONObject("result");
			res_detail.setAddress(ja.getString("formatted_address"));
			res_detail.setPhoneNumber(ja.getString("formatted_phone_number"));
			res_detail.setWebsite(ja.getString("website"));
			//Log.v("Restaurant Adapter, Restaurant Address,PhNo,Lat, Lon, Website frm JSON =", res_detail.getAddress() + "  "+res_detail.getPhoneNumber()+ res_detail.getLatitude() +" "+res_detail.getLongitude()+" "+res_detail.getWebsite());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return res_detail;
	}
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
		return add;

	}
}
