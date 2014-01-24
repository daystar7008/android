package com.sukraa.testinfo;

import java.util.ArrayList;

import org.sukraa.testinfo.db.TestAdapter;
import org.sukraa.testinfo.db.Utility;

import com.sukraa.testinfo.beans.Profile;
import com.sukraa.testinfo.beans.Test;
import com.sukraa.testinfo.beans.Item.ItemType;
import com.sukraa.testinfo.listeners.MenuListener;
import com.sukraa.testinfo.util.AutoCompleteAdapter;
import com.sukraa.testinfo.util.CustomTestListAdapter;

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
import android.view.Menu;
import android.view.View;
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

public class TestActivity extends Activity implements
		ListView.OnItemClickListener, MenuListener,
		TextWatcher, ListView.OnItemLongClickListener {

	private ListView listViewTest = null;
	private ArrayList<Test> tests = null;
	private String selectedDeptCode = null;
	private String selectedDeptName = null;
	private String selectedProfCode = null;
	private Intent intent = null;
	private TestAdapter mDbHelper = null;
	
	private TextView tvTest = null;
	private TextView tvProfCode, tvProfAmount;
	private AutoCompleteTextView autoComplete = null;
	private ImageView imgViewSearch = null;
	private Profile profile;
	
	private Button departmentMenu, testMenu, profileMenu, favoriteMenu;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_test);
		
		intent = getIntent();
		selectedDeptCode = intent.getStringExtra("selectedDeptCode");
		selectedDeptName = intent.getStringExtra("selectedDeptName");
		
		tvTest = (TextView)findViewById(R.id.tvTest);
		
		profile = (Profile)intent.getSerializableExtra("profile");
		if(profile != null){
			tvProfCode =  (TextView)findViewById(R.id.tvCode);
			tvProfAmount =  (TextView)findViewById(R.id.tvAmount);
			
			tests = (ArrayList<Test>) profile.getTests();
			tvTest.setText(profile.getProfileName());
			selectedProfCode = profile.getProfileCode();
			
			tvProfCode.setText(tvProfCode.getText().toString() + " " + profile.getProfileCode());
			tvProfCode.setVisibility(TextView.VISIBLE);
			
			tvProfAmount.setText(tvProfAmount.getText().toString() + " Rs. " + String.valueOf((int)profile.getAmount()));
			tvProfAmount.setVisibility(TextView.VISIBLE);
		}
		
		final InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		
		listViewTest = (ListView)findViewById(R.id.listViewTest);
		listViewTest.setOnItemClickListener(this);
		listViewTest.setOnItemLongClickListener(this);
		
		autoComplete = (AutoCompleteTextView)findViewById(R.id.autoComplete);
		autoComplete.addTextChangedListener(this);
		autoComplete.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				TextView tv = (TextView)arg1;
				for(Test test : tests){
					if(tv.getText().equals(test.getTestName())){
						imm.hideSoftInputFromWindow(autoComplete.getWindowToken(), 0);
						autoComplete.setText("");
						autoComplete.setVisibility(AutoCompleteTextView.GONE);
						tvTest.setVisibility(TextView.VISIBLE);
						Intent intent = new Intent(getApplicationContext(), TestDescriptionActivity.class);
						intent.putExtra("test", test);
						startActivity(intent);
						break;
					}
				}
			}
		});
		
		if(selectedDeptName != null){
			tvTest.setText(selectedDeptName);
		}
		
		imgViewSearch = (ImageView)findViewById(R.id.imgViewSearch);
		imgViewSearch.setOnClickListener(new ImageView.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(tvTest.getVisibility() == TextView.VISIBLE){
					autoComplete.setVisibility(AutoCompleteTextView.VISIBLE);
					tvTest.setVisibility(TextView.GONE);
					autoComplete.requestFocus();
					imm.showSoftInput(autoComplete, InputMethodManager.SHOW_FORCED);
				}
				else{
					autoComplete.setVisibility(AutoCompleteTextView.GONE);
					tvTest.setVisibility(TextView.VISIBLE);
					autoComplete.setText("");
					imm.hideSoftInputFromWindow(autoComplete.getWindowToken(), 0);
				}
			}
		});
		
		departmentMenu = (Button)findViewById(R.id.btnDeptMenu);
		departmentMenu.setOnClickListener(this);
		
		testMenu = (Button)findViewById(R.id.btnTestMenu);
		//testMenu.setOnClickListener(this);
		testMenu.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_menu_selected));
		
		profileMenu = (Button)findViewById(R.id.btnProfileMenu);
		profileMenu.setOnClickListener(this);
		
		favoriteMenu = (Button)findViewById(R.id.btnFavoriteMenu);
		favoriteMenu.setOnClickListener(this);
		
		if(tests == null){
			fetchTests();
		}
		populateListView();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.department, menu);
		return true;
	}
	
	private void fetchTests(){
		tests = new ArrayList<Test>();
		try{
			String query = "";
			if(selectedDeptCode != null){
				query = "SELECT TsMt_Dept_Code, TsMt_Test_Code, TsMt_Test_Name, " +
							   "TsMt_Test_Amount, TsMt_Test_Unit, TsMt_Remarks, TsMt_Test_Desc, " +
							   "TsMt_Reference_Value, TsMt_Critical_Value " +
						  "FROM TsMt_Test_Mast_T " +
						 "WHERE TsMt_Dept_Code='" + selectedDeptCode + "'";
			}
			else if(selectedDeptCode == null){
				query = "SELECT TsMt_Dept_Code, TsMt_Test_Code, TsMt_Test_Name, " +
							   "TsMt_Test_Amount, TsMt_Test_Unit, TsMt_Remarks, TsMt_Test_Desc, " +
						  	   "TsMt_Reference_Value, TsMt_Critical_Value " +
						  "FROM TsMt_Test_Mast_T";
			}
			
			mDbHelper = new TestAdapter(this).createDatabase().open();
			
			//executes query and gets the results into cursor
			Cursor testdata = mDbHelper.getTestData(query);

			if(testdata.getCount() <= 0){
				Toast.makeText(this, "No Records Found", Toast.LENGTH_SHORT).show();
				return;
			}
			do{
				//parses columns with its exact name 
				String deptCode = Utility.getColumnValue(testdata, "TsMt_Dept_Code");
				String testCode = Utility.getColumnValue(testdata, "TsMt_Test_Code");
				String testName = Utility.getColumnValue(testdata, "TsMt_Test_Name");
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
				test.setTestName(testName);
				test.setAmount(Double.valueOf(testAmount));
				test.setRemarks(remarks);
				test.setDesc(desc);
				//test.setUnit(Double.parseDouble(testUnit));
				test.setReferenceValue(refValue);
				test.setCriticalValue(criticalValue);
				
				tests.add(test);
				Log.d("Code", testCode + ":" + testName);
			}while(testdata.moveToNext());
			mDbHelper.close();
			testdata.close();
		}
		catch (Exception e) {
			Log.e("Connection problem", e.toString());
			Toast.makeText(getApplicationContext(), "Couldn't connect to database", Toast.LENGTH_LONG).show();
		}
	}
	
	private void populateListView(){
		if(tests != null){
			boolean amountLabel = true;
			if(profile != null){
				amountLabel = false;
			}
			ArrayAdapter<Test> adapter = new CustomTestListAdapter(this, R.layout.view_test, tests, amountLabel);
			
			listViewTest.setChoiceMode(ListView.CHOICE_MODE_NONE);
			listViewTest.setAdapter(adapter);
		}
	}
	
	public void showDescription(Test test){
		Intent intent = new Intent(this, TestDescriptionActivity.class);
		intent.putExtra("test", test);
		startActivity(intent);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mDbHelper != null) {
			mDbHelper.close();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int index, long arg3) {
		showDescription(tests.get(index));
	}
	
	@Override
	public void onDepartmentClick() {
		Intent intent = new Intent(this, DepartmentActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	@Override
	public void onTestClick() {
		
	}

	@Override
	public void onProfileClick() {
		Intent intent = new Intent(this, ProfileActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	@Override
	public void onFavoriteClick() {
		Intent intent = new Intent(this, FavoriteActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
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
			onFavoriteClick();
			break;

		default:
			break;
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		AutoCompleteAdapter adapter = null;
		if(selectedProfCode != null){
			adapter = new AutoCompleteAdapter(this,
					android.R.layout.simple_spinner_dropdown_item, s.toString(), ItemType.TEST_IN_PROFILE);
		}
		else {
			adapter = new AutoCompleteAdapter(this,
					android.R.layout.simple_spinner_dropdown_item, s.toString(), ItemType.TEST);
		}
		
		if(selectedDeptCode != null){
			adapter.addFilter("TsMt_Dept_Code", selectedDeptCode);
		}
		
		if(selectedProfCode != null){
			adapter.addFilter("PfDt_Prof_Code", selectedProfCode);
		}
		
		autoComplete.setAdapter(adapter);
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View v, int index, long arg3) {
		final int position = index;
		final String testName = tests.get(position).getTestName();
		try {
			
			String countQuery = "SELECT COUNT(*) AS count FROM FvMt_Favorite_Mast_T " +
					"WHERE FvMt_Name ='" + testName + "' AND FvMt_Type = 'T'";
			
			mDbHelper = new TestAdapter(this).createDatabase().open();
			Cursor countData = mDbHelper.getTestData(countQuery);
			final int count = Integer.parseInt(Utility.getColumnValue(countData, "count"));
			
			TextView view = new TextView(this);
			view.setTextSize(getResources().getDimension(R.dimen.font_small));
			view.setPadding(5, 5, 5, 5);
			view.setTextColor(getResources().getColor(R.color.font));
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			
			if(count == 0){
				builder.setView(view);
				view.setText("Add \"" + tests.get(position).getTestName().trim() + "\" to Favorites?");
			}
			else{
				view.setText("Remove \"" + tests.get(position).getTestName().trim() + "\" from Favorites?");
				builder.setView(view);
			}
			
			builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(count == 0){
						boolean status = mDbHelper.addFavorite(testName, "T");
						if(status){
							//Toast.makeText(DepartmentActivity.this, "\"" + deptName.trim() + "\"Added to Favorites", Toast.LENGTH_SHORT).show();
							fetchTests();
							populateListView();
						}
						else{
							Toast.makeText(TestActivity.this, "Not Added To Favorites", Toast.LENGTH_SHORT).show();
						}
					}
					else{
						boolean status = mDbHelper.removeFavorite(testName, "T");
						if(status){
							//Toast.makeText(DepartmentActivity.this, "\"" + deptName.trim() + "\"Removed From Favorites", Toast.LENGTH_SHORT).show();
							fetchTests();
							populateListView();
						}
						else{
							Toast.makeText(TestActivity.this, "Not Removed From Favorites", Toast.LENGTH_SHORT).show();
						}
					}
				}
			});
			
			builder.setNegativeButton("No", null);
			
			mDbHelper.close();
			builder.show();
			
		} catch (Exception e) {
			Log.e("favorite", e.toString());
		}
		return true;
	}
	
}
