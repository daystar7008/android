package org.aars.android.expensemanager.util;

import java.util.List;
import org.aars.android.expensemanager.R;
import org.aars.android.beans.Transaction;
import org.aars.android.beans.Transaction.Type;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomTranSummaryAdapter extends ArrayAdapter<Transaction> {

    private Context mContext = null;
    private int id;
    private List<Transaction> transactions = null;

    public CustomTranSummaryAdapter(Context context, int textViewResourceId, List<Transaction> transactions) {
        super(context, textViewResourceId, transactions);
        mContext = context;
        id = textViewResourceId;
        this.transactions = transactions;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        View mView = v;
        if(mView == null){
            LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mView = vi.inflate(id, null);
        }

        Transaction tran = transactions.get(position);
        
        TextView tvDate = (TextView) mView.findViewById(R.id.date);
        tvDate.setText(transactions.get(position).getDate());
        
        TextView tvName = (TextView) mView.findViewById(R.id.name);
        tvName.setText(transactions.get(position).getName());
        
        TextView tvAmount = (TextView) mView.findViewById(R.id.amount);
        tvAmount.setText(String.valueOf(transactions.get(position).getAmount()));
        
        if(tran.getType().equals(Type.INCOME)){
        	tvDate.setTextColor(mContext.getResources().getColor(R.color.green));
        	tvName.setTextColor(mContext.getResources().getColor(R.color.green));
        	tvAmount.setTextColor(mContext.getResources().getColor(R.color.green));
		}
		else if(tran.getType().equals(Type.EXPENSE)){
			tvDate.setTextColor(mContext.getResources().getColor(R.color.red));
			tvName.setTextColor(mContext.getResources().getColor(R.color.red));
			tvAmount.setTextColor(mContext.getResources().getColor(R.color.red));
		}
                
        return mView;
    }
    
}
