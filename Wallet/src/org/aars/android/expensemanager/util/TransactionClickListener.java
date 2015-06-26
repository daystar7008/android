package org.aars.android.expensemanager.util;

import org.aars.android.beans.Transaction.Type;
import org.aars.android.wallet.IncomeFragment;
import org.aars.android.wallet.R;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;

public class TransactionClickListener implements ImageButton.OnClickListener {
	
	private TransactionUtil transUtil;
	private Object obj;
	
	public TransactionClickListener(Context context, Object obj){
		transUtil = new TransactionUtil(context);
		this.obj = obj;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnAddExpense:
			transUtil.displayAddTransaction(Type.EXPENSE, obj);
			break;
		case R.id.btnAddIncome:
			transUtil.displayAddTransaction(Type.INCOME, obj);
			break;
		case R.id.btnRemoveIncome:
			transUtil.removeIncome(obj);
			break;
		case R.id.btnRemoveExpense:
			transUtil.removeExpense(obj);
			break;
		default:
			break;
		}
	}
	
}
