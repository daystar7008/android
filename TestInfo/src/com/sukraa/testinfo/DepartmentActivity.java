package com.sukraa.testinfo;

import java.util.ArrayList;

import org.sukraa.testinfo.db.TestAdapter;
import org.sukraa.testinfo.db.Utility;

import com.sukraa.testinfo.beans.Department;
import com.sukraa.testinfo.beans.Item.ItemType;
import com.sukraa.testinfo.listeners.MenuListener;
import com.sukraa.testinfo.util.AutoCompleteAdapter;
import com.sukraa.testinfo.util.CustomDeptListAdapter;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DepartmentActivity extends Activity implements
		ListView.OnItemClickListener,
		ListView.OnItemLongClickListener,
		TextWatcher, MenuListener {

	private ListView listViewDept = null;
	private ArrayList<Department> departments = null;
	private TestAdapter mDbHelper;
	
	private TextView tvDept = null;
	private AutoCompleteTextView autoComplete = null;
	private ImageView imgViewSearch;
	
	private Button departmentMenu, testMenu, profileMenu, favoriteMenu;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_dept);
		
		listViewDept = (ListView)findViewById(R.id.listViewDept);
		listViewDept.setOnItemClickListener(this);
		listViewDept.setOnItemLongClickListener(this);
		
		final InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		
		autoComplete = (AutoCompleteTextView)findViewById(R.id.autoComplete);
		autoComplete.addTextChangedListener(this);
		autoComplete.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				TextView tv = (TextView)arg1;
				for(Department dept : departments){
					if(tv.getText().equals(dept.getDeptName())){
						imm.hideSoftInputFromWindow(autoComplete.getWindowToken(), 0);
						autoComplete.setText("");
						autoComplete.setVisibility(AutoCompleteTextView.GONE);
						tvDept.setVisibility(TextView.VISIBLE);
						Intent intent = new Intent(getApplicationContext(), TestActivity.class);
						intent.putExtra("selectedDeptCode", dept.getDeptCode());
						intent.putExtra("selectedDeptName", dept.getDeptName());
						startActivity(intent);
						break;
					}
				}
			}
		});
		
		tvDept = (TextView)findViewById(R.id.tvDept);
		
		imgViewSearch = (ImageView)findViewById(R.id.imgViewSearch);
		imgViewSearch.setOnClickListener(new ImageView.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(tvDept.getVisibility() == TextView.VISIBLE){
					autoComplete.setVisibility(AutoCompleteTextView.VISIBLE);
					tvDept.setVisibility(TextView.GONE);
					autoComplete.requestFocus();
					imm.showSoftInput(autoComplete, InputMethodManager.SHOW_FORCED);
				}
				else{
					autoComplete.setVisibility(AutoCompleteTextView.GONE);
					tvDept.setVisibility(TextView.VISIBLE);
					autoComplete.setText("");
					imm.hideSoftInputFromWindow(autoComplete.getWindowToken(), 0);
				}
			}
		});
		
		departmentMenu = (Button)findViewById(R.id.btnDeptMenu);
		//departmentMenu.setOnClickListener(this);
		departmentMenu.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_menu_selected));
		
		testMenu = (Button)findViewById(R.id.btnTestMenu);
		testMenu.setOnClickListener(this);
		
		profileMenu = (Button)findViewById(R.id.btnProfileMenu);
		profileMenu.setOnClickListener(this);
		
		favoriteMenu = (Button)findViewById(R.id.btnFavoriteMenu);
		favoriteMenu.setOnClickListener(this);
		
		fetchDepartments();
		populateListView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.menu_common, menu);
		return true;
	}
	
	private void fetchDepartments(){
		departments = new ArrayList<Department>();
		try{

			String query = "SELECT DpMt_Dept_Code,DpMt_Dept_Name, DpMt_Remarks FROM DpMt_Dept_Mast_T";

			mDbHelper = new TestAdapter(this).createDatabase().open();
			
			//executes query and gets the results into cursor
			Cursor testdata = mDbHelper.getTestData(query);

			if(testdata.getCount() <= 0){
				Toast.makeText(this, "No Records Found", Toast.LENGTH_SHORT).show();
				return;
			}
			do{
				//parses columns with its exact name 
				String code = Utility.getColumnValue(testdata, "DpMt_Dept_Code");
				String name = Utility.getColumnValue(testdata, "DpMt_Dept_Name");
				String desc = Utility.getColumnValue(testdata, "DpMt_Remarks");
				
				String countQuery = "SELECT COUNT(*) AS count FROM TsMt_Test_Mast_T " +
				"WHERE TsMt_Dept_Code='" + code.trim() + "'";
				
				Cursor countData = mDbHelper.getTestData(countQuery);
				
				int testCount = Integer.parseInt(Utility.getColumnValue(countData, "count"));
				
				//updating user names and codes to arraylist
				departments.add(new Department(code, name, testCount, desc));
				
				countData.close();
				Log.d("Dept", code + ":" + name);
			}while(testdata.moveToNext());
			
			testdata.close();
			//closes the database connection
			mDbHelper.close();

		}
		catch (Exception e) {
			Toast.makeText(getApplicationContext(), "Couldn't connect to database", Toast.LENGTH_LONG).show();
		}
	}
	
	private void populateListView(){
		ArrayAdapter<Department> adapter = new CustomDeptListAdapter(this, R.layout.view_department, departments);
		listViewDept.setChoiceMode(ListView.CHOICE_MODE_NONE);
		listViewDept.setAdapter(adapter);
	}
	
	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3) {
		if(departments.get(position).getTestCount() > 0){
			Intent intent = new Intent(this, TestActivity.class);
			intent.putExtra("selectedDeptCode", departments.get(position).getDeptCode());
			intent.putExtra("selectedDeptName", departments.get(position).getDeptName());
			Log.d("deptcode", departments.get(position).getDeptCode());
			startActivity(intent);
		}
		else{
			Toast.makeText(this, "No Tests Available", Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mDbHelper != null) {
			mDbHelper.close();
		}
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
				android.R.layout.simple_spinner_dropdown_item, s.toString(), ItemType.DEPARTMENT));
	}

	@Override
	public void onDepartmentClick() {
		
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
	public boolean onItemLongClick(AdapterView<?> arg0, View v, int index, long arg3) {
		final int position = index;
		final String deptName = departments.get(position).getDeptName();
		try {
			
			String countQuery = "SELECT COUNT(*) AS count FROM FvMt_Favorite_Mast_T " +
					"WHERE FvMt_Name ='" + deptName + "' AND FvMt_Type = 'D'";
			
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
				view.setText("Add \"" + departments.get(position).getDeptName().trim() + "\" to Favorites?");
			}
			else{
				view.setText("Remove \"" + departments.get(position).getDeptName().trim() + "\" from Favorites?");
				builder.setView(view);
			}
			
			builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(count == 0){
						boolean status = mDbHelper.addFavorite(deptName, "D");
						if(status){
							//Toast.makeText(DepartmentActivity.this, "\"" + deptName.trim() + "\"Added to Favorites", Toast.LENGTH_SHORT).show();
							fetchDepartments();
							populateListView();
						}
						else{
							Toast.makeText(DepartmentActivity.this, "Not Added To Favorites", Toast.LENGTH_SHORT).show();
						}
					}
					else{
						boolean status = mDbHelper.removeFavorite(deptName, "D");
						if(status){
							//Toast.makeText(DepartmentActivity.this, "\"" + deptName.trim() + "\"Removed From Favorites", Toast.LENGTH_SHORT).show();
							fetchDepartments();
							populateListView();
						}
						else{
							Toast.makeText(DepartmentActivity.this, "Not Removed From Favorites", Toast.LENGTH_SHORT).show();
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
