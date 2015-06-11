package org.aars.android.expensemanager;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.aars.android.db.TestAdapter;
import org.aars.android.db.Utility;
import org.aars.android.expensemanager.util.CustomTranSummaryAdapter;
import org.aars.android.expensemanager.util.CustomTransListAdapter;
import org.aars.android.expensemanager.util.TransactionLayout;
import org.aars.android.beans.Transaction;
import org.aars.android.beans.Transaction.Type;

import android.os.Bundle;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity implements ImageButton.OnClickListener {

	private TabHost tabHost;
	private ImageButton btnAddIncome, btnAddExpense, btnRemoveIncome, btnRemoveExpense;
	private TextView tvTotal, tvTotalIncome, tvTotalExpense;
	private Spinner spnFetchType;
	private AlertDialog.Builder builder;
	
	private ListView listViewIncome, listViewExpense, listSummary;
	
	private TransactionLayout transLayout;
	private List<Transaction> incomes, expenses, transactions;
	private List<String> modes;
	private CustomTransListAdapter incomeAdapter, expenseAdapter;
	private CustomTranSummaryAdapter summaryAdapter;
	
	private HashMap<String, String> viewModeMap;
	private String viewMonth = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_main);
		
		tabHost = getTabHost();
		// creates a tab named 'Doctor' and sets 'form_doc' as its content
        TabSpec tab1 = tabHost.newTabSpec("S").setIndicator("Summary", null)
				        		.setContent(R.id.summary);
        
        // creates a tab named 'Chemist' and sets 'form_chem' as its content
        TabSpec tab2 = tabHost.newTabSpec("I").setIndicator("Income", null)
        						.setContent(R.id.income);
        
        // creates a tab named 'Chemist' and sets 'form_chem' as its content
        TabSpec tab3 = tabHost.newTabSpec("E").setIndicator("Expense", null)
        						.setContent(R.id.expense);
        
        //adds tabs to the TabHost
        tabHost.addTab(tab1);
        tabHost.addTab(tab2);
        tabHost.addTab(tab3);
        
        //sets 0th tab as current tab
        tabHost.setCurrentTab(0);
        
        btnAddIncome = (ImageButton)findViewById(R.id.btnAddIncome);
        btnAddIncome.setOnClickListener(this);
        
        btnAddExpense = (ImageButton)findViewById(R.id.btnAddExpense);
        btnAddExpense.setOnClickListener(this);
        
        btnRemoveIncome = (ImageButton)findViewById(R.id.btnRemoveIncome);
        btnRemoveIncome.setOnClickListener(this);
        
        btnRemoveExpense = (ImageButton)findViewById(R.id.btnRemoveExpense);
        btnRemoveExpense.setOnClickListener(this);
        
        tvTotal = (TextView)findViewById(R.id.tvTotal);
        tvTotalIncome = (TextView)findViewById(R.id.tvTotalIncome);
        tvTotalExpense = (TextView)findViewById(R.id.tvTotalExpense);
        
        spnFetchType = (Spinner)findViewById(R.id.spnFetchType);
        spnFetchType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View view,
					int index, long arg3) {
				if(viewModeMap.containsKey(modes.get(index))){
					String[] dmy = viewModeMap.get(modes.get(index)).split("/");
					viewMonth = dmy[1] + "/" + dmy[2];
					setTotalIncome(0);
					setTotalExpense(0);
					fetchSummary();
					fetchIncomes();
					fetchExpenses();
				}
				else{
					viewMonth = "";
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
        
        listViewIncome = (ListView)findViewById(R.id.listViewIncome);
        listViewExpense = (ListView)findViewById(R.id.listViewExpense);
        listSummary = (ListView)findViewById(R.id.listSummary);
        
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
			
			@Override
			public void onTabChanged(String tabId) {
				if(tabHost.getCurrentTab() == 0){
					fetchSummary();
				}
				else if(tabHost.getCurrentTab() == 1){
					fetchIncomes();
				}
				else if(tabHost.getCurrentTab() == 2){
					fetchExpenses();
				}
			}
		});
        fetchViewModes();
        fetchSummary();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnAddExpense:
			displayAddTransaction(Type.EXPENSE);
			break;
		case R.id.btnAddIncome:
			displayAddTransaction(Type.INCOME);
			break;
		case R.id.btnRemoveIncome:
			removeIncome();
			break;
		case R.id.btnRemoveExpense:
			removeExpense();
			break;
		default:
			break;
		}
	}
	
	private void fetchIncomes(){
		incomes = new ArrayList<Transaction>();
		int totalIncome = 0;
		TestAdapter testAdapter = null;
		try {
			testAdapter = new TestAdapter(this).createDatabase().open();
			
			String query = "";
			if(viewMonth != null || viewMonth != ""){
				query = "SELECT id, name, date, amount FROM incomes WHERE date LIKE '%" + viewMonth + "'";
			}
			else{
				query = "SELECT id, name, date, amount FROM incomes";
			}
			
			Cursor cursor = testAdapter.getTestData(query);
			if(cursor.getCount() <= 0){
				//Toast.makeText(this, "No Records Found", Toast.LENGTH_SHORT).show();
				testAdapter.close();
				populateIncomes(new ArrayList<Transaction>());
				return;
			}
			do {
				Log.d("id", Utility.getColumnValue(cursor, "id"));
				Log.d("name", Utility.getColumnValue(cursor, "name"));
				Log.d("amount", Utility.getColumnValue(cursor, "amount"));
				Log.d("date", Utility.getColumnValue(cursor, "date"));
				
				int id = Integer.parseInt(Utility.getColumnValue(cursor, "id"));
				String name = Utility.getColumnValue(cursor, "name");
				int amount = Integer.parseInt(Utility.getColumnValue(cursor, "amount"));
				String date = Utility.getColumnValue(cursor, "date");
				
				totalIncome += amount;
				
				Transaction trans = new Transaction();
				trans.setName(name);
				trans.setType(Type.INCOME);
				trans.setId(id);
				trans.setAmount(amount);
				trans.setDate(date);
				
				incomes.add(trans);
			} while (cursor.moveToNext());
			
			setTotalIncome(totalIncome);
			
			testAdapter.close();
			populateIncomes(incomes);
		} catch (Exception e) {
			testAdapter.close();
			Log.e("MainActivity - fetchIncomes()", e.toString());
		}
	}
	
	private void populateIncomes(List<Transaction> incomes){
		incomeAdapter = new CustomTransListAdapter(this, R.layout.view_transaction_multi_selection, incomes);
		listViewIncome.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listViewIncome.setAdapter(incomeAdapter);
	}
	
	private void fetchExpenses(){
		expenses = new ArrayList<Transaction>();
		int totalExpense = 0;
		TestAdapter testAdapter = null;
		try {
			testAdapter = new TestAdapter(this).createDatabase().open();
			
			String query = "";
			if(viewMonth != null || viewMonth != ""){
				query = "SELECT id, name, date, amount FROM expenses WHERE date LIKE '%" + viewMonth + "'";
			}
			else{
				query = "SELECT id, name, date, amount FROM expenses";
			}
			Cursor cursor = testAdapter.getTestData(query);
			if(cursor.getCount() <= 0){
				//Toast.makeText(this, "No Records Found", Toast.LENGTH_SHORT).show();
				testAdapter.close();
				populateExpenses(new ArrayList<Transaction>());
				return;
			}
			do {
				Log.d("id", Utility.getColumnValue(cursor, "id"));
				Log.d("name", Utility.getColumnValue(cursor, "name"));
				Log.d("amount", Utility.getColumnValue(cursor, "amount"));
				Log.d("date", Utility.getColumnValue(cursor, "date"));
				
				int id = Integer.parseInt(Utility.getColumnValue(cursor, "id"));
				String name = Utility.getColumnValue(cursor, "name");
				int amount = Integer.parseInt(Utility.getColumnValue(cursor, "amount"));
				String date = Utility.getColumnValue(cursor, "date");
				
				totalExpense += amount;
				
				Transaction trans = new Transaction();
				trans.setName(name);
				trans.setType(Type.EXPENSE);
				trans.setId(id);
				trans.setAmount(amount);
				trans.setDate(date);
				
				expenses.add(trans);
			} while (cursor.moveToNext());
			
			setTotalExpense(totalExpense);
			
			testAdapter.close();
			populateExpenses(expenses);
		} catch (Exception e) {
			testAdapter.close();
			Log.e("MainActivity - fetchExpenses()", e.toString());
		}
	}
	
	private void populateExpenses(List<Transaction> expenses){
		expenseAdapter = new CustomTransListAdapter(this, R.layout.view_transaction_multi_selection, expenses);
		listViewExpense.setChoiceMode(ListView.CHOICE_MODE_NONE);
		listViewExpense.setAdapter(expenseAdapter);
	}
	
	private void fetchSummary(){
		transactions = new ArrayList<Transaction>();
		TestAdapter testAdapter = null;
		try {
			testAdapter = new TestAdapter(this).createDatabase().open();
			
			String query = "";
			if(viewMonth != null || viewMonth != ""){
				query = "SELECT id, name, date, amount, 'E' AS type FROM expenses WHERE date LIKE '%" + viewMonth + "'" +
						"UNION " +
						"SELECT id, name, date, amount, 'I' AS type FROM incomes WHERE date LIKE '%" + viewMonth + "'";
			}
			else{
				query = "SELECT id, name, date, amount, 'E' AS type FROM expenses " +
						"UNION " +
						"SELECT id, name, date, amount, 'I' AS type FROM incomes";
			}
			
			Cursor cursor = testAdapter.getTestData(query);
			if(cursor.getCount() <= 0){
				//Toast.makeText(this, "No Records Found", Toast.LENGTH_SHORT).show();
				testAdapter.close();
				return;
			}
			do {
				int id = Integer.parseInt(Utility.getColumnValue(cursor, "id"));
				String name = Utility.getColumnValue(cursor, "name");
				int amount = Integer.parseInt(Utility.getColumnValue(cursor, "amount"));
				String date = Utility.getColumnValue(cursor, "date");
				String type = Utility.getColumnValue(cursor, "type");
				
				Transaction trans = new Transaction();
				trans.setName(name);
				if(type.equals("I")){
					trans.setType(Type.INCOME);
				}
				else if(type.equals("E")){
					trans.setType(Type.EXPENSE);
				}
				trans.setId(id);
				trans.setAmount(amount);
				trans.setDate(date);
				
				transactions.add(trans);
			} while (cursor.moveToNext());
			
			testAdapter.close();
			populateSummary(transactions);
			calculateTotal(transactions);
		} catch (Exception e) {
			testAdapter.close();
			Log.e("MainActivity - fetchSummary()", e.toString());
		}
	}
	
	private void calculateTotal(List<Transaction> transes){
		int total = 0;
		for(Transaction tran : transes){
			if(tran.getType().equals(Type.INCOME)){
				total += tran.getAmount();
			}
			else if(tran.getType().equals(Type.EXPENSE)){
				total -= tran.getAmount();
			}
		}
		tvTotal.setText(String.valueOf(total));
		if(total < 0){
			tvTotal.setTextColor(getResources().getColor(R.color.red));
		}
		else{
			tvTotal.setTextColor(getResources().getColor(R.color.green));
		}
	}
	
	private void setTotalIncome(int total){
		tvTotalIncome.setText(String.valueOf(total));
	}
	
	private void setTotalExpense(int total){
		tvTotalExpense.setText(String.valueOf(total));
	}
	
	private void populateSummary(List<Transaction> transactions){
		summaryAdapter =  new CustomTranSummaryAdapter(this, R.layout.view_transaction_selection_none, transactions);
		listSummary.setChoiceMode(ListView.CHOICE_MODE_NONE);
		listSummary.setAdapter(summaryAdapter);
		listSummary.invalidate();
	}
	
	private void displayAddTransaction(Type type){
		final Type transType = type;
		transLayout = new TransactionLayout(this);
		builder = new AlertDialog.Builder(this);
		if(type.equals(Type.EXPENSE)){
			builder.setTitle("Add Expense");
		}
		else if(type.equals(Type.INCOME)){
			builder.setTitle("Add Income");
		}
		
		ScrollView view = new ScrollView(this);
		view.addView(transLayout.getLayout());
		
		builder.setView(view);
		builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String name = transLayout.getName().trim();
				int amount = transLayout.getAmount();
				
				if(name != null){
					if(!name.trim().equals("")){
						if(amount != 0){
							Transaction transaction = new Transaction();
							transaction.setName(name);
							transaction.setDate(transLayout.getDate());
							transaction.setAmount(amount);
							if(transType.equals(Type.EXPENSE)){
								addExpense(transaction);
							}
							else if(transType.equals(Type.INCOME)){
								addIncome(transaction);
							}
						}
						else{
							Toast.makeText(MainActivity.this, "Enter Amount", Toast.LENGTH_SHORT).show();
							displayAddTransaction(transType);
						}
					}
					else{
						Toast.makeText(MainActivity.this, "Enter Transaction Name", Toast.LENGTH_SHORT).show();
						displayAddTransaction(transType);
					}
				}
				else{
					Toast.makeText(MainActivity.this, "Enter Transaction Name", Toast.LENGTH_SHORT).show();
					displayAddTransaction(transType);
				}
			}
		});
		builder.setNegativeButton("Cancel", null);
		builder.show();
	}
	
	private void fetchViewModes(){
		TestAdapter testAdapter = null;
		modes = new ArrayList<String>();
		viewModeMap = new HashMap<String, String>();
		modes.add("Overall");
		try {
			testAdapter = new TestAdapter(this).createDatabase().open();
			
			String query = "SELECT DISTINCT date FROM incomes " +
						   "UNION " +
						   "SELECT DISTINCT date FROM expenses";
			Cursor cursor = testAdapter.getTestData(query);
			if(cursor.getCount() <= 0){
				testAdapter.close();
				return;
			}
			do{
				String date = Utility.getColumnValue(cursor, "date");
				String[] dateArr = date.split("/");
				String month = new DateFormatSymbols().getMonths()[Integer.parseInt(dateArr[1]) - 1];
				String year = dateArr[2];
				if(!modes.contains(month + ", " + year)){
					modes.add(month + ", " + year);
				}
				viewModeMap.put(month + ", " + year, date);
			}while(cursor.moveToNext());
			testAdapter.close();
			
			spnFetchType.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, modes));
		} catch (Exception e) {
			testAdapter.close();
			Log.e("MainActivity - getViewModes()", e.toString());
		}
	}
	
	private void addIncome(Transaction trans){
		TestAdapter testAdapter = null;
		try {
			testAdapter = new TestAdapter(this).createDatabase().open();
			if(testAdapter.addIncome(trans)){
				Toast.makeText(this, "Income Saved", Toast.LENGTH_SHORT).show();
				fetchViewModes();
				fetchIncomes();
				fetchSummary();
			}
			else{
				Toast.makeText(this, "Income Not Saved", Toast.LENGTH_SHORT).show();
			}
			testAdapter.close();
		} catch (Exception e) {
			testAdapter.close();
			Log.e("MainActivity - addIncome()", e.toString());
		}
	}
	
	private void removeIncome(){
		TestAdapter testAdapter = null;
		Transaction trans = null;
		try {
			testAdapter = new TestAdapter(this).createDatabase().open();
			for(Integer pos : incomeAdapter.getCheckedPositions()){
				testAdapter.removeIncome(incomes.get(pos).getId());
				trans = incomes.get(pos);
			}
			testAdapter.close();
			fetchViewModes();
			fetchIncomes();
			fetchSummary();
			listViewIncome.invalidate();
			
			if(trans != null){
				int tot = Integer.parseInt(tvTotalIncome.getText().toString()) - trans.getAmount();
				setTotalIncome(tot);
			}
		} catch (Exception e) {
			testAdapter.close();
			Log.e("MainActivity - removeIncome()", e.toString());
		}
	}
	
	private void addExpense(Transaction trans){
		TestAdapter testAdapter = null;
		try {
			testAdapter = new TestAdapter(this).createDatabase().open();
			if(testAdapter.addExpense(trans)){
				Toast.makeText(this, "Expense Saved", Toast.LENGTH_SHORT).show();
				fetchViewModes();
				fetchExpenses();
				fetchSummary();
			}
			else{
				Toast.makeText(this, "Expense Not Saved", Toast.LENGTH_SHORT).show();
			}
			testAdapter.close();
		} catch (Exception e) {
			testAdapter.close();
			Log.e("MainActivity - addExpense()", e.toString());
		}
	}
	
	private void removeExpense(){
		TestAdapter testAdapter = null;
		Transaction trans = null;
		try {
			testAdapter = new TestAdapter(this).createDatabase().open();
			for(Integer pos : expenseAdapter.getCheckedPositions()){
				testAdapter.removeExpense(expenses.get(pos).getId());
				trans = expenses.get(pos);
			}
			testAdapter.close();
			fetchViewModes();
			fetchExpenses();
			fetchSummary();
			listViewExpense.invalidate();
			
			if(trans != null){
				int tot = Integer.parseInt(tvTotalExpense.getText().toString()) - trans.getAmount();
				setTotalExpense(tot);
			}
		} catch (Exception e) {
			testAdapter.close();
			Log.e("MainActivity - removeExpense()", e.toString());
		}
	}
	
}
