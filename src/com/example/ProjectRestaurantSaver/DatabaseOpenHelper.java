package com.example.ProjectRestaurantSaver;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseOpenHelper extends SQLiteOpenHelper{
	static private String dbName;
	static final String restaurantTable = "restaurants";
	static final String restaurantName = "RName";
	static final String noOfTimes = "NoOfTimes";
	static final String restaurantAddress = "RAddress";
	static final String restaurantContact = "RContact";
	static final String restaurantFavorite = "RFavorite";
	static final String restaurantWebsite = "Rwebsite";
	static final String restaurantId = "_id";
	static final String restaurantRatings = "Rrating";
	private static final String CREATE_RESTAURANT_SQL=
		"CREATE TABLE " + restaurantTable + " ("
		+ restaurantName + " TEXT,"
		+ restaurantId + " TEXT,"
		+ noOfTimes + " INTEGER,"+ restaurantAddress + " TEXT,"
		+ restaurantContact + " TEXT,"
		+ restaurantFavorite + " INTEGER,"
		+ restaurantWebsite + " TEXT,"
		+ restaurantRatings + " FLOAT"
		+ ");";

	private static DatabaseOpenHelper rd;

	public DatabaseOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, null, 33);
	}

	public static DatabaseOpenHelper getOrCreateInstance(Context context, String name,
			CursorFactory factory, int version) {
		if(rd != null) 
			return rd;
		rd = new DatabaseOpenHelper(context, name, factory, version);
		return rd;
	}

	public static DatabaseOpenHelper getInstance() {
		return rd;
	}

	/*
	 * Create a restaurant database with the name passed as string "name" by the user
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_RESTAURANT_SQL);
		ContentValues cv = new ContentValues(); 
		cv.put(restaurantId, "");
		cv.put(noOfTimes, 0 );
		cv.put(restaurantFavorite, 0);
		cv.put(restaurantRatings, 0.0);
		db.update(restaurantTable, cv, null, null);
	}


	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}

	/*
	 * Insert the name of the restaurant, the number of times and address of the restaurant as the user selects the
	 * restaurant to add to the "Most visited list"
	 */
	public void insert_mostVisited(String id, String name, int times, String address, String contact, String website){
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		
		values.put(restaurantId, id);
		values.put(restaurantName, name);
		values.put(noOfTimes, times);
		values.put(restaurantAddress, address);
		values.put(restaurantContact, contact);
		values.put(restaurantWebsite, website);
		values.put(restaurantFavorite, 0);
		db.insert(restaurantTable, null, values);
		db.close();
	}

	/*
	 * Insert the name of the restaurant, the number of times and address of the restaurant as the user selects the
	 * restaurant to add to the "Most visited list"
	 */
	public void insert_fav(String id, String name, String address, String contact, String website){
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		
		values.put(restaurantId, id);
		values.put(restaurantName, name);
		values.put(restaurantFavorite, 1);
		values.put(restaurantAddress, address);
		values.put(restaurantContact, contact);
		values.put(restaurantWebsite, website);
		values.put(noOfTimes, 0);
		db.insert(restaurantTable, null, values);
		db.close();
	}

	/*
	 * Cursor to derive all the columns from the restaurants table
	 */
	public Cursor all(){
		String[] from = {restaurantId, restaurantName, noOfTimes, restaurantAddress, restaurantContact};
		String order = restaurantName;

		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(restaurantTable, null, null, null, null, null, null);		
		return cursor;
	}

	/*
	 * Query to obtain the row of the table where the restaurant name = res_name.
	 * This query is then used in myClickHandler() to check if the user has previously added the restaurant to "Most visited" list. If not then the 
	 * count of NoOfTimes is updated to one. If the count is not null then it means that the user has add the restaurant earlier
	 * in which case the number of times is updated by one and written again into the database.
	 */
	public Cursor check_restaurant_visited_inDatabase(String res_id){

		SQLiteDatabase db = getReadableDatabase();
		String q = "SELECT * FROM restaurants WHERE _id = " +"\"" +res_id + "\""+ ";";
		Cursor mCursor = db.rawQuery(q, null);
		return mCursor;
	}
	
	public Cursor getStats() {
		
		SQLiteDatabase db = getReadableDatabase();
		String q = "SELECT ROWID as _id, RName, NoOfTimes, Rrating FROM (SELECT RName, SUM(NoOfTimes) as NoOfTimes, SUM(CASE WHEN Rrating > 0 THEN Rrating ELSE 0 END)*1.0/COUNT(CASE WHEN Rrating > 0 THEN Rrating ELSE 0 END)  as Rrating FROM restaurants GROUP BY LOWER(RName));";
		Cursor mCursor = db.rawQuery(q, null);
		return mCursor;
		
	}
	

	/*
	 * Query to obtain the row of the table where the restaurant name = res_name.
	 * This query is then used in myClickHandler() to check if the user has previously added the restaurant to "Most visited" list. If not then the 
	 * count of NoOfTimes is updated to one. If the count is not null then it means that the user has add the restaurant earlier
	 * in which case the number of times is updated by one and written again into the database.
	 */
	public Cursor check_restaurant_favorite_inDatabase(String res_id){

		SQLiteDatabase db = getReadableDatabase();
		String q = "SELECT * FROM restaurants WHERE _id = " +"\"" +res_id + "\""+ ";";
		Cursor mCursor = db.rawQuery(q, null);
		return mCursor;
	}

	/*
	 * Query to get the RestaurantId of restaurant listed as favorite restaurant
	 * 
	 */
	public Cursor getFavoriteRestaurantNames(){
		SQLiteDatabase db = getReadableDatabase();
		//Cursor cursor = db.query(restaurantTable, new String[] {"RName", "NoOfTimes"}, null, null, null, null, "NoOfTimes ASC");
		String q = "SELECT RName, _id, Rrating FROM restaurants WHERE RFavorite == 1 Order By Rrating DESC;";
		Cursor mCursor = db.rawQuery(q, null);
		return mCursor;
	}


	/*
	 * If the user selects the restaurant for a second or more times using the +1 button in the UI, update the number of times in the database.
	 * The method updates the "NoOfTimes" parameter against the restaurant selected by the user by "1". It takes NoOfTimes and the Restaurant 
	 * Name as the parameter
	 */
	public int updateTimesInDatabase(String res_id, int times){
		SQLiteDatabase db = getReadableDatabase();
		int count;
		String[] whereArgs = new String[]{(res_id)};
		try {  
			db.beginTransaction();
			ContentValues val = new ContentValues();
			val.put(noOfTimes, times);
			count = db.update(restaurantTable, val, restaurantId+"=?",whereArgs );//change??
			db.setTransactionSuccessful();
		} 
		finally{
			db.endTransaction();
		}
		return count;
	}

	public boolean favTimesInDatabase(String resId){
		return favTimesInDatabase(resId, true);
	}

	//Query to set the Favorite table according to the passed argument. The Favorite column is updated in the database against the restaurant name obtained as an argument. 
	public boolean favTimesInDatabase(String res_id, boolean setInFavorites){
		SQLiteDatabase db = getReadableDatabase();
		int count;
		String[] whereArgs = new String[]{(res_id)};
		try {  
			db.beginTransaction();
			ContentValues val = new ContentValues();
			val.put(restaurantFavorite, setInFavorites? 1 : 0);
			count = db.update(restaurantTable, val, restaurantId+"=?",whereArgs );
			db.setTransactionSuccessful();
		} 
		finally{
			db.endTransaction();
		}
		return true;
	}



	//Execute this code when the database file is accessed
	//	Fires when Activity uses getReadableDatabase or getWriteableDatabase
	@Override
	public void onOpen(SQLiteDatabase db){
		super.onOpen(db);
	}

	//Close your database connection
	@Override
	public synchronized void close(){
		SQLiteDatabase db = getWritableDatabase();
		if (db != null) {
			db.close();
		}
		super.close();
	}

	//Return the contact phone number of the restaurant obtain as argument.
	public Cursor check_restaurant_contact_inDatabase(String id) {
		SQLiteDatabase db = getReadableDatabase();
		String q = "SELECT RContact FROM restaurants WHERE _id = " +"\"" +id + "\""+ ";";
		Cursor mCursor = db.rawQuery(q, null);
		return mCursor;		
	}

	//Return the address of the restaurant obtained as argument.
	public Cursor check_restaurant_address_inDatabase(String id) {
		SQLiteDatabase db = getReadableDatabase();
		String q = "SELECT RAddress FROM restaurants WHERE _id = " +"\"" +id + "\""+ ";";
		Cursor mCursor = db.rawQuery(q, null);
		return mCursor;
	}

	//Return the address of the restaurant obtained as argument.
	public Cursor getRestaurantAddressesByName(String name) {
		SQLiteDatabase db = getReadableDatabase();
		String q = "SELECT RAddress FROM restaurants WHERE RName = " +"\"" +name + "\""+ ";";
		Cursor mCursor = db.rawQuery(q, null);
		return mCursor;
	}


	public Cursor get_website_inDatabase(String id) {
		SQLiteDatabase db = getReadableDatabase();
		String q = "SELECT Rwebsite FROM restaurants WHERE _id = " +"\"" +id + "\""+ ";";
		Cursor mCursor = db.rawQuery(q, null);
		return mCursor;
	}
	//Get a list of the top 10 favorite restaurants
	public Cursor getTopTenFavoriteRestaurantNames() {
		SQLiteDatabase db = getReadableDatabase();
		String q = "select RName, Rrating from restaurants where RFavorite == 1 order by Rrating DESC limit 10;";
		Cursor mCursor = db.rawQuery(q, null);
		return mCursor;
	}

	//Get a list of top 10 restaurants which the user visited the most.
	public Cursor getTopTenMostVisitedRestaurantNames() {
		SQLiteDatabase db = getReadableDatabase();
		String q = "select RName from restaurants where NoOfTimes != 0 order by NoOfTimes DESC limit 10;";
		Cursor mCursor = db.rawQuery(q, null);
		return mCursor;
	}

	//Update the ratings column against the Restaurant name obtained as an argument 
	public int updateRatingInDatabase(String res_id, float ratings){
		SQLiteDatabase db = getReadableDatabase();
		int count;
		String[] whereArgs = new String[]{(res_id)};
		try {  
			db.beginTransaction();
			ContentValues val = new ContentValues();
			val.put(restaurantRatings, ratings);
			count = db.update(restaurantTable, val, restaurantId+"=?",whereArgs );
			db.setTransactionSuccessful();
		} 
		finally{
			db.endTransaction();
		}
		return count;
	}

	//Delete the row for the Restaurant received as argument
	public boolean deleteRowInList(String res_id){
		SQLiteDatabase db = getReadableDatabase();
		String[] whereArgs = new String[]{(res_id)};
		try {  
			db.beginTransaction();
			db.delete(restaurantTable, restaurantId+"=?",whereArgs );
			db.setTransactionSuccessful();
		} 
		finally{
			db.endTransaction();
		}
		return true;
	}

	//Make the value of the RFavorite Column '0' against the Restaurant name obtained as argument
	public boolean removeRFavoriteInDatabase(String res_id){
		SQLiteDatabase db = getReadableDatabase();
		String[] whereArgs = new String[]{(res_id)};

		try {  
			db.beginTransaction();
			ContentValues val = new ContentValues();
			val.put("RFavorite", 0);
			val.put("Rrating", 0);
			db.update(restaurantTable, val, restaurantId+"=?", whereArgs );
			db.setTransactionSuccessful();
		} 
		finally{
			db.endTransaction();
		}

		return false;

	}

	//Make the 'NoOfTimes" entry 0 against the Restaurant obtained as argument
	public boolean removeMVisitedInDatabase(String id) {
		SQLiteDatabase db = getReadableDatabase();
		String[] whereArgs = new String[]{(id)};

		try {  
			db.beginTransaction();
			ContentValues val = new ContentValues();
			val.put("NoOfTimes", 0);
			db.update(restaurantTable, val, restaurantId+"=?", whereArgs );
			db.setTransactionSuccessful();
		} 
		finally{
			db.endTransaction();
		}

		return false;
	}
	/*
	 * Query to get the RestaurantId of restaurant and number of times the restaurant is visited in Descending Order of Times
	 * 
	 */
	public Cursor get_restaurantName_timesVisitedDesc(){
		SQLiteDatabase db = getReadableDatabase();
		//Cursor cursor = db.query(restaurantTable, new String[] {"RName", "NoOfTimes"}, null, null, null, null, "NoOfTimes ASC");
		String q = "SELECT _id, RName, NoOfTimes FROM restaurants WHERE NoOfTimes != 0 ORDER BY NoOfTimes DESC;";
		Cursor mCursor = db.rawQuery(q, null);

		return mCursor;
	}
	/*
	 * Query to get the RestaurantId of restaurant and number of times the restaurant is visited in Ascending Order of Times
	 * 
	 */
	public Cursor get_restaurantName_timesVisitedAsc(){
		SQLiteDatabase db = getReadableDatabase();
		//Cursor cursor = db.query(restaurantTable, new String[] {"RName", "NoOfTimes"}, null, null, null, null, "NoOfTimes ASC");
		String q = "SELECT _id, RName, NoOfTimes FROM restaurants WHERE NoOfTimes != 0 ORDER BY NoOfTimes ASC;";
		Cursor mCursor = db.rawQuery(q, null);

		return mCursor;
	}
	/*
	 * Query to get the RestaurantId of restaurant and number of times the restaurant is visited in Ascending Order of Name
	 * 
	 */
	public Cursor sortVisitedRestaurantsByNameAsc() {
		SQLiteDatabase db = getReadableDatabase();
		//Cursor cursor = db.query(restaurantTable, new String[] {"RName", "NoOfTimes"}, null, null, null, null, "NoOfTimes ASC");
		String q = "SELECT _id, RName, NoOfTimes FROM restaurants WHERE NoOfTimes != 0 ORDER BY RName ASC;";
		Cursor mCursor = db.rawQuery(q, null);

		return mCursor;
	}
	/*
	 * Query to get the RestaurantId of restaurant and number of times the restaurant is visited in Descending Order of Name
	 * 
	 */
	public Cursor sortVisitedRestaurantsByNameDesc() {
		SQLiteDatabase db = getReadableDatabase();
		//Cursor cursor = db.query(restaurantTable, new String[] {"RName", "NoOfTimes"}, null, null, null, null, "NoOfTimes ASC");
		String q = "SELECT _id, RName, NoOfTimes FROM restaurants WHERE NoOfTimes != 0 ORDER BY RName DESC;";
		Cursor mCursor = db.rawQuery(q, null);
		return mCursor;
	}


	/*
	 * Query to get the RestaurantId of restaurant and rating of the restaurant is visited in Descending Order of Rating
	 * 
	 */
	public Cursor get_restaurantNameRatingDesc(){
		SQLiteDatabase db = getReadableDatabase();
		//Cursor cursor = db.query(restaurantTable, new String[] {"RName", "NoOfTimes"}, null, null, null, null, "NoOfTimes ASC");
		String q = "SELECT _id, RName, Rrating FROM restaurants WHERE RFavorite == 1 ORDER BY Rrating DESC;";
		Cursor mCursor = db.rawQuery(q, null);

		return mCursor;
	}
	/*
	 * Query to get the RestaurantId of restaurant and rating of the restaurant is visited in Ascending Order of Rating
	 * 
	 */
	public Cursor get_restaurantNameRatingAsc(){
		SQLiteDatabase db = getReadableDatabase();
		//Cursor cursor = db.query(restaurantTable, new String[] {"RName", "NoOfTimes"}, null, null, null, null, "NoOfTimes ASC");
		String q = "SELECT _id, RName, Rrating FROM restaurants WHERE RFavorite == 1 ORDER BY Rrating ASC;";
		Cursor mCursor = db.rawQuery(q, null);

		return mCursor;
	}
	/*
	 * Query to get the RestaurantId of restaurant and Rating of the restaurant is visited in Ascending Order of Name
	 * 
	 */
	public Cursor sortFavRestaurantsByNameAsc() {
		SQLiteDatabase db = getReadableDatabase();
		//Cursor cursor = db.query(restaurantTable, new String[] {"RName", "NoOfTimes"}, null, null, null, null, "NoOfTimes ASC");
		String q = "SELECT _id, RName, Rrating FROM restaurants WHERE RFavorite == 1 ORDER BY RName ASC;";
		Cursor mCursor = db.rawQuery(q, null);

		return mCursor;
	}
	/*
	 * Query to get the RestaurantId of restaurant and Rating of the restaurant is visited in Descending Order of Name
	 * 
	 */
	public Cursor sortFavRestaurantsByNameDesc() {
		SQLiteDatabase db = getReadableDatabase();
		//Cursor cursor = db.query(restaurantTable, new String[] {"RName", "NoOfTimes"}, null, null, null, null, "NoOfTimes ASC");
		String q = "SELECT _id, RName, Rrating FROM restaurants WHERE RFavorite == 1 ORDER BY RName DESC;";
		Cursor mCursor = db.rawQuery(q, null);
		return mCursor;
	}


}
