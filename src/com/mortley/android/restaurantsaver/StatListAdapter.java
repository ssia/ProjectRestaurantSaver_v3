package com.mortley.android.restaurantsaver;


import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.ResourceCursorAdapter;

public class StatListAdapter extends ResourceCursorAdapter{
	     
	    public StatListAdapter(Context context, Cursor cursor) {
	        super(context, R.layout.statlistrow, cursor);
	    }
	 
	    @Override
	    public void bindView(View view, Context context, Cursor cursor) {
	    }
}

