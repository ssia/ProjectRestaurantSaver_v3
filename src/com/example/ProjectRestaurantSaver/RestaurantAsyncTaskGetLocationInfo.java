package com.example.ProjectRestaurantSaver;

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

public class RestaurantAsyncTaskGetLocationInfo extends AsyncTask<Void, Void, JSONObject> {
	protected String address;

	public RestaurantAsyncTaskGetLocationInfo(String address) {
		this.address = address;
		Log.v("RestaurantAsyncTaskFetchDetails", "restaurant adapter = "+ this.address);
	}

	@Override
	protected void onPreExecute() {

	}

	@Override
	protected JSONObject doInBackground(Void... params) {
		Log.v("Restaurant Adapter", "HttpGet "+address);
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


	@Override
	protected  void onPostExecute(JSONObject jsonObject) {
	}


}