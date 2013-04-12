package com.example.ProjectRestaurantSaver;

public class LocationObject {
	private double lat;
	private double lng;
	
	LocationObject(){
		lat = 0.0;
		lng = 0.0;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLat() {
		return lat;
	}
	public void setLng(double lng) {
		this.lng = lng;
	}
	public double getLng() {
		return lng;
	}
}
