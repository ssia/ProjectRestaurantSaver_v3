package com.example.ProjectRestaurantSaver;

public class MostVisitedResturantObject {
	private String name;
	private int NoOfTimes;
	private String address;
	private String id;

	MostVisitedResturantObject(){
		setName("");
		setNoOfTimes(0);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setNoOfTimes(int noOfTimes) {
		NoOfTimes = noOfTimes;
	}

	public int getNoOfTimes() {
		return NoOfTimes;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddress() {
		return address;
	}

}
