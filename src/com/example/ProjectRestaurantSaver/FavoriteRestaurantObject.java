package com.example.ProjectRestaurantSaver;

public class FavoriteRestaurantObject {
	private String name;
	private int rating;
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

	public void setRating(int ratings) {
		rating = ratings;
	}

	public int getRating() {
		return rating;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddress() {
		return address;
	}

}


