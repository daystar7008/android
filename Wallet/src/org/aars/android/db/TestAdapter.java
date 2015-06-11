package org.aars.android.db;

import java.io.IOException;

import org.aars.android.beans.Transaction;

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

	public boolean addIncome(Transaction trans) {
		try {
			open();
			ContentValues cv = new ContentValues();
			int currId = getCurrentId("incomes");
			cv.put("id", currId);
			cv.put("name", trans.getName());
			cv.put("amount", trans.getAmount());
			cv.put("date", trans.getDate());

			long id = mDb.insert("incomes", null, cv);
			if(id > 0){
				Log.d("add Income", "income saved - " + trans.getName());
				updateKey("incomes", currId + 1);
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
			Log.e("add income exception", ex.toString());
			return false;
		}
	}
	
	public boolean removeIncome(int id){
		try {
			open();
			ContentValues cv = new ContentValues();
			cv.put("id", id);

			int status = mDb.delete("incomes", " id = " + id, null);
			if(status > 0){
				Log.d("delete Income", "income deleted - " + String.valueOf(id));
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
			Log.e("delete income exception", ex.toString());
			return false;
		}
	}
	
	public boolean addExpense(Transaction trans) {
		try {
			open();
			ContentValues cv = new ContentValues();
			int currId = getCurrentId("expenses");
			cv.put("id", currId);
			cv.put("name", trans.getName());
			cv.put("amount", trans.getAmount());
			cv.put("date", trans.getDate());

			long id = mDb.insert("expenses", null, cv);
			if(id > 0){
				Log.d("add Expense", "expense saved - " + trans.getName());
				updateKey("expenses", currId + 1);
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
			Log.e("add expense exception", ex.toString());
			return false;
		}
	}
	
	public boolean removeExpense(int id){
		try {
			open();
			ContentValues cv = new ContentValues();
			cv.put("id", id);

			int status = mDb.delete("expenses", " id = " + id, null);
			if(status > 0){
				Log.d("delete Expenses", "expense deleted - " + String.valueOf(id));
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
			Log.e("delete expense exception", ex.toString());
			return false;
		}
	}
	
	public int getCurrentId(String type){
		try {
			Cursor cursor = getTestData("SELECT id FROM keys " +
										 "WHERE code = '" + type + "'");
			int id = Integer.parseInt(Utility.getColumnValue(cursor, "id"));
			Log.d("curr id", String.valueOf(id));
			return id;
		} catch (Exception e) {
			close();
			e.printStackTrace();
			Log.e("getting id caused exception", e.toString());
			return -1;
		}
	}
	
	public void updateKey(String type, int id){
		try {
			open();
			getTestData("UPDATE keys SET id = " + id +
						" WHERE code = '" + type + "'");
			close();
		} catch (Exception e) {
			close();
			e.printStackTrace();
			Log.e("check on favorites exception", e.toString());
		}
	}

}
