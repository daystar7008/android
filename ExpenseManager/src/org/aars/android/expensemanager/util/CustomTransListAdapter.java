package org.aars.android.expensemanager.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aars.android.expensemanager.R;
import org.aars.beans.Transaction;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

public class CustomTransListAdapter extends ArrayAdapter<Transaction> {

    private Context mContext = null;
    private int id;
    private List<Transaction> transactions = null;
    private Set<Integer> checkedPositions;

    public CustomTransListAdapter(Context context, int textViewResourceId, List<Transaction> transactions) {
        super(context, textViewResourceId, transactions);
        mContext = context;
        id = textViewResourceId;
        this.transactions = transactions;
        checkedPositions = new HashSet<Integer>();
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
    	final int index = position;
        View mView = v;
        if(mView == null){
            LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mView = vi.inflate(id, null);
        }

        Log.d("TransAdapter - getView()", transactions.get(position).getName());
        
        final CheckBox check = (CheckBox) mView.findViewById(R.id.check);
        check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					checkedPositions.add(index);
				}
				else{
					checkedPositions.remove(index);
				}
			}
		});
        
        TextView tvDate = (TextView) mView.findViewById(R.id.date);
        tvDate.setText(transactions.get(position).getDate());
        
        TextView tvName = (TextView) mView.findViewById(R.id.name);
        tvName.setText(transactions.get(position).getName());
        
        TextView tvAmount = (TextView) mView.findViewById(R.id.amount);
        tvAmount.setText(String.valueOf(transactions.get(position).getAmount()));
                
        return mView;
    }
    
    public Set<Integer> getCheckedPositions(){
    	return checkedPositions;
    }
    
}
