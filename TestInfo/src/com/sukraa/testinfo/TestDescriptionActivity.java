package com.sukraa.testinfo;

import java.util.ArrayList;
import java.util.List;

import org.sukraa.testinfo.db.TestAdapter;
import org.sukraa.testinfo.db.Utility;

import com.sukraa.testinfo.beans.Test;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TestDescriptionActivity extends Activity {

	private Test test;
	private Intent intent;
	private TextView tvTestDescHead;
	private TextView tvTestCode, tvDept, tvTestDesc, tvRelatedTests;
	private TextView tvTestAmount, tvRefValue, tvCriticalValue;
	private LinearLayout layoutRelatedTests;
	private ImageView imgFavTest;
	
	private List<Test> relatedTests;
	
	@Override
	public void onCreate(Bundle bundle){
		super.onCreate(bundle);
		setContentView(R.layout.layout_test_desc);
		
		intent = getIntent();
		test = (Test)intent.getSerializableExtra("test");
		
		tvTestDescHead = (TextView)findViewById(R.id.tvTestDescHead);
		tvTestDescHead.setText(test.getTestName());
		
		tvTestCode = (TextView)findViewById(R.id.tvTestCode);
		tvTestCode.setText(tvTestCode.getText().toString() + test.getTestCode());
		
		tvTestAmount = (TextView)findViewById(R.id.tvTestDescAmount);
		tvTestAmount.setText(tvTestAmount.getText().toString() + "Rs. " + Integer.toString((int)test.getAmount()));
		
		tvDept = (TextView)findViewById(R.id.tvDept);
		tvDept.setText(getDeptName(test.getDeptCode()));
		
		tvTestDesc = (TextView)findViewById(R.id.tvTestDesc);
		tvTestDesc.setText(test.getDesc());
		
		tvRefValue = (TextView)findViewById(R.id.tvRefVal);
		tvRefValue.setText(test.getReferenceValue());
		
		tvCriticalValue = (TextView)findViewById(R.id.tvCriticalVal);
		tvCriticalValue.setText(test.getCriticalValue());
		
		tvRelatedTests = (TextView)findViewById(R.id.tvRelatedTests);
		
		imgFavTest = (ImageView)findViewById(R.id.imgFavTest);
		imgFavTest.setOnClickListener(new ImageView.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switchFavorite();
			}
		});
		
		if(isFavorite()){
			imgFavTest.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.btn_star_big_on));
		}
		else{
			imgFavTest.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.btn_star_big_off));
		}
		
		layoutRelatedTests = (LinearLayout)findViewById(R.id.layoutRelatedTests);
		fetchRelatedTests();
		populateRelatedTests();
	}
	
	private boolean isFavorite(){
		TestAdapter testAdapter = null;
		try {
			testAdapter = new TestAdapter(this).createDatabase().open();
			String query = "SELECT COUNT(*) AS count FROM FvMt_Favorite_Mast_T " +
							"WHERE FvMt_Name = '" + test.getTestName() + "' AND FvMt_Type='T'";
			
			Cursor cursor = testAdapter.getTestData(query);
			int count = Integer.parseInt(Utility.getColumnValue(cursor, "count"));
			testAdapter.close();
			if(count <= 0){
				return false;
			}
			else{
				return true;
			}
		} catch (Exception e) {
			Log.e("Test Description", e.toString());
			testAdapter.close();
			return false;
		}
	}
	
	private void switchFavorite(){
		TestAdapter testAdapter = null;
		try {
			testAdapter = new TestAdapter(this).createDatabase().open();
			String query = "SELECT COUNT(*) AS count FROM FvMt_Favorite_Mast_T " +
							"WHERE FvMt_Name = '" + test.getTestName() + "' AND FvMt_Type='T'";
			
			Cursor cursor = testAdapter.getTestData(query);
			int count = Integer.parseInt(Utility.getColumnValue(cursor, "count"));
			if(count <= 0){
				if(testAdapter.addFavorite(test.getTestName(), "T")){
					imgFavTest.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.btn_star_big_on));
					Toast.makeText(this, "Added to Favorites", Toast.LENGTH_SHORT).show();
				}
			}
			else{
				if(testAdapter.removeFavorite(test.getTestName(), "T")){
					imgFavTest.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.btn_star_big_off));
					Toast.makeText(this, "Removed from Favorites", Toast.LENGTH_SHORT).show();
				}
			}
			testAdapter.close();
		} catch (Exception e) {
			Log.e("Test Description", e.toString());
			testAdapter.close();
		}
	}
	
	private String getDeptName(String deptCode){
		TestAdapter testAdapter = null;
		try {
			testAdapter = new TestAdapter(this).createDatabase().open();
			String query = "SELECT DpMt_Dept_Name FROM DpMt_Dept_Mast_T " +
							"WHERE DpMt_Dept_Code = '" + test.getDeptCode() + "'";
			
			Cursor cursor = testAdapter.getTestData(query);
			String dept = Utility.getColumnValue(cursor, "DpMt_Dept_Name");
			
			testAdapter.close();
			if(dept != null){
				return dept;
			}
			else{
				return deptCode;
			}
		} catch (Exception e) {
			Log.e("Test Description", e.toString());
			testAdapter.close();
			return deptCode;
		}
	}
	
	private void fetchRelatedTests(){
		relatedTests = new ArrayList<Test>();
		TestAdapter testAdapter = null;
		try {
			testAdapter = new TestAdapter(this).createDatabase().open();
			String query = "SELECT RlTs_Test_Code, RlTs_Rel_Test_Code, " +
								  "TsMt_Dept_Code, TsMt_Test_Code, TsMt_Test_Name, " +
								  "TsMt_Test_Amount, TsMt_Test_Unit, TsMt_Remarks, TsMt_Test_Desc, " +
							  	  "TsMt_Reference_Value, TsMt_Critical_Value " +
							 "FROM RlTs_Relate_Test_T " +
							 "LEFT JOIN TsMt_Test_Mast_T " +
							   "ON RlTs_Relate_Test_T.RlTs_Rel_Test_Code = TsMt_Test_Mast_T.TsMt_Test_Code " +
							"WHERE RlTs_Test_Code = '" + test.getTestCode() + "'";
			
			Cursor cursor = testAdapter.getTestData(query);
			
			if(cursor.getCount() <= 0){
				testAdapter.close();
				return;
			}
			
			do{
				//parses columns with its exact name 
				String deptCode = Utility.getColumnValue(cursor, "TsMt_Dept_Code");
				String testCode = Utility.getColumnValue(cursor, "TsMt_Test_Code");
				String testName = Utility.getColumnValue(cursor, "TsMt_Test_Name");
				String testAmount = Utility.getColumnValue(cursor, "TsMt_Test_Amount");
				String testUnit = Utility.getColumnValue(cursor, "TsMt_Test_Unit");
				String remarks = Utility.getColumnValue(cursor, "TsMt_Remarks");
				String desc = Utility.getColumnValue(cursor, "TsMt_Test_Desc");
				String refValue = Utility.getColumnValue(cursor, "TsMt_Reference_Value");
				String criticalValue = Utility.getColumnValue(cursor, "TsMt_Critical_Value");
				
				Log.d("Related test", testName + "," + testCode + "," + testAmount + "," + testUnit);
				
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
				
				relatedTests.add(test);
				Log.d("Code", testCode + ":" + testName);
			}while(cursor.moveToNext());
			
			testAdapter.close();
		} catch (Exception e) {
			Log.e("Test Description", e.toString());
			testAdapter.close();
		}
	}
	
	private void populateRelatedTests(){
		if(relatedTests == null || relatedTests.size() <= 0){
			layoutRelatedTests.setVisibility(LinearLayout.GONE);
			tvRelatedTests.setVisibility(LinearLayout.GONE);
			return;
		}
		
		LayoutParams param = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		for (Test test : relatedTests) {
			final Test t = test;
			TextView tv = new TextView(this);
			
			String str = test.getTestName();
			
			SpannableString content = new SpannableString(str);
			content.setSpan(new UnderlineSpan(), 0, str.length(), 0);
			
			tv.setText(content);
			tv.setTextColor(getResources().getColor(R.color.font_blue));
			tv.setSingleLine(true);
			tv.setPadding(10, 5, 10, 5);
			tv.setGravity(Gravity.CENTER_VERTICAL);
			tv.setLayoutParams(param);
			
			try {
				TestAdapter testAdapter = new TestAdapter(this);
				if(testAdapter.isExistInFavorites(test.getTestName(), "T")){
					Log.d("Fav check", "exists in fav");
					tv.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(android.R.drawable.btn_star_big_on), null);
				}
				testAdapter.close();
			} catch (Exception e) {
				Log.d("TestDescriptionActivity - check fav exception",  e.toString());
			}
			
			layoutRelatedTests.addView(tv);
			
			tv.setOnClickListener(new TextView.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(TestDescriptionActivity.this, TestDescriptionActivity.class);
					intent.putExtra("test", t);
					startActivity(intent);
					finish();
				}
			});
		}
	}
	
}
