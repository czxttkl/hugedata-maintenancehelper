package com.czxttkl.hugedata.unlockscreen;

import org.beryl.app.ComponentEnabler;
import org.beryl.app.ServiceBase;

/*import com.futonredemption.nokeyguard.AutoCancelingForegrounder;
import com.futonredemption.nokeyguard.Constants;
import com.futonredemption.nokeyguard.Intents;
import com.futonredemption.nokeyguard.KeyguardLockWrapper;
import com.futonredemption.nokeyguard.LockScreenState;
import com.futonredemption.nokeyguard.LockScreenStateManager;
import com.futonredemption.nokeyguard.R;
import com.futonredemption.nokeyguard.appwidgets.AppWidgetProvider1x1;
import com.futonredemption.nokeyguard.receivers.PowerStateChangedReceiver;
import com.futonredemption.nokeyguard.receivers.RelayRefreshWidgetReceiver;*/

import android.content.Intent;
import android.os.IBinder;

public class DisableKeyguardService extends ServiceBase {
	private Object _commandLock = new Object();

	private KeyguardLockWrapper _wrapper;
	
	private static final String KeyGuardTag = "KeyguardLockWrapper";
	
	public static final String RemoteAction_EnableKeyguard = "RemoteAction_EnableKeyguard";
	public static final String RemoteAction_DisableKeyguard = "RemoteAction_DisableKeyguard";
	public static final String RemoteAction_DisableKeyguardOnCharging = "RemoteAction_DisableKeyguardOnCharging";
	public static final String RemoteAction_RefreshWidgets = "RemoteAction_RefreshWidgets";
	public static final String RemoteAction_NotifyState = "RemoteAction_NotifyState";
	public static final String EXTRA_RemoteAction = "EXTRA_RemoteAction";
	public static final String EXTRA_ForceNotify = "EXTRA_ForceNotify";

	private final LockScreenStateManager lockStateManager = new LockScreenStateManager(this);
	
	@Override
	public void onCreate() {
		super.onCreate();
		_wrapper = new KeyguardLockWrapper(this, KeyGuardTag);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		_wrapper.dispose();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	protected int handleOnStartCommand(Intent intent, int flags, int startId) {
		handleCommand(intent);
		return START_STICKY;
	}
	
	private void handleCommand(final Intent intent) {
		synchronized (_commandLock) {
			onDisableKeyguard();
		}
	}

	private void updateAllWidgets() {
		final LockScreenState state = getLockScreenState();
		if(!state.IsLockActive)
			disableLockscreen();
		broadcastState(state);
	}

	private void broadcastState(LockScreenState state) {
		this.sendBroadcast(Intents.broadcastLockState(state));
	}

	private void disableLockscreen() {
		setLockscreenMode(false);
	}

	private void setLockscreenMode(boolean enableLockscreen) {

		if (enableLockscreen) {
			_wrapper.enableKeyguard();
		} else {
			_wrapper.disableKeyguard();
		}
	}


	private LockScreenState getLockScreenState() {
		final LockScreenState state = new LockScreenState();
		
		state.Mode = lockStateManager.getKeyguardEnabledPreference();

		if (state.Mode == Constants.MODE_Enabled) {
			state.IsLockActive = true;
		} else {
			lockStateManager.determineIfLockShouldBeDeactivated(state);
		}
		
		return state;
	}

	private void onDisableKeyguard() {
		lockStateManager.setKeyguardTogglePreference(Constants.MODE_Disabled);
		updateAllWidgets();
	}

	@Override
	protected String getTag() {
		return DisableKeyguardService.class.getName();
	}
}
