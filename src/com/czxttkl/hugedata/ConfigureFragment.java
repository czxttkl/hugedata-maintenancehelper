package com.czxttkl.hugedata;

import com.czxttkl.service.TcpServerService;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ConfigureFragment extends PreferenceFragment {

	public SharedPreferences mSharedPreferences;
	public static PreferenceScreen mPreferenceScreen;
	public static String port;
	public static String ipTcpClient;
	public static boolean tcp;
	public static TcpServerService mTcpServerService;
	
	// Listener defined by anonymous inner class.
	public OnSharedPreferenceChangeListener mListener = new OnSharedPreferenceChangeListener() {
		@Override
		public void onSharedPreferenceChanged(
				SharedPreferences sharedPreferences, String key) {
			Log.i("Hugedata:ConfigureFragment", key
					+ " preference has been changed");
			mPreferenceScreen = getPreferenceScreen();
			
			if(key.equals("portTcpClient")){
			port = sharedPreferences.getString(key, "32100");
			mPreferenceScreen.findPreference(key).setSummary(
					port.equals("32100")? "Now:" + port + " (Default)": "Now:" + port + " (Not Default)" ); 
			}
			if(key.equals("portTcpServer")){
			port = sharedPreferences.getString(key, "32101");
			mPreferenceScreen.findPreference(key).setSummary(
					port.equals("32101")? "Now:" + port + " (Default)": "Now:" + port + " (Not Default)" ); 
			}
			if(key.equals("ipTcpClient")){
			ipTcpClient = sharedPreferences.getString(key,"192.168.1.113");
			mPreferenceScreen.findPreference(key).setSummary("Now:" + ipTcpClient);
			}
		}
	};

	
	
	


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.configurefragment);
		mSharedPreferences = PreferenceManager
		.getDefaultSharedPreferences(getActivity());
		//Obtain the Control of SharedPreferences and Register a OnSharedPreferenceChangeListener
		mSharedPreferences.registerOnSharedPreferenceChangeListener(mListener);
		
		
	}


	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		Log.i("Hugedata:ConfigureFragment","OnResume");
		//judge the default values and set the summaries
		mPreferenceScreen = getPreferenceScreen();
		
		port = mSharedPreferences.getString("portTcpClient","32100");
		mPreferenceScreen.findPreference("portTcpClient").setSummary(
				port.equals("32100")? "Now:" + port + " (Default)": "Now:" + port + " (Not Default)" ); 
		port = mSharedPreferences.getString("portTcpServer","32101");
		mPreferenceScreen.findPreference("portTcpServer").setSummary(
				port.equals("32101")? "Now:" + port + " (Default)": "Now:" + port + " (Not Default)" ); 
		
		ipTcpClient = mSharedPreferences.getString("ipTcpClient", "192.168.1.113");
		mPreferenceScreen.findPreference("ipTcpClient").setSummary("Now:" + ipTcpClient);
		
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.i("Hugedata:ConfigureFragment","OnCreateView");
		return super.onCreateView(inflater, container, savedInstanceState);
	}
}