package com.czxttkl.hugedata.activity;

import java.io.File;
import java.io.IOException;

import com.czxttkl.hugedata.R;

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

	public void storePref(String manuf, String phontyp) {
		// echo "manufacturer:type" > /sdcard/hugedata/deviceinfo
		StringBuilder storePrefCmd = new StringBuilder();
		storePrefCmd.append("busybox ");
		storePrefCmd.append("echo \"");
		storePrefCmd.append(manufacturer);
		storePrefCmd.append(":");
		storePrefCmd.append(type);
		storePrefCmd.append("\" > ");
		storePrefCmd.append(Environment.getExternalStorageDirectory().getPath() + "/hugedata/deviceinfo");
		try {
			Log.i("hugedata", storePrefCmd.toString());
			Process process = Runtime.getRuntime().exec(storePrefCmd.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.i("hugedata", e.getMessage());
			e.printStackTrace();
		}
	}
}
