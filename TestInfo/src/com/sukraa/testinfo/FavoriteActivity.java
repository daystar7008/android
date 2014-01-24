package com.sukraa.testinfo;

import java.util.ArrayList;
import java.util.List;

import org.sukraa.testinfo.db.TestAdapter;
import org.sukraa.testinfo.db.Utility;

import com.sukraa.testinfo.beans.Favorite;
import com.sukraa.testinfo.beans.Profile;
import com.sukraa.testinfo.beans.Test;
import com.sukraa.testinfo.beans.Item.ItemType;
import com.sukraa.testinfo.listeners.MenuListener;
import com.sukraa.testinfo.util.AutoCompleteAdapter;
import com.sukraa.testinfo.util.CustomFavListAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class FavoriteActivity extends Activity implements MenuListener,
		ListView.OnItemClickListener, 
		ListView.OnItemLongClickListener,
		TextWatcher,
		OnClickListener {

	private TextView tvHead;
	private ListView listViewFavorites;
	private Button departmentMenu, testMenu, profileMenu, favoriteMenu;
	
	private List<Favorite> favorites;
	
	private TestAdapter testAdapter;
	private AutoCompleteTextView autoComplete = null;
	private ImageView imgViewSearch;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_dept);
		
		tvHead = (TextView)findViewById(R.id.tvDept);
		tvHead.setText("Favorites");
		
		listViewFavorites = (ListView)findViewById(R.id.listViewDept);
		listViewFavorites.setOnItemClickListener(this);
		listViewFavorites.setOnItemLongClickListener(this);
		
		final InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		
		autoComplete = (AutoCompleteTextView)findViewById(R.id.autoComplete);
		autoComplete.addTextChangedListener(this);
		autoComplete.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				TextView tv = (TextView)arg1;
				for(Favorite fav : favorites){
					
					final String favName = fav.getName();
					final String favType = fav.getType();
					
					Log.d("auto complete Item Click", favName + " : " + favType);
					
					if(tv.getText().equals(favName)){
						imm.hideSoftInputFromWindow(autoComplete.getWindowToken(), 0);
						autoComplete.setText("");
						autoComplete.setVisibility(AutoCompleteTextView.GONE);
						tvHead.setVisibility(TextView.VISIBLE);
						
						if(favType.trim().equals("P")){
							openProfile(favName);
						}
						else if(favType.trim().equals("D")){
							openDepartment(favName);
						}
						else if(favType.trim().equals("T")){
							openTest(favName);
						}
						break;
					}
				}
			}
		});
		
		imgViewSearch = (ImageView)findViewById(R.id.imgViewSearch);
		imgViewSearch.setOnClickListener(new ImageView.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(tvHead.getVisibility() == TextView.VISIBLE){
					autoComplete.setVisibility(AutoCompleteTextView.VISIBLE);
					tvHead.setVisibility(TextView.GONE);
					autoComplete.requestFocus();
					imm.showSoftInput(autoComplete, InputMethodManager.SHOW_FORCED);
				}
				else{
					autoComplete.setVisibility(AutoCompleteTextView.GONE);
					tvHead.setVisibility(TextView.VISIBLE);
					autoComplete.setText("");
					imm.hideSoftInputFromWindow(autoComplete.getWindowToken(), 0);
				}
			}
		});
		
		departmentMenu = (Button)findViewById(R.id.btnDeptMenu);
		departmentMenu.setOnClickListener(this);
		
		testMenu = (Button)findViewById(R.id.btnTestMenu);
		testMenu.setOnClickListener(this);
		
		profileMenu = (Button)findViewById(R.id.btnProfileMenu);
		profileMenu.setOnClickListener(this);
		
		favoriteMenu = (Button)findViewById(R.id.btnFavoriteMenu);
		//favoriteMenu.setOnClickListener(this);
		favoriteMenu.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_menu_selected));
		
		fetchFavorites();
		populateListView();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (testAdapter != null) {
			testAdapter.close();
		}
	}
	
	private void fetchFavorites(){
		favorites = new ArrayList<Favorite>();
		try{

			String query = "SELECT FvMt_Name, FvMt_Type FROM FvMt_Favorite_Mast_T";

			testAdapter = new TestAdapter(this).createDatabase().open();
			
			//executes query and gets the results into cursor
			Cursor testdata = testAdapter.getTestData(query);

			if(testdata.getCount() <= 0){
				Toast.makeText(this, "No Records Found", Toast.LENGTH_SHORT).show();
				return;
			}
			do{
				//parses columns with its exact name 
				String name = Utility.getColumnValue(testdata, "FvMt_Name");
				String type = Utility.getColumnValue(testdata, "FvMt_Type");
				
				//updating user names and codes to arraylist
				favorites.add(new Favorite(name, type));
				
				Log.d("Fav", name + ":" + type);
			}while(testdata.moveToNext());
			
			testdata.close();
			//closes the database connection
			testAdapter.close();
		}
		catch (Exception e) {
			Toast.makeText(getApplicationContext(), "Couldn't connect to database", Toast.LENGTH_LONG).show();
		}
	}
	
	private void populateListView(){
		ArrayAdapter<Favorite> adapter = new CustomFavListAdapter(this, R.layout.view_favorite, favorites);
		listViewFavorites.setChoiceMode(ListView.CHOICE_MODE_NONE);
		listViewFavorites.setAdapter(adapter);
	}
	
	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		autoComplete.setAdapter(new AutoCompleteAdapter(this,
				android.R.layout.simple_spinner_dropdown_item, s.toString(), ItemType.FAVORITE));
	}
	
	@Override
	public void onDepartmentClick() {
		Intent intent = new Intent(this, DepartmentActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	@Override
	public void onTestClick() {
		Intent intent = new Intent(this, TestActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	@Override
	public void onProfileClick() {
		Intent intent = new Intent(this, ProfileActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	@Override
	public void onFavoriteClick() {
		//Toast.makeText(getApplicationContext(), "Favorites Menu", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnDeptMenu:
			onDepartmentClick();
			break;
		case R.id.btnTestMenu:
			onTestClick();
			break;
		case R.id.btnProfileMenu:
			onProfileClick();
			break;
		case R.id.btnFavoriteMenu:
			//onFavoriteClick();
			break;

		default:
			break;
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3) {
		final String favName = favorites.get(position).getName();
		final String favType = favorites.get(position).getType();
		
		if(favType.trim().equals("P")){
			openProfile(favName);
		}
		else if(favType.trim().equals("D")){
			openDepartment(favName);
		}
		else if(favType.trim().equals("T")){
			openTest(favName);
		}
	}
	
	private void openDepartment(String deptName){
		testAdapter = new TestAdapter(this).createDatabase().open();
		String query = "";
		query = "SELECT DpMt_Dept_Code FROM DpMt_Dept_Mast_T " +
				 "WHERE DpMt_Dept_Name = '" + deptName + "'";
		Cursor testdata = testAdapter.getTestData(query);
		
		String selectedDeptCode = Utility.getColumnValue(testdata, "DpMt_Dept_Code");
		String selectedDeptName = deptName;
		
		Intent intent = new Intent(getApplicationContext(), TestActivity.class);
		intent.putExtra("selectedDeptCode", selectedDeptCode);
		intent.putExtra("selectedDeptName", selectedDeptName);
		startActivity(intent);
	}
	
	private void openProfile(String profName){
		try {
			String query = "SELECT PfMt_Prof_Code, PfMt_Prof_Name, PfMt_Amount, PfMt_Remarks " +
							 "FROM PfMt_Prof_Mast_T " +
							"WHERE PfMt_Prof_Name = '" + profName + "'";
			testAdapter = new TestAdapter(this).createDatabase().open();
			
			//executes query and gets the results into cursor
			Cursor testdata = testAdapter.getTestData(query);

			if(testdata.getCount() <= 0){
				Toast.makeText(this, "No Records Found", Toast.LENGTH_SHORT).show();
				return;
			}
			
			String profileCode = Utility.getColumnValue(testdata, "PfMt_Prof_Code");
			String profileName = Utility.getColumnValue(testdata, "PfMt_Prof_Name");
			String amount = Utility.getColumnValue(testdata, "PfMt_Amount");
			String remarks = Utility.getColumnValue(testdata, "PfMt_Remarks");
			
			Log.d("Profile", profileCode + " : " + profileName);
			
			{
				String subQuery = "SELECT TsMt_Dept_Code, TsMt_Test_Code, TsMt_Test_Name, TsMt_Test_Amount, TsMt_Test_Desc, " +
									     "TsMt_Remarks, TsMt_Test_Unit, TsMt_Critical_Value, TsMt_Reference_Value  FROM PfMt_Prof_Mast_T " +
								    "LEFT JOIN PfDt_Prof_Detail_T " +
									  "ON PfMt_Prof_Mast_T.PfMt_Prof_Code = PfDt_Prof_Detail_T.PfDt_Prof_Code " +
								    "LEFT JOIN  TsMt_Test_Mast_T " +
								      "ON PfDt_Prof_Detail_T.PfDt_Test_Code = TsMt_Test_Mast_T.TsMt_Test_Code " + 
								   "WHERE PfMt_Prof_Mast_T.PfMt_Prof_Code = '" + profileCode + "'";
				
				TestAdapter dbAdapter = new TestAdapter(this).createDatabase().open();
				Cursor cursor = dbAdapter.getTestData(subQuery);
				
				if(cursor.getCount() > 0){
					List<Test> tests = new ArrayList<Test>();
					do {
						Test test = new Test();
						
						String deptCode = Utility.getColumnValue(cursor, "TsMt_Dept_Code");
						String testCode = Utility.getColumnValue(cursor, "TsMt_Test_Code");
						String testName = Utility.getColumnValue(cursor, "TsMt_Test_Name");
						String testAmount = Utility.getColumnValue(cursor, "TsMt_Test_Amount");
						String testDesc = Utility.getColumnValue(cursor, "TsMt_Test_Desc");
						String testRemarks = Utility.getColumnValue(cursor, "TsMt_Remarks");
						String unit = Utility.getColumnValue(cursor, "TsMt_Test_Unit");
						String criticalValue = Utility.getColumnValue(cursor, "TsMt_Critical_Value");
						String refValue = Utility.getColumnValue(cursor, "TsMt_Reference_Value");
						
						Log.d("Profile - Test", testCode + " : " + testName);
						
						test.setDeptCode(deptCode);
						test.setDeptCode(deptCode);
						test.setTestCode(testCode);
						test.setTestName(testName);
						test.setAmount(Double.valueOf(testAmount));
						test.setRemarks(testRemarks);
						test.setDesc(testDesc);
						//test.setUnit(Double.parseDouble(testUnit));
						test.setReferenceValue(refValue);
						test.setCriticalValue(criticalValue);
						
						tests.add(test);
					} while (cursor.moveToNext());
					dbAdapter.close();
					
					Profile profile = new Profile();
					
					profile.setProfileCode(profileCode);
					profile.setProfileName(profileName);
					profile.setTests(tests);
					profile.setAmount(Double.valueOf(amount));
					profile.setRemarks(remarks);

					Intent intent = new Intent(getApplicationContext(), TestActivity.class);
					intent.putExtra("profile", profile);
					startActivity(intent);
				}
			}
			
			testdata.close();
			testAdapter.close();
		} catch (Exception e) {
			Log.e("Connection problem", e.toString());
			Toast.makeText(getApplicationContext(), "Couldn't connect to database", Toast.LENGTH_LONG).show();
		}
	}
	
	private void openTest(String testName){
		try{
			String query = "";
			query = "SELECT TsMt_Dept_Code, TsMt_Test_Code, TsMt_Test_Name, " +
					   "TsMt_Test_Amount, TsMt_Test_Unit, TsMt_Remarks, TsMt_Test_Desc, " +
					   "TsMt_Reference_Value, TsMt_Critical_Value " +
				  "FROM TsMt_Test_Mast_T " +
				 "WHERE TsMt_Test_Name ='" + testName + "'";
			
			testAdapter = new TestAdapter(this).createDatabase().open();
			
			//executes query and gets the results into cursor
			Cursor testdata = testAdapter.getTestData(query);

			if(testdata.getCount() <= 0){
				Toast.makeText(this, "No Records Found", Toast.LENGTH_SHORT).show();
				return;
			}
			
			//parses columns with its exact name 
			String deptCode = Utility.getColumnValue(testdata, "TsMt_Dept_Code");
			String testCode = Utility.getColumnValue(testdata, "TsMt_Test_Code");
			String tstName = Utility.getColumnValue(testdata, "TsMt_Test_Name");
			String testAmount = Utility.getColumnValue(testdata, "TsMt_Test_Amount");
			String testUnit = Utility.getColumnValue(testdata, "TsMt_Test_Unit");
			String remarks = Utility.getColumnValue(testdata, "TsMt_Remarks");
			String desc = Utility.getColumnValue(testdata, "TsMt_Test_Desc");
			String refValue = Utility.getColumnValue(testdata, "TsMt_Reference_Value");
			String criticalValue = Utility.getColumnValue(testdata, "TsMt_Critical_Value");
			
			Log.d("test", testName + "," + testCode + "," + testAmount + "," + testUnit);
			
			Test test = new Test();
			test.setDeptCode(deptCode);
			test.setTestCode(testCode);
			test.setTestName(tstName);
			test.setAmount(Double.valueOf(testAmount));
			test.setRemarks(remarks);
			test.setDesc(desc);
			//test.setUnit(Double.parseDouble(testUnit));
			test.setReferenceValue(refValue);
			test.setCriticalValue(criticalValue);
			
			Log.d("Code", testCode + ":" + testName);
			
			testAdapter.close();
			testdata.close();
			
			Intent intent = new Intent(this, TestDescriptionActivity.class);
			intent.putExtra("test", test);
			startActivity(intent);
		}
		catch (Exception e) {
			Log.e("Connection problem", e.toString());
			Toast.makeText(getApplicationContext(), "Couldn't connect to database", Toast.LENGTH_LONG).show();
		}
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View v, int index, long arg3) {
		final int position = index;
		final String favName = favorites.get(position).getName();
		final String favType = favorites.get(position).getType();
		try {
			
			TextView view = new TextView(this);
			view.setTextSize(getResources().getDimension(R.dimen.font_small));
			view.setPadding(5, 5, 5, 5);
			view.setTextColor(getResources().getColor(R.color.font));
			view.setText("Remove \"" + favName.trim() + "\" from Favorites");
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setView(view);
			
			builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					boolean status = testAdapter.removeFavorite(favName, favType);
					if(status){
						fetchFavorites();
						populateListView();
					}
					else{
						Toast.makeText(FavoriteActivity.this, "Not Removed From Favorites", Toast.LENGTH_SHORT).show();
					}
				}
			});
			
			builder.setNegativeButton("No", null);
			
			testAdapter.close();
			builder.show();
			
		} catch (Exception e) {
			Log.e("favorite", e.toString());
		}
		return true;
	}

}
