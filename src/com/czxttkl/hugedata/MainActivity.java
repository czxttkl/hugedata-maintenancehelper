package com.czxttkl.hugedata;


import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;


public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener {

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current tab position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	public static ConditionFragment conditionFragment = new ConditionFragment();
	public static PreferenceFragment configureFragment = new ConfigureFragment();
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		// Set up the action bar to show tabs.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// For each of the sections in the app, add a tab to the action bar.
		actionBar.addTab(actionBar.newTab().setText(R.string.title_section1)
				.setTabListener(this));
		actionBar.addTab(actionBar.newTab().setText(R.string.title_section2)
				.setTabListener(this));
		actionBar.addTab(actionBar.newTab().setText(R.string.title_section3)
				.setTabListener(this));
		
		// Start Measurement Service
		Intent i = new Intent();
		i.setAction("android.intent.action.MeasureService");
		this.startService(i);
		
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current tab position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current tab position.
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar()
				.getSelectedNavigationIndex());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, show the tab contents in the
		// container view.
		Log.i("tab",""+(tab.getPosition()+1));
		//android.app.FragmentManager fragmentManager = getFragmentManager();
		fragmentTransaction= getFragmentManager().beginTransaction();
		
		switch(tab.getPosition()+1){
		case 1:
			fragmentTransaction.replace(R.id.container, configureFragment);
			fragmentTransaction.addToBackStack(null);
			fragmentTransaction.commit();
			break;		
		case 2:
			fragmentTransaction.replace(R.id.container, conditionFragment);
			fragmentTransaction.addToBackStack(null);
			fragmentTransaction.commit();
			break;
		case 3:
			break;
		}
		
		/*		Bundle args = new Bundle();
		args.putInt(ConfigureFragment.ARG_SECTION,
				tab.getPosition() + 1);
		fragment.setArguments(args);*/
		
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}


	


}
