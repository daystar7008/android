package org.aars.android.expensemanager.util;

import org.aars.android.beans.Transaction;
import org.aars.android.beans.Transaction.Type;
import org.aars.android.db.TestAdapter;
import org.aars.android.wallet.ExpenseFragment;
import org.aars.android.wallet.IncomeFragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.ScrollView;
import android.widget.Toast;

public class TransactionUtil {
	
	private TransactionLayout transLayout;
	private AlertDialog.Builder builder;
	private Context context;
	
	public TransactionUtil(Context context){
		this.context = context;
	}
	
	public void displayAddTransaction(Type type, final Object obj){
		final Type transType = type;
		transLayout = new TransactionLayout(context);
		builder = new AlertDialog.Builder(context);
		if(type.equals(Type.EXPENSE)){
			builder.setTitle("Add Expense");
		}
		else if(type.equals(Type.INCOME)){
			builder.setTitle("Add Income");
		}
		
		ScrollView view = new ScrollView(context);
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
								((ExpenseFragment) obj).fetchExpenses();
							}
							else if(transType.equals(Type.INCOME)){
								addIncome(transaction);
								((IncomeFragment) obj).fetchIncomes();
							}
						}
						else{
							Toast.makeText(context, "Enter Amount", Toast.LENGTH_SHORT).show();
							displayAddTransaction(transType, obj);
						}
					}
					else{
						Toast.makeText(context, "Enter Transaction Name", Toast.LENGTH_SHORT).show();
						displayAddTransaction(transType, obj);
					}
				}
				else{
					Toast.makeText(context, "Enter Transaction Name", Toast.LENGTH_SHORT).show();
					displayAddTransaction(transType, obj);
				}
			}
		});
		builder.setNegativeButton("Cancel", null);
		builder.show();
	}
	
	public void addExpense(Transaction trans){
		TestAdapter testAdapter = null;
		try {
			testAdapter = new TestAdapter(context).createDatabase().open();
			if(testAdapter.addExpense(trans)){
				Toast.makeText(context, "Expense Saved", Toast.LENGTH_SHORT).show();
			}
			else{
				Toast.makeText(context, "Expense Not Saved", Toast.LENGTH_SHORT).show();
			}
			testAdapter.close();
		} catch (Exception e) {
			testAdapter.close();
			Log.e("Transacton Util - addExpense()", e.toString());
		}
	}
	
	public void removeExpense(Object obj){
		TestAdapter testAdapter = null;
		Transaction trans = null;
		try {
			testAdapter = new TestAdapter(context).createDatabase().open();
			
			if(!(obj instanceof ExpenseFragment)){
				testAdapter.close();
				Log.d("TransactionUtil - removeExpense()", "Cannot remove expense -- given object is not ExpenseFragment");
				return;
			}
			
			ExpenseFragment expenseFragment = (ExpenseFragment)obj;
			
			for(Integer pos : expenseFragment.getExpenseAdapter().getCheckedPositions()){
				testAdapter.removeExpense(expenseFragment.getExpenses().get(pos).getId());
				trans = expenseFragment.getExpenses().get(pos);
			}
			testAdapter.close();
			expenseFragment.fetchExpenses();
			
			if(trans != null){
				int tot = Integer.parseInt(expenseFragment.getTvTotalExpense().getText().toString()) - trans.getAmount();
				expenseFragment.setTotalExpense(tot);
			}
		} catch (Exception e) {
			testAdapter.close();
			Log.e("TransactionUtil - removeExpense()", e.toString());
		}
	}
	
	public void addIncome(Transaction trans){
		TestAdapter testAdapter = null;
		try {
			testAdapter = new TestAdapter(context).createDatabase().open();
			if(testAdapter.addIncome(trans)){
				Toast.makeText(context, "Income Saved", Toast.LENGTH_SHORT).show();
			}
			else{
				Toast.makeText(context, "Income Not Saved", Toast.LENGTH_SHORT).show();
			}
			testAdapter.close();
		} catch (Exception e) {
			testAdapter.close();
			Log.e("MainActivity - addIncome()", e.toString());
		}
	}
	
	public void removeIncome(Object obj){
		TestAdapter testAdapter = null;
		Transaction trans = null;
		try {
			testAdapter = new TestAdapter(context).createDatabase().open();
			
			if(!(obj instanceof IncomeFragment)){
				testAdapter.close();
				Log.d("TransactionUtil - removeIncome()", "Cannot remove income -- given object is not IncomeFragment");
				return;
			}
			
			IncomeFragment incomeFragment = (IncomeFragment) obj;
			
			for(Integer pos : incomeFragment.getIncomeAdapter().getCheckedPositions()){
				testAdapter.removeIncome(incomeFragment.getIncomes().get(pos).getId());
				trans = incomeFragment.getIncomes().get(pos);
			}
			testAdapter.close();
			incomeFragment.fetchIncomes();
			
			if(trans != null){
				int tot = Integer.parseInt(incomeFragment.getTvTotalIncome().getText().toString()) - trans.getAmount();
				incomeFragment.setTotalIncome(tot);
			}
		} catch (Exception e) {
			testAdapter.close();
			Log.e("TransactionUtil - removeIncome()", e.toString());
		}
	}
	
}
