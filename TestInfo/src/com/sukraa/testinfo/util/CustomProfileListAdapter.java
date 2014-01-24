package com.sukraa.testinfo.util;


import java.util.List;

import org.sukraa.testinfo.db.TestAdapter;

import com.sukraa.testinfo.R;
import com.sukraa.testinfo.beans.Profile;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomProfileListAdapter extends ArrayAdapter<Profile> {

    private Context mContext = null;
    private int id;
    private List<Profile> profiles = null;

    public CustomProfileListAdapter(Context context, int textViewResourceId, List<Profile> profiles) {
        super(context, textViewResourceId, profiles);
        mContext = context;
        id = textViewResourceId;
        this.profiles = profiles;
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
        boolean isFavorite = adapter.isExistInFavorites(profiles.get(position).getProfileName(), "P");
        if(isFavorite){
        	Log.d(profiles.get(position).getProfileName(), "TRUE");
        	imgFav.setBackgroundResource(android.R.drawable.btn_star_big_on);
        }
        else{
        	Log.d(profiles.get(position).getProfileName(), "FALSE");
        	imgFav.setBackgroundResource(android.R.drawable.btn_star_big_off);
        }
        adapter.close();
        
        TextView tvProfDesc = (TextView) mView.findViewById(R.id.tvProfDesc);
        tvProfDesc.setText(profiles.get(position).getRemarks());
        
        TextView tvProfName = (TextView) mView.findViewById(R.id.tvProfName);
        tvProfName.setText(profiles.get(position).getProfileName());
        
        TextView tvProfAmount = (TextView) mView.findViewById(R.id.tvProfAmount);
        tvProfAmount.setText("Rs. " + String.valueOf((int)profiles.get(position).getAmount()));
        
        TextView tvTestCount = (TextView) mView.findViewById(R.id.tvTestCount);
        tvTestCount.setText(String.valueOf(profiles.get(position).getTests().size()));
                
        return mView;
    }

}
