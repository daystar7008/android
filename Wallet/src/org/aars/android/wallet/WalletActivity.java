package org.aars.android.wallet;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;

import org.aars.android.db.TestAdapter;
import org.aars.android.expensemanager.util.NavDrawerItem;
import org.aars.android.expensemanager.util.NavDrawerListAdapter;
import org.aars.android.util.ExportUtil;
import org.aars.android.wallet.R;

import com.startapp.android.publish.StartAppAd;
import com.startapp.android.publish.StartAppSDK;
import com.startapp.android.publish.splash.SplashConfig;

public class WalletActivity extends Activity {

	private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
 
    // nav drawer title
    private CharSequence mDrawerTitle;
 
    // used to store app title
    private CharSequence mTitle;
 
    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
 
    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;
 
    private StartAppAd startAppAd = new StartAppAd(this);
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
		SplashConfig splashConfig = new SplashConfig()
				.setAppName(getString(R.string.app_name))
				.setTheme(SplashConfig.Theme.OCEAN)
				.setLogo(R.drawable.ic_launcher) // resource ID
				.setOrientation(SplashConfig.Orientation.PORTRAIT);
    	
    	StartAppSDK.init(this, "101777646", "205504763", false);
    	StartAppAd.showSplash(this, savedInstanceState, splashConfig);
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_wallet);
 
        mTitle = mDrawerTitle = getTitle();
 
        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
 
        // nav drawer icons from resources
        navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);
 
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
 
        navDrawerItems = new ArrayList<NavDrawerItem>();
 
        // adding nav drawer items to array
        // Home
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
        // Find People
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
        // Photos
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
         
 
        // Recycle the typed array
        navMenuIcons.recycle();
 
        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter);
 
        // enabling action bar app icon and behaving it as toggle button
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
 
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ){
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }
 
            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
 
        if (savedInstanceState == null) {
            // on first time display view for first nav item
            displayView(0);
        }
        
        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());
    }
    
	private class SlideMenuClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// display view for selected nav drawer item
			displayView(position);
		}
	}

	private void displayView(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (position) {
        case 0:
            fragment = new SummaryFragment();
            break;
        case 1:
        	fragment = new IncomeFragment();
        	break;
        case 2:
        	fragment = new ExpenseFragment();
        	break;
 
        default:
            break;
        }
 
        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();
 
            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(navMenuTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            // error in creating fragment
            Log.e("WalletActivity", "Error in creating fragment");
        }
    }
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
	        case R.id.action_export:
	        	export();
	            return true;
	        case R.id.action_share:
	        	shareApp();
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
        }
    }
 
    /***
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_export).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }
 
    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }
 
    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */
 
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }
 
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    
    public void export(){
		TestAdapter testAdapter = null;
		try {
			testAdapter = new TestAdapter(this).createDatabase()
					.open();

			String query = "SELECT name, date, amount, 'expense' AS category FROM expenses " +
							"UNION " +
							"SELECT name, date, amount, 'income' AS category FROM incomes";
			Cursor cursor = testAdapter.getTestData(query);
			if (cursor.getCount() <= 0) {
				testAdapter.close();
				return;
			} else {
				ArrayList<String> cols = new ArrayList<String>(Arrays.asList("DESC", "DATE", "AMOUNT", "CATEGORY"));
				
				Date date = new Date();
				DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
				String formattedDate = dateFormat.format(date);
				String fileName = "transactions_" + formattedDate;
				ExportUtil.exportToExcel(this, cursor, cols, fileName);
			}
		} catch (Exception e) {
			Log.e("WalletActivity - export()", e.toString());
		} finally {
			testAdapter.close();
		}
	}
    
    public void shareApp(){
    	try {
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("text/plain");
			intent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=org.aars.android.expensemanager");
			startActivity(Intent.createChooser(intent, "Share 'Wallet' via"));
		} catch (Exception e) {
			Toast.makeText(this, "Cannot Share", Toast.LENGTH_SHORT).show();
		}
    }

	@Override
	protected void onPause() {
		super.onPause();
		startAppAd.onPause();
		//startAppAd.showAd(); // show the ad
		//startAppAd.loadAd(); // load the next ad
	}

	@Override
	protected void onResume() {
		super.onResume();
		startAppAd.onResume();
		//startAppAd.showAd(); // show the ad
		//startAppAd.loadAd(); // load the next ad
	}
	
	@Override
	public void onBackPressed() {
	    startAppAd.onBackPressed();
	    super.onBackPressed();
	    startAppAd.showAd(); // show the ad
	    startAppAd.loadAd(); // load the next ad
	}
    
    
}
