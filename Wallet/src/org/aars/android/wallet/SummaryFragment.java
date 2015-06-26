package org.aars.android.wallet;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.aars.android.beans.Transaction;
import org.aars.android.beans.Transaction.Type;
import org.aars.android.db.TestAdapter;
import org.aars.android.db.Utility;
import org.aars.android.expensemanager.util.CustomTranSummaryAdapter;
import org.aars.android.wallet.R;

import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class SummaryFragment extends Fragment {
	
	private String viewMonth = "";
	private List<Transaction> transactions;
	private CustomTranSummaryAdapter summaryAdapter;
	private ListView listSummary;
	private List<String> modes;
	private HashMap<String, String> viewModeMap;
	private Spinner spnFetchType;
	private TextView tvTotal;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.layout_summary, container, false);
		
		tvTotal = (TextView)view.findViewById(R.id.tvTotal);
		listSummary = (ListView)view.findViewById(R.id.listSummary);
		
		spnFetchType = (Spinner)view.findViewById(R.id.spnFetchType);
        spnFetchType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View view,
					int index, long arg3) {
				if(viewModeMap.containsKey(modes.get(index))){
					String[] dmy = viewModeMap.get(modes.get(index)).split("/");
					viewMonth = dmy[1] + "/" + dmy[2];
					fetchSummary();
				}
				else{
					viewMonth = "";
					fetchSummary();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});

        fetchViewModes();
        fetchSummary();
        
		// Inflate the layout for this fragment
		return view;
	}
	
	private void fetchSummary(){
		transactions = new ArrayList<Transaction>();
		TestAdapter testAdapter = null;
		try {
			testAdapter = new TestAdapter(getActivity()).createDatabase().open();
			
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
	
	private void populateSummary(List<Transaction> transactions){
		summaryAdapter =  new CustomTranSummaryAdapter(getActivity(), R.layout.view_transaction_selection_none, transactions);
		listSummary.setChoiceMode(ListView.CHOICE_MODE_NONE);
		listSummary.setAdapter(summaryAdapter);
		listSummary.invalidate();
	}

	public void fetchViewModes(){
		TestAdapter testAdapter = null;
		modes = new ArrayList<String>();
		viewModeMap = new HashMap<String, String>();
		modes.add("Overall");
		try {
			testAdapter = new TestAdapter(getActivity()).createDatabase().open();
			
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
			
			spnFetchType.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, modes));
		} catch (Exception e) {
			testAdapter.close();
			Log.e("Summary Fragment - getViewModes()", e.toString());
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
	
}
