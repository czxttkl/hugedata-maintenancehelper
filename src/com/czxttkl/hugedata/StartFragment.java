package com.czxttkl.hugedata;

import com.czxttkl.service.TcpServerService;
import com.czxttkl.service.TcpServerService.MyBinder;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;

public class StartFragment extends Fragment {

	public TcpServerService mTcpServerService;
	public boolean mIsBound;
	public static Button btnTcpClient;
	public static Button btnTcpServer;
	OnClickListener ltnTcpClient;
	OnClickListener ltnTcpServer;
	public boolean tcpServerStart = false;
	public SharedPreferences mSharedPreferences;
	

	protected static final int SERVER_ESTABLISH_FAILED = 0;
	protected static final int SERVER_ESTABLISH_SUCCESSFUL = 1;
	protected static final int SERVER_CLOSE_FAILED = 2;
	protected static final int SERVER_CLOSE_SUCCESSFUL = 3;

	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			mTcpServerService = ((MyBinder) service).getService();
			Log.i("Hugedata:StartFragment",
					(mTcpServerService == null ? "StartFragment has connected TcpServerService"
							: "StartFragment has not connected TcpServerServicel"));
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			// TODO Auto-generated method stub
			Log.i("Hugedata:StartFragment", "ServiceDisConnected");
			mTcpServerService = null;
		}
	};

	void doBindService() {
		getActivity().bindService(
				new Intent(getActivity(), TcpServerService.class), mConnection,
				Context.BIND_AUTO_CREATE);
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
		super.onCreate(savedInstanceState);

		mSharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getActivity());

		ltnTcpClient = new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
			}
		};

		ltnTcpServer = new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.i("Hugedata:StartFragment11", "clickBtnTcpServer:mTcpServerService" + (mTcpServerService==null?"null":"not null"));
				if (!mSharedPreferences.getBoolean("tcpServer", false)) {
					new Thread(mTcpServerService.new TcpServerStart()).start();
				} else {
					new Thread(mTcpServerService.new TcpServerClose()).start();
				}
				// Log.i("Hugedata:StartFragment:ClickListener",(mTcpServerService==null?"null":"not null"));
			}
		};

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.startfragment, container, false);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		doUnbindService();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// Use getView() to Get the View of the Fragment
		btnTcpClient = (Button) getView().findViewById(R.id.tcpClientBtn);
		btnTcpServer = (Button) getView().findViewById(R.id.tcpServerBtn);
		// Log.i("Hugedata:StartFragment:btn",(btnTcpServer==null?"null":"not null"));
		// Log.i("Hugedata:StartFragment:listener",(ltnTcpServer==null?"null":"not null"));
		btnTcpClient.setOnClickListener(ltnTcpClient);
		btnTcpServer.setOnClickListener(ltnTcpServer);
		
		if(mSharedPreferences.getBoolean("tcpServer",false)){
			btnTcpServer.setText("Close Tcp Server");
		}
		
		doBindService();
		// Log.i("Hugedata:StartFragment","doBindService()");
		// Log.i("Hugedata:StartFragment:OnResume:mTcpServerService",(mTcpServerService==null?"null":"not null"));

	}

	/*********** Create a Button Handler for Button Text Change *************/
	public static Handler myButtonHandler = new Handler() {
		StringBuilder sb = new StringBuilder();

		@Override
		public void handleMessage(Message msg) {
			// Log.i("Hugedata:StartFragment","changeText:");
			switch (msg.what) {
			case SERVER_ESTABLISH_FAILED:
				btnTcpServer.setText("Start Tcp Server");
				break;
			case SERVER_ESTABLISH_SUCCESSFUL:
				btnTcpServer.setText("Close Tcp Server");
				break;
			case SERVER_CLOSE_SUCCESSFUL:
				btnTcpServer.setText("Start Tcp Server");
				break;
			case SERVER_CLOSE_FAILED:
				btnTcpServer.setText("Close Tcp Server");
				break;
			}
		}
	};
}
