package com.sukraa.testinfo.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sukraa.testinfo.db.TestAdapter;
import org.sukraa.testinfo.db.Utility;

import com.sukraa.testinfo.beans.Item;
import com.sukraa.testinfo.beans.Item.ItemType;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

public class AutoCompleteAdapter extends ArrayAdapter<Item> implements
		Filterable {

	private List<Item> list;
	private Context context;
	private String searchString;
	private int id;
	private ItemType type;
	
	private Map<String, String> filterMap;
	
	public AutoCompleteAdapter(Context context, int textViewResourceId, String searchString, ItemType type) {
		super(context, textViewResourceId);
		this.context = context;
		this.searchString = searchString;
		this.id = textViewResourceId;
		this.type = type;
		filterMap = new HashMap<String, String>();
	}

	@Override
    public int getCount() {
		if(list != null){
			Log.d("Count", String.valueOf(list.size()));
	        return list.size();
		}
		else {
			return 0;
		}
    }
	
	@Override
    public Item getItem(int index) {
        return list.get(index);
    }

    @Override
    public Filter getFilter() {
        Filter myFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if(constraint != null) {
                    // A class that queries a web API, parses the data and returns an ArrayList<Style>
                    try {
                    	list = fetchResults();
                    }
                    catch(Exception e) {
                    	Log.e("AutoCompleteError", e.toString());
                    }
                    // Now assign the values and count to the FilterResults object
                    filterResults.values = list;
                    filterResults.count = list.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence contraint, FilterResults results) {
                if(results != null && results.count > 0) {
                	notifyDataSetChanged();
                }
                else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return myFilter;
    }
    
    private List<Item> fetchResults(){
		List<Item> list = new ArrayList<Item>();
		try{
			String query = "";
			if(type.equals(ItemType.DEPARTMENT)){
				query = "SELECT DpMt_Dept_Name AS Name,  'D' AS Type FROM DpMt_Dept_Mast_T " +
						"WHERE DpMt_Dept_Name LIKE '%" + searchString + "%' ";
        	}
        	else if(type.equals(ItemType.TEST)){
        		query = "SELECT TsMt_Test_Name AS Name,  'T' AS Type FROM TsMt_Test_Mast_T " +
						"WHERE TsMt_Test_Name LIKE '%" + searchString + "%' ";
        		
        		for(String key : filterMap.keySet()){
        			query = query + " AND " + key + " = '" + filterMap.get(key) + "'";
        		}
        	}
        	else if(type.equals(ItemType.TEST_IN_PROFILE)){
        		query = "SELECT TsMt_Test_Name AS Name,  'T' AS Type " +
						  "FROM TsMt_Test_Mast_T, PfDt_Prof_Detail_T " +
						 "WHERE PfDt_Test_Code = TsMt_Test_Code " +
						   "AND TsMt_Test_Name LIKE '%" + searchString + "%' ";
        		
        		for(String key : filterMap.keySet()){
        			query = query + " AND " + key + " = '" + filterMap.get(key) + "'";
        		}
        	}
        	else if(type.equals(ItemType.PROFILE)){
        		query = "SELECT PfMt_Prof_Name AS Name, 'P' AS Type FROM PfMt_Prof_Mast_T " +
        				 "WHERE PfMt_Prof_Name LIKE '%" + searchString + "%'";
        	}
        	else if(type.equals(ItemType.FAVORITE)){
        		query = "SELECT FvMt_Name AS Name, FvMt_Type AS Type FROM FvMt_Favorite_Mast_T " +
        				 "WHERE FvMt_Name LIKE '%" + searchString + "%'";
        	}

			TestAdapter mDbHelper = new TestAdapter(context).createDatabase().open();
			
			//executes query and gets the results into cursor
			Cursor testdata = mDbHelper.getTestData(query);

			if(testdata.getCount() <= 0){
				//Toast.makeText(context, "No Records Found", Toast.LENGTH_SHORT).show();
				return new ArrayList<Item>();
			}
			do{
				//parses columns with its exact name 
				String name = Utility.getColumnValue(testdata, "Name");
				String type = Utility.getColumnValue(testdata, "Type");
				
				if(type.trim().equals("D")){
					list.add(new Item(name, ItemType.DEPARTMENT));
				}
				else if(type.trim().equals("T")){
					list.add(new Item(name, ItemType.TEST));
				}
				else if(type.trim().equals("P")){
					list.add(new Item(name, ItemType.PROFILE));
				}
				
				Log.d("Dept", name);
			}while(testdata.moveToNext());
			
			testdata.close();
			//closes the database connection
			mDbHelper.close();

		}
		catch (Exception e) {
			Toast.makeText(context, "Couldn't connect to database", Toast.LENGTH_LONG).show();
		}
		return list;
    }
    
    public void addFilter(String filterBy, String filterText){
    	filterMap.put(filterBy, filterText);
    }
    
    public void removeFilter(String filter){
    	if(filterMap.containsKey(filter))
    		filterMap.remove(filter);
    }
    
    public void removeAllFilters(){
    	filterMap = null;
    	filterMap = new HashMap<String, String>();
    }

}
