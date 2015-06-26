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

public class ExpenseFragment extends Fragment {
	
	private CustomTransListAdapter expenseAdapter;
	private List<Transaction> expenses;
	private ListView listViewExpense;
	private TextView tvTotalExpense;
	private String viewMonth = "";
	private ImageButton btnAddExpense, btnRemoveExpense;
	private TransactionClickListener transClickListener;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.layout_expense, container, false);
		
		transClickListener = new TransactionClickListener(getActivity(), this);
		
		btnAddExpense = (ImageButton)view.findViewById(R.id.btnAddExpense);
        btnAddExpense.setOnClickListener(transClickListener);
        
        btnRemoveExpense = (ImageButton)view.findViewById(R.id.btnRemoveExpense);
        btnRemoveExpense.setOnClickListener(transClickListener);
        
		listViewExpense = (ListView)view.findViewById(R.id.listViewExpense);
		tvTotalExpense = (TextView)view.findViewById(R.id.tvTotalExpense);

		fetchExpenses();
		
		// Inflate the layout for this fragment
		return view;
	}
	
	public void fetchExpenses(){
		expenses = new ArrayList<Transaction>();
		int totalExpense = 0;
		TestAdapter testAdapter = null;
		try {
			testAdapter = new TestAdapter(getActivity()).createDatabase().open();
			
			String query = "";
			if(viewMonth != null || viewMonth != ""){
				query = "SELECT id, name, date, amount FROM expenses WHERE date LIKE '%" + viewMonth + "'"
						+ " ORDER BY date(date) DESC";
			}
			else{
				query = "SELECT id, name, date, amount FROM expenses ORDER BY date(date) DESC";
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
		expenseAdapter = new CustomTransListAdapter(getActivity(), R.layout.view_transaction_multi_selection, expenses);
		listViewExpense.setChoiceMode(ListView.CHOICE_MODE_NONE);
		listViewExpense.setAdapter(expenseAdapter);
	}
	
	public void setTotalExpense(int total){
		tvTotalExpense.setText(String.valueOf(total));
	}

	public CustomTransListAdapter getExpenseAdapter() {
		return expenseAdapter;
	}

	public void setExpenseAdapter(CustomTransListAdapter expenseAdapter) {
		this.expenseAdapter = expenseAdapter;
	}

	public List<Transaction> getExpenses() {
		return expenses;
	}

	public void setExpenses(List<Transaction> expenses) {
		this.expenses = expenses;
	}

	public TextView getTvTotalExpense() {
		return tvTotalExpense;
	}

	public void setTvTotalExpense(TextView tvTotalExpense) {
		this.tvTotalExpense = tvTotalExpense;
	}
	
}
