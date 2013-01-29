package com.czxttkl.hugedata;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;

public class ConfigureFragment extends PreferenceFragment {

	public static SharedPreferences mSharedPreferences;
	public static PreferenceScreen mPreferenceSreen;
	public static String port;
	
	// Listener defined by anonymous inner class.
	public OnSharedPreferenceChangeListener mListener = new OnSharedPreferenceChangeListener() {
		@Override
		public void onSharedPreferenceChanged(
				SharedPreferences sharedPreferences, String key) {
			Log.i("Hugedata:ConfigureFragment", key
					+ " preference has been changed");
			
			mPreferenceSreen = getPreferenceScreen();
			port = sharedPreferences.getString(key, key.equals("portTcpClient")?"32100":"32101");
			Log.i("Hugedata:ConfigureFragment","changed port:" + port);
			
			if(key.equals("portTcpClient")){
			mPreferenceSreen.findPreference(key).setSummary(
					port.equals("32100")? "Now:" + port + " (Default)": "Now:" + port + " (Not Default)" ); 
			}
			if(key.equals("portTcpServer")){
			mPreferenceSreen.findPreference(key).setSummary(
					port.equals("32101")? "Now:" + port + " (Default)": "Now:" + port + " (Not Default)" ); 
			}
			
			
		}
	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.configurefragment);
		mSharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		mSharedPreferences.registerOnSharedPreferenceChangeListener(mListener);
		//judge the default values
		mPreferenceSreen = getPreferenceScreen();
		port = mSharedPreferences.getString("portTcpClient","32100");
		mPreferenceSreen.findPreference("portTcpClient").setSummary(
				port.equals("32100")? "Now:" + port + " (Default)": "Now:" + port + " (Not Default)" ); 
		port = mSharedPreferences.getString("portTcpServer","32101");
		mPreferenceSreen.findPreference("portTcpServer").setSummary(
				port.equals("32101")? "Now:" + port + " (Default)": "Now:" + port + " (Not Default)" ); 
	}
}