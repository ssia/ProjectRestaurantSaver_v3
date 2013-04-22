package com.example.ProjectRestaurantSaver;

public class FavoriteRestaurantObject {
	private String name;
	private float rating;
	private String address;
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	FavoriteRestaurantObject(){
		setName("");
		setRating(0);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setRating(float ratings) {
		rating = ratings;
	}

	public float getRating() {
		return rating;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddress() {
		return address;
	}

}


