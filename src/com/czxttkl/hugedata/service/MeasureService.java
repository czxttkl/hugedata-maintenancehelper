package com.czxttkl.hugedata.service;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Set;

import com.czxttkl.hugedata.activity.ConfigureFragment;
import com.czxttkl.hugedata.service.TcpServerService.MyBinder;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

public class MeasureService extends Service {

	// All the measurement results
	public static HashMap<String, String> dataMap = new HashMap<String, String>();
	
	private final MyBinder mBinder = new MyBinder();
	// Shared Preferences for all the activities and services
	SharedPreferences sharedPref;

	// Battery Info
	private int intLevel;
	private int intScale;
	private int intHealth;
	private String strTechnology;
	private int intTemperature;
	private int intVoltage;

	private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
				intLevel = intent.getIntExtra("level", 0);
				intScale = intent.getIntExtra("scale", 100);
				intHealth = intent.getIntExtra("health", 0);
				strTechnology = intent.getStringExtra("technology");
				intTemperature = intent.getIntExtra("temperature", 0);
				intVoltage = intent.getIntExtra("voltage", 0);
				dataMap.put("Battery Level", String.valueOf(intLevel));
				dataMap.put("Battery Scale", String.valueOf(intScale));
				dataMap.put("Battery Health", String.valueOf(intHealth));
				dataMap.put("Battery Temperature",
						String.valueOf(intTemperature));
				dataMap.put("Battery Technology", strTechnology);
				dataMap.put("Battery Voltage", String.valueOf(intVoltage));
			}
		}
	};

	public static void getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						if (inetAddress instanceof Inet4Address) {
							dataMap.put("Ip Address", inetAddress
									.getHostAddress().toString());
/*							Log.i("Hugedata:MeasureService", "IP Address:" + inetAddress
									.getHostAddress().toString());*/
						}
					}
				}
			}
		} catch (SocketException ex) {
			Log.e("Hugedata:MeasureService", "getLocalIpAddressException" + ex.toString());
		}
	}

	@Override
	public MyBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return mBinder;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		registerReceiver(mBatInfoReceiver, new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED));
		
		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		// Log.i("MeasureService","portTcpClient:" +
		// getSharedPreferences("portTcpClient",0).getString(key, defValue));
		//Log.i("Hugedata:MeasureService", "OnCreate()" + sharedPref.getString("portTcpClient", "111"));
		Log.i("Hugedata:MeasureService","onCreate");
		getLocalIpAddress();
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.i("Hugedata:MeasureService", "onStartCommand");
		return START_STICKY; // this service is explicitly started and stopped
								// as needed
	}
	
	public Set<String> getDatamapKeySet(){
		getLocalIpAddress();
		return dataMap.keySet();
	}

	
	public class MyBinder extends Binder {
		public MeasureService getService() {
			return MeasureService.this;
		}
	}
	
}
