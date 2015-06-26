package org.aars.android.wallet;

import java.util.ArrayList;
import java.util.List;

import org.aars.android.beans.Transaction;
import org.aars.android.beans.Transaction.Type;
import org.aars.android.db.TestAdapter;
import org.aars.android.db.Utility;
import org.aars.android.expensemanager.util.CustomTransListAdapter;
import org.aars.android.expensemanager.util.TransactionClickListener;
import org.aars.android.wallet.R;

import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class IncomeFragment extends Fragment {

	private CustomTransListAdapter incomeAdapter;
	private List<Transaction> incomes;
	private ListView listViewIncome;
	private TextView tvTotalIncome;
	private String viewMonth = "";
	private ImageButton btnAddIncome, btnRemoveIncome;
	private TransactionClickListener transClickListener;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.layout_income, container, false);
		
		transClickListener = new TransactionClickListener(getActivity(), this);
		
		listViewIncome = (ListView)view.findViewById(R.id.listViewIncome);
		tvTotalIncome = (TextView)view.findViewById(R.id.tvTotalIncome);
		
		btnAddIncome = (ImageButton)view.findViewById(R.id.btnAddIncome);
        btnAddIncome.setOnClickListener(transClickListener);
		
        btnRemoveIncome = (ImageButton)view.findViewById(R.id.btnRemoveIncome);
        btnRemoveIncome.setOnClickListener(transClickListener);
        
        fetchIncomes();
        
		// Inflate the layout for this fragment
		return view;
	}
	
	public void fetchIncomes(){
		incomes = new ArrayList<Transaction>();
		int totalIncome = 0;
		TestAdapter testAdapter = null;
		try {
			testAdapter = new TestAdapter(getActivity()).createDatabase().open();
			
			String query = "";
			if(viewMonth != null || viewMonth != ""){
				query = "SELECT id, name, date, amount FROM incomes WHERE date LIKE '%" + viewMonth + "'"
						+ " ORDER BY date(date) DESC";
			}
			else{
				query = "SELECT id, name, date, amount FROM incomes ORDER BY date(date) DESC ";
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
			Log.e("IncomeFragment - fetchIncomes()", e.toString());
		}
	}
	
	private void populateIncomes(List<Transaction> incomes){
		incomeAdapter = new CustomTransListAdapter(getActivity(), R.layout.view_transaction_multi_selection, incomes);
		listViewIncome.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listViewIncome.setAdapter(incomeAdapter);
	}
	
	public void setTotalIncome(int total){
		tvTotalIncome.setText(String.valueOf(total));
	}
	
	public CustomTransListAdapter getIncomeAdapter() {
		return incomeAdapter;
	}

	public void setIncomeAdapter(CustomTransListAdapter incomeAdapter) {
		this.incomeAdapter = incomeAdapter;
	}

	public List<Transaction> getIncomes() {
		return incomes;
	}

	public void setIncomes(List<Transaction> incomes) {
		this.incomes = incomes;
	}

	public TextView getTvTotalIncome() {
		return tvTotalIncome;
	}

	public void setTvTotalExpense(TextView tvTotalIncome) {
		this.tvTotalIncome = tvTotalIncome;
	}
	
}
