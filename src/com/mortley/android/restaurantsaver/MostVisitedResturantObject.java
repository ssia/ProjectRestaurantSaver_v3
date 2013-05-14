package com.mortley.android.restaurantsaver;

public class MostVisitedResturantObject {
	private String name;
	private int NoOfTimes;
	private String address;
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

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
