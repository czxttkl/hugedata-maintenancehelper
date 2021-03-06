package com.czxttkl.hugedata.activity;


import java.io.File;
import java.io.IOException;

import com.czxttkl.hugedata.R;
import com.czxttkl.hugedata.unlockscreen.Intents;
import com.czxttkl.hugedata.unlockscreen.Constants;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
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
	public static PreferenceFragment configureFragment = new ConfigureFragment1();
//	public static StartFragment startFragment = new StartFragment();
	public static UnlockScreenFragment unlockScreenFragment = new UnlockScreenFragment();
	FragmentTransaction myFragmentTransaction;
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
		actionBar.setSelectedNavigationItem(2);
		
		//Related with unlockscreen
		startService(Intents.disableKeyguard(MainActivity.this));
		registerReceiver(LockState, Intents.broadcastLockStateIntentFilter());
		startService(Intents.getStatus(this));
		
		// Related with file creation
		try {
			File dir = new File(Environment.getExternalStorageDirectory().getPath() + "/hugedata");
			if (!dir.exists()) {
				Log.i("Hugedata", "hugedata make dir");
				File location = new File(Environment.getExternalStorageDirectory().getPath());
				Runtime.getRuntime().exec("mkdir hugedata", null, location);
			} 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	//Related with UnlockScreen
	public final LockStatusReceiver LockState = new LockStatusReceiver();
	public class LockStatusReceiver extends BroadcastReceiver {
		public int Mode = Constants.MODE_Enabled;
		@Override
		public void onReceive(Context context, Intent intent) {
			Mode = intent.getIntExtra("mode", Constants.MODE_Enabled);
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// Start Measurement Service
		Intent i = new Intent();
		i.setAction("android.intent.action.MeasureService");
		this.startService(i);
		// Abandon using TCPService
		/*		Intent i1 = new Intent();
		i1.setAction("android.intent.action.TcpServerService");
		this.startService(i1);*/

		//Every time calling onResume(), unlockscreen would be selected by default
		getActionBar().setSelectedNavigationItem(2);

		//Related with unlockscreen
		registerReceiver(LockState, Intents.broadcastLockStateIntentFilter());
		startService(Intents.getStatus(this));
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.i("czxttkl","onstop");
	}

	@Override
	public void onPause() {
		super.onPause();
		//Related with unlockscreen
		Log.i("czxttkl","onpause");
		unregisterReceiver(LockState);
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current tab position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM,2));
		}

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current tab position.
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar()
				.getSelectedNavigationIndex());
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction ft) {
		// When the given tab is selected, show the tab contents in the
		// container view.
		Log.i("czxttkl","ontabselected");
		switch(tab.getPosition()+1){
		case 1:
			ft.replace(R.id.container, configureFragment);
			break;		
		case 2:
			ft.replace(R.id.container, conditionFragment);
			break;
		case 3:
			ft.replace(R.id.container, unlockScreenFragment);
			break;
		}
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		//This method is mostly called after onResume()#getActionBar().setSelectedNavigationItem(2);
		//Make sure the system will call UnlockScreenFragment.onCreateView()
		switch(tab.getPosition()+1){
		case 1:
			ft.remove(configureFragment);
			configureFragment = new ConfigureFragment1();
			ft.add(R.id.container, configureFragment);
			break;		
		case 2:		
			ft.remove(conditionFragment);
			conditionFragment = new ConditionFragment();
			ft.add(R.id.container, conditionFragment);
			break;
		case 3:
			ft.remove(unlockScreenFragment);
			unlockScreenFragment = new UnlockScreenFragment();
			ft.add(R.id.container, unlockScreenFragment);
			break;
		}
	}

}
