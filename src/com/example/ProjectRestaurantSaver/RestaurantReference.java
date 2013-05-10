package com.example.ProjectRestaurantSaver;

import java.util.Comparator;

//class to hold the data received from the Google Places API webservice
public class RestaurantReference {
	private String name;
	private String id;
	private String referenceKey; // reference key unique to every restaurant. It is used to call the GooglePlaces details API
	private boolean isInFavorites; 
	private double latitude;
	private double longitude;
	private String address;
	//private String distanceInMiles;

	RestaurantReference(){
		setName("");
		setReferenceKey("");
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}


	public void setReferenceKey(String referenceKey) {
		this.referenceKey = referenceKey;
	}

	public String getReferenceKey() {
		return referenceKey;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setInFavorites(boolean isInFavorites) {
		
		//System.out.println(".....................Settting in favoritess for " + this.getName() + " to " + isInFavorites + ", oldValue: " + this.isInFavorites);
		this.isInFavorites = isInFavorites;
	}

	public boolean getInFavorites() {
		return isInFavorites;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddress() {
		return address;
	}

	/*public String getDistanceInMiles() {
		return distanceInMiles;
	}

	public void setDistanceInMiles(String distanceInMiles) {
		this.distanceInMiles = distanceInMiles;
	}*/




}
