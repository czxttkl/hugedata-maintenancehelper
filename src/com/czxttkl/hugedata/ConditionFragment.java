package com.czxttkl.hugedata;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;

import android.app.ListFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

public class ConditionFragment extends ListFragment {
	
	public static ArrayList<String> dataList = new ArrayList<String>();
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.i("Condition Fragment","onCreate");
		super.onCreate(savedInstanceState);
	}

	
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i("Condition Fragment","onCreateView");
		return inflater.inflate(R.layout.conditionfragment, container, false);
	}



	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		dataList.clear();
		Log.i("Condition Fragment","onResume");
		Intent i = new Intent();
		i.setAction("android.intent.action.MeasureService");
		getActivity().startService(i);
		
		for(String key : com.czxttkl.service.MeasureService.dataMap.keySet()){
			dataList.add(key);
			dataList.add(com.czxttkl.service.MeasureService.dataMap.get(key));
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,dataList);
        setListAdapter(adapter);
  
		super.onResume();
	}

}