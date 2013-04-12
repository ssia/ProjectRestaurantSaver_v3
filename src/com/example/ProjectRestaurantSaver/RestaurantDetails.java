package com.example.ProjectRestaurantSaver;
//Class to hold the Details of a restaurant. Data fetched from Google Places details API
public class RestaurantDetails {
	private String address;
	private String phoneNumber;
	private String website;
	private String latitude;
	private String longitude;

	RestaurantDetails(){
		setAddress("");
		setPhoneNumber("");
		setWebsite("");
		setLatitude("");
		setLongitude("");
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddress() {
		return address;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getWebsite() {
		return website;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLongitude() {
		return longitude;
	}
}
