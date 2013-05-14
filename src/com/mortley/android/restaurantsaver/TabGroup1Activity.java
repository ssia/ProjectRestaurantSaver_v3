package com.mortley.android.restaurantsaver;

import android.content.Intent;
import android.os.Bundle;

public class TabGroup1Activity extends TabGroupActivity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("In TabGroup1Activity-oncreate");
        startChildActivity("Near By Restaurants", new Intent(this, NearbyRestaurantActivity.class));
        System.out.println("In TabGroup1Activity-oncreate done");
    }
}
