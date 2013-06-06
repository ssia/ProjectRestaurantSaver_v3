package com.mortley.android.restaurantsaver;


import java.io.IOException;
import java.io.InputStream;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

import android.util.Log;

public class GetAddressAsyncTask extends AsyncTask<Void, Void, String> {
	double lat;
	double lng;
	String distanceURL;


	public GetAddressAsyncTask(double lat, double lng) {
		this.lat = lat;
		this.lng = lng;
	}



	@Override
	protected void onPreExecute() {

	}

	@Override
	protected String doInBackground(Void... params) {

        HttpGet httpGet = new HttpGet("http://maps.google.com/maps/api/geocode/json?latlng="+lat+","+lng+"&sensor=true");
        
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
        
        try {
        	JSONObject location;
            location = jsonObject.getJSONArray("results").getJSONObject(0);
            distanceURL = location.getString("formatted_address");
            //Log.d("GetAddressAsyncTask", "formattted address:" + distanceURL);
        } catch (JSONException e1) {
            e1.printStackTrace();

        }
        
		return distanceURL;
       
    }
	

	@Override
	protected void onPostExecute(String distanceinMiles) {
	}


}
