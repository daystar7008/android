package com.sukraa.testinfo.util;


import java.util.List;

import org.sukraa.testinfo.db.TestAdapter;

import com.sukraa.testinfo.R;
import com.sukraa.testinfo.beans.Department;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomDeptListAdapter extends ArrayAdapter<Department> {

    private Context mContext = null;
    private int id;
    private List<Department> departments = null;

    public CustomDeptListAdapter(Context context, int textViewResourceId, List<Department> departments) {
        super(context, textViewResourceId, departments);
        mContext = context;
        id = textViewResourceId;
        this.departments = departments;
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
        boolean isFavorite = adapter.isExistInFavorites(departments.get(position).getDeptName(), "D");
        if(isFavorite){
        	Log.d(departments.get(position).getDeptName(), "TRUE");
        	imgFav.setBackgroundResource(android.R.drawable.btn_star_big_on);
        }
        else{
        	Log.d(departments.get(position).getDeptName(), "FALSE");
        	imgFav.setBackgroundResource(android.R.drawable.btn_star_big_off);
        }
        adapter.close();
        
        TextView tvDeptDesc = (TextView) mView.findViewById(R.id.tvDeptDesc);
        tvDeptDesc.setText(departments.get(position).getDesc());
        
        TextView tvDeptName = (TextView) mView.findViewById(R.id.tvDeptName);
        tvDeptName.setText(departments.get(position).getDeptName());
        
        TextView tvTestCount = (TextView) mView.findViewById(R.id.tvTestCount);
        tvTestCount.setText(String.valueOf(departments.get(position).getTestCount()));
                
        return mView;
    }

}
