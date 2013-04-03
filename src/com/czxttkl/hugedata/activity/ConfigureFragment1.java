package com.czxttkl.hugedata.activity;

import com.czxttkl.hugedata.R;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
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

	}

}
