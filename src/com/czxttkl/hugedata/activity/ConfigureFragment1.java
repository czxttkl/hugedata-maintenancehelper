package com.czxttkl.hugedata.activity;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.PushbackInputStream;

import com.czxttkl.hugedata.R;
import com.czxttkl.hugedata.helper.StreamTool;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ConfigureFragment1 extends PreferenceFragment {
	public SharedPreferences mSharedPreferences;
	public static PreferenceScreen mPreferenceScreen;
	public String manufacturer;
	public String type;
	// Listener defined by anonymous inner class.
	public OnSharedPreferenceChangeListener mListener = new OnSharedPreferenceChangeListener() {
		@Override
		public void onSharedPreferenceChanged(
				SharedPreferences sharedPreferences, String key) {

			mPreferenceScreen = getPreferenceScreen();

			if (key.equals("manufacturer")) {
				manufacturer = sharedPreferences.getString(key, null);
				mPreferenceScreen.findPreference(key).setSummary(manufacturer);
			}

			if (key.equals("type")) {
				type = sharedPreferences.getString(key, null);
				mPreferenceScreen.findPreference(key).setSummary(type);
			}
			
			storePref(manufacturer, type);
		}
	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.configurefragment1);
		mSharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		// Obtain the Control of SharedPreferences and Register a
		// OnSharedPreferenceChangeListener
		mSharedPreferences.registerOnSharedPreferenceChangeListener(mListener);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// judge the default values and set the summaries
		mPreferenceScreen = getPreferenceScreen();
		manufacturer = mSharedPreferences.getString("manufacturer", null);
		mPreferenceScreen.findPreference("manufacturer").setSummary(
				manufacturer);
		type = mSharedPreferences.getString("type", null);
		mPreferenceScreen.findPreference("type").setSummary(type);
		storePref(manufacturer, type);
	}

	/**
	 * SharedPreferences are stored in
	 * /data/data/com.czxttkl.hugedatashared_prefs/com.czxttkl.hugedata_preferences.xml 
	 * 
	 * Since runner server can't not pull it to the 
	 * local disk due to the limited permission, 
	 * my first thought was to use : echo "manufacturer:type" > /sdcard/hugedata/deviceinfo 
	 * However, ">" for IO redirection doesn't work in Runtime.getRuntime().exec 
	 * So the alternative is to get inputstream from the process which will be written to the /sdcard then.
	 * /sdcard/hugedata/deviceinfo format: manufacturer:type
	 */
	public void storePref(String manuf, String phontyp) {
		StringBuilder storePrefCmd = new StringBuilder();
		storePrefCmd.append("busybox ");
		storePrefCmd.append("echo ");
		storePrefCmd.append(manufacturer);
		storePrefCmd.append(":");
		storePrefCmd.append(type);
		// storePrefCmd.append("\" > ");
		// storePrefCmd.append(Environment.getExternalStorageDirectory().getPath()
		// + "/hugedata/deviceinfo");
		try {
			Log.i("hugedata", storePrefCmd.toString());
			Process process = Runtime.getRuntime()
					.exec(storePrefCmd.toString());
			BufferedReader bfr = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
			File deviceInfoFile = new File(Environment
					.getExternalStorageDirectory().getPath()
					+ "/hugedata/deviceinfo");
			PrintWriter out = new PrintWriter(deviceInfoFile);
			out.print(bfr.readLine().trim());
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.i("hugedata", e.getMessage());
			e.printStackTrace();
		}
	}
}
