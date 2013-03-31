package com.czxttkl.hugedata.service;

import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class UnlockScreenService extends Service {
	private static PowerManager.WakeLock wakeLock;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		PowerManager pm = (PowerManager) getApplicationContext()
				.getSystemService(Context.POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK
				| PowerManager.ACQUIRE_CAUSES_WAKEUP
				| PowerManager.ON_AFTER_RELEASE, "okTag");
		wakeLock.acquire();

		wakeLock.release();
		
		
//		KeyguardManager mKeyGuardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
//		KeyguardLock mLock = mKeyGuardManager.newKeyguardLock("UnlockScreenService");
//		int a = android.view.WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD;
//		mLock.disableKeyguard();
	
		UnlockScreenService.this.stopSelf();
		
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.i("Hugedata:UnlockScreenService", "onStartCommand");
		return START_STICKY; // this service is explicitly started and stopped
								// as needed
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
