package com.czxttkl.hugedata.activity;


import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import com.czxttkl.hugedata.R;
import com.czxttkl.hugedata.unlockscreen.Intents;
import com.czxttkl.hugedata.unlockscreen.Constants;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;


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
		//Related with file creation
		Process process = null;
		try {
			File dir = new File("/sdcard/hugedata");
			if (!dir.exists()) {
				Log.i("Hugedata", "hugedata make dir");
				File location = new File("/sdcard");
				process = Runtime.getRuntime().exec("mkdir hugedata", null, location);
			} else
				Log.i("Hugedata", "hugedata dir existed");
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
		Log.i("Hugedata:MainActivity","onResume");
		// Start Measurement Service
		Intent i = new Intent();
		i.setAction("android.intent.action.MeasureService");
		this.startService(i);
		
		/*		Intent i1 = new Intent();
		i1.setAction("android.intent.action.TcpServerService");
		this.startService(i1);*/
		
		//Related with unlockscreen
		registerReceiver(LockState, Intents.broadcastLockStateIntentFilter());
		startService(Intents.getStatus(this));
	}

	@Override
	public void onPause() {
		super.onPause();
		//Related with unlockscreen
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
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, show the tab contents in the
		// container view.
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
			fragmentTransaction.replace(R.id.container, unlockScreenFragment);
			fragmentTransaction.addToBackStack(null);
			fragmentTransaction.commit();
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
