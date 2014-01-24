package com.sukraa.testinfo.util;

import java.util.List;

import org.sukraa.testinfo.db.TestAdapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sukraa.testinfo.R;
import com.sukraa.testinfo.beans.Test;

public class CustomTestListAdapter extends ArrayAdapter<Test> {
	private Context mContext = null;
    private int id;
    private List<Test> tests = null;
    private boolean amountLabel = true;

    public CustomTestListAdapter(Context context, int textViewResourceId, List<Test> tests, boolean amountLabel) {
        super(context, textViewResourceId, tests);
        mContext = context;
        id = textViewResourceId;
        this.tests = tests;
        this.amountLabel = amountLabel;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        View mView = v;
        if(mView == null){
            LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mView = vi.inflate(id, null);
        }
        
        TestAdapter adapter = new TestAdapter(mContext).createDatabase().open();
        
        ImageView imgFav = (ImageView)mView.findViewById(R.id.imgFav);
        boolean isFavorite = adapter.isExistInFavorites(tests.get(position).getTestName(), "T");
        if(isFavorite){
        	Log.d(tests.get(position).getTestName(), "TRUE");
        	imgFav.setBackgroundResource(android.R.drawable.btn_star_big_on);
        }
        else{
        	Log.d(tests.get(position).getTestName(), "FALSE");
        	imgFav.setBackgroundResource(android.R.drawable.btn_star_big_off);
        }
        adapter.close();
        
        TextView tvTestName = (TextView) mView.findViewById(R.id.tvTestName);
        tvTestName.setText(tests.get(position).getTestName());
        
        TextView tvTestRemarks = (TextView) mView.findViewById(R.id.tvTestRemarks);
        tvTestRemarks.setText(tests.get(position).getRemarks());
        
        if(amountLabel){
        	TextView tvTestAmount = (TextView) mView.findViewById(R.id.tvTestAmount);
            //tvTestAmount.setText("\u20B9 " + String.valueOf((int)tests.get(position).getAmount()));
            tvTestAmount.setText("Rs. " + String.valueOf((int)tests.get(position).getAmount()));
        }
        
        return mView;
    }
    
}
