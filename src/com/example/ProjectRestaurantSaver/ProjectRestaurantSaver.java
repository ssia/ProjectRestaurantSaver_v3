package com.example.ProjectRestaurantSaver;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
public class ProjectRestaurantSaver extends TabActivity {
	private TabSpec firstTabSpec;
	private TabSpec secondTabSpec;
	private TabSpec thirdTabSpec;
	private TabSpec fourthTabSpec;
	private TabHost tabHost ;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		tabHost = (TabHost)findViewById(android.R.id.tabhost);
		firstTabSpec = tabHost.newTabSpec("tid1");
		secondTabSpec = tabHost.newTabSpec("tid2");
		thirdTabSpec = tabHost.newTabSpec("tid3");
		fourthTabSpec = tabHost.newTabSpec("tid4");
		//AddFlags helps to add flags to call the intent again helping to refresh the tab every time it is clicked.
		//We are not adding flags to the first tab as we don't want a web service call every time we click it.
		firstTabSpec.setIndicator("Restaurants").setContent(new Intent(this, TabGroup1Activity.class));
		secondTabSpec.setIndicator("Favorite Restaurants").setContent(new Intent(this, FavoriteRestaurantActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
		thirdTabSpec.setIndicator("Most Visited Restaurants").setContent(new Intent(this, MostVisitedActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
		fourthTabSpec.setIndicator("Stats").setContent(new Intent(this, StatsActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
		tabHost.addTab(firstTabSpec);
		tabHost.addTab(secondTabSpec);
		tabHost.addTab(thirdTabSpec);
		tabHost.addTab(fourthTabSpec);
		tabHost.setCurrentTab(0);
	}

}