package org.sukraa.testinfo.db;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TestAdapter {
	protected static final String TAG = "DataAdapter";

	private final Context mContext;
	private SQLiteDatabase mDb;
	private DataBaseHelper mDbHelper;

	public TestAdapter(Context context) {
		this.mContext = context;
		mDbHelper = new DataBaseHelper(mContext);
	}

	public TestAdapter createDatabase() throws SQLException {
		try {
			//copyDataBase("testinfo");
			mDbHelper.createDataBase();
		} catch (IOException mIOException) {
			Log.e(TAG, mIOException.toString() + "  UnableToCreateDatabase");
			throw new Error("UnableToCreateDatabase");
		}
		return this;
	}

	public TestAdapter open() throws SQLException {
		try {
			mDbHelper.openDataBase();
			mDbHelper.close();
			mDb = mDbHelper.getReadableDatabase();
		} catch (SQLException mSQLException) {
			Log.e(TAG, "open >>" + mSQLException.toString());
			throw mSQLException;
		}
		return this;
	}
	
	/*private void copyDataBase(String dbname) throws IOException {
        // Open your local db as the input stream
        InputStream myInput = mContext.getAssets().open(dbname);
        // Path to the just created empty db
        String outFileName = "/data/data/com.sukraa.testinfo/databases/" + dbname;
        // Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);
        // transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        // Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }*/
	
	public void close() {
		if (mDbHelper != null) {
			mDbHelper.close();
			mDb.close();
		}
	}

	public Cursor getTestData(String query) {
		try {
			String sql = query;

			Cursor mCur = mDb.rawQuery(sql, null);
			if (mCur != null) {
				mCur.moveToNext();
			}
			return mCur;
		} catch (SQLException mSQLException) {
			Log.e(TAG, "getTestData >>" + mSQLException.toString());
			throw mSQLException;
		}
	}

	public boolean addFavorite(String name, String type) {
		try {
			open();
			ContentValues cv = new ContentValues();
			cv.put("FvMt_Name", name);
			cv.put("FvMt_Type", type);

			long id = mDb.insert("FvMt_Favorite_Mast_T", null, cv);
			if(id > 0){
				Log.d("addFavorite", "favorite saved - " + name + " : " + type);
				close();
				return true;
			}
			else{
				close();
				return false;
			}
		} catch (Exception ex) {
			close();
			ex.printStackTrace();
			Log.e("add favorite exception", ex.toString());
			return false;
		}
	}
	
	public boolean removeFavorite(String name, String type) {
		try {
			open();
			int affected = mDb.delete("FvMt_Favorite_Mast_T", "FvMt_Name = '" + name + "' AND FvMt_Type = '" + type + "'", null);
			if(affected >= 1){
				Log.d("removeFavorite", "favorite saved");
				close();
				return true;
			}
			else{
				close();
				return false;
			}
		} catch (Exception ex) {
			close();
			ex.printStackTrace();
			Log.e("remove favorite exception", ex.toString());
			return false;
		}
	}
	
	public boolean isExistInFavorites(String name, String type){
		try {
			open();
			Cursor cursor = getTestData("SELECT COUNT(*) AS count FROM FvMt_Favorite_Mast_T " +
										 "WHERE FvMt_Name = '" + name + "' AND FvMt_Type = '" + type + "'");
			int count = Integer.parseInt(Utility.getColumnValue(cursor, "count"));
			close();
			if(count > 0){
				return true;
			}
			else{
				return false;
			}
		} catch (Exception e) {
			close();
			e.printStackTrace();
			Log.e("check on favorites exception", e.toString());
			return false;
		}
	}

}
