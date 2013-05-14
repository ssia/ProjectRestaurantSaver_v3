package com.mortley.android.restaurantsaver;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

public class VisitedCallAsnycTask extends AsyncTask<Void, Void, Void> {
	Context context;
	MostVisitedResturantObject mv;
	private DatabaseOpenHelper rd;

	public VisitedCallAsnycTask(Context context, MostVisitedResturantObject mv) {
		this.context = context;
		this.mv = mv;
	}

	

	@SuppressWarnings("unused")
	@Override
	protected Void doInBackground(Void... params) {
		rd = DatabaseOpenHelper.getOrCreateInstance(context, "restaurantSaver.db", null, 0);
		Cursor c = rd.check_restaurant_contact_inDatabase(mv.getId());
		int contactColumn = c.getColumnIndex("RContact");	
		String favContact;
		if (c != null) {
			c.moveToFirst();
			favContact = c.getString(contactColumn);
		}
		else {
			favContact = "";
			Toast.makeText(context, "Contact Number is Not Available", Toast.LENGTH_SHORT).show();
		}
		Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+favContact));
		context.startActivity(intent);
		return null;
	}


	protected void onPostExecute() {
		
	}

}
