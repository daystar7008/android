package com.sukraa.testinfo.util;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sukraa.testinfo.R;
import com.sukraa.testinfo.beans.Favorite;

public class CustomFavListAdapter extends ArrayAdapter<Favorite> {

    private Context mContext = null;
    private int id;
    private List<Favorite> favorites = null;

    public CustomFavListAdapter(Context context, int textViewResourceId, List<Favorite> favorites) {
        super(context, textViewResourceId, favorites);
        mContext = context;
        id = textViewResourceId;
        this.favorites = favorites;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        View mView = v;
        if(mView == null){
            LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mView = vi.inflate(id, null);
        }

        TextView tvFavName = (TextView) mView.findViewById(R.id.tvFavName);
        tvFavName.setText(favorites.get(position).getName());
        
        ImageView imgFavType = (ImageView) mView.findViewById(R.id.imgFavType);
        
        String type = favorites.get(position).getType();
        if(type.trim().equals("P")){
        	imgFavType.setBackgroundResource(R.drawable.profile);
        }
        else if(type.trim().equals("D")){
        	imgFavType.setBackgroundResource(R.drawable.department);
        }
        else if(type.trim().equals("T")){
        	imgFavType.setBackgroundResource(R.drawable.test);
        }
        
        return mView;
    }

}
