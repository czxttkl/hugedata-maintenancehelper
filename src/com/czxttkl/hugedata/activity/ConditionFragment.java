package com.czxttkl.hugedata.activity;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;

import com.czxttkl.hugedata.R;
import com.czxttkl.hugedata.service.MeasureService;
import com.czxttkl.hugedata.service.MeasureService.MyBinder;

import android.app.ListFragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

public class ConditionFragment extends ListFragment {

	public MeasureService mMeasureService;
	public static ArrayList<String> dataList = new ArrayList<String>();
	public boolean mIsBound;
	public ArrayAdapter<String> adapter;

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		doUnbindService();
	}

	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			Log.i("Hugedata:ConditionFragment", "onServiceConnected()");
			mMeasureService = ((MyBinder) service).getService();
			Log.i("Hugedata:ConditionFragment",
					(mMeasureService == null ? "ConditionFragment has not connected MeasureService"
							: "ConditionFragment has connected MeasureService"));
			updateListView();
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			// TODO Auto-generated method stub
			Log.i("Hugedata:ConditionFragment", "ServiceDisConnected");
			mMeasureService = null;
		}
	};

	void doBindService() {
		Log.i("Hugedata:ConditionFragment", "doBindService()");
		/*
		 * Log.i("Hugedata:ConditionFragment", (mMeasureService == null ?
		 * "ConditionFragment has connected MeasureService" :
		 * "ConditionFragment has not connected MeasureService"));
		 */
		getActivity().bindService(
				new Intent("android.intent.action.MeasureService"),
				mConnection, Context.BIND_AUTO_CREATE);
		mIsBound = true;
	}

	void doUnbindService() {
		if (mIsBound) {
			// Detach our existing connection.
			getActivity().unbindService(mConnection);
			mIsBound = false;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.i("ConditionFragment", "onCreate");
		super.onCreate(savedInstanceState);

	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i("Condition Fragment", "onCreateView");
		return inflater.inflate(R.layout.conditionfragment, container, false);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		doBindService();

		// Log.i("Condition Fragment", "onResume");
		/*
		 * Intent i = new Intent();
		 * i.setAction("android.intent.action.MeasureService");
		 * getActivity().startService(i);
		 */
		/*
		 * while(mMeasureService==null){ try { Thread.sleep(1000); } catch
		 * (InterruptedException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } }
		 */
		Log.i("Hugedata:Condition Fragment",
				"onResume"
						+ (mMeasureService == null ? "ConditionFragment has not connected MeasureService"
								: "ConditionFragment has connected MeasureService"));
		if (mMeasureService != null)
			updateListView();
		super.onResume();
	}

	public void updateListView() {
		dataList.clear();
		for (String key : mMeasureService.getDatamapKeySet()) {
			dataList.add(key);
			dataList.add(com.czxttkl.hugedata.service.MeasureService.dataMap
					.get(key));
		}
		adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, dataList);
		setListAdapter(adapter);
	}

}