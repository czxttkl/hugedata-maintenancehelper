package com.czxttkl.service;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

import com.czxttkl.hugedata.MainActivity;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class TcpClientService extends Service {

	//Integer Parameters for Toast Display
	protected static final int CONNECTION_FAILED = 0;
	protected static final int CONNECTION_SUCCESSFUL = 1;
	protected static final int CLOSE_FAILED = 2;
	protected static final int CLOSE_SUCCESSFUL = 3;
	
	public Socket socket;
	public static boolean tcpStart = false;
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	Handler myToast = new Handler() {
		StringBuilder sb = new StringBuilder();
		@Override
		public void handleMessage(Message msg) {
		switch(msg.what){
		case CONNECTION_FAILED:
			sb.delete(0,sb.length());
			sb.append("Connection Failed:\n");
			sb.append("\n-----------Please Check Your Settings-----------\n");
			Toast.makeText(getApplication(), sb.toString(), Toast.LENGTH_LONG)
					.show();
			break;
		case CONNECTION_SUCCESSFUL:
			sb.delete(0,sb.length());
			sb.append("Connection Successful:\n");
			sb.append("\n-----------Start TCP Client-----------\n");
			Toast.makeText(getApplication(), sb.toString(), Toast.LENGTH_LONG)
					.show();
			break;
		case CLOSE_FAILED:
			sb.delete(0,sb.length());
			sb.append("Stop Service Exception:\n");
			sb.append("\n-----------Close TCP Client Failed-----------\n");
			Toast.makeText(getApplication(), sb.toString(), Toast.LENGTH_LONG)
					.show();
			break;
		case CLOSE_SUCCESSFUL:
			sb.delete(0,sb.length());
			sb.append("Stop Service Successful:\n");
			sb.append("\n-----------Close TCP Client-----------\n");
			Toast.makeText(getApplication(), sb.toString(), Toast.LENGTH_LONG)
					.show();
			break;
		}
	}
	};

	class TcpClientStart implements Runnable {
		public void run() {
			establishTCPClient();
		}
	}
	class TcpClientClose implements Runnable {
		public void run() {
			closeTCPClient();
		}
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		switch (intent.getFlags()) {
		case 0:
			new Thread(new TcpClientClose()).start();
			break;
		case 1:
			new Thread(new TcpClientStart()).start();
			break;
		}
		return START_STICKY;
	}

	
	public void establishTCPClient() {
		Log.i("Hugedata:TcpClientService", "Connecting...");
		try {
			socket = new Socket();
			tcpStart = true;
			SocketAddress socketAddress = new InetSocketAddress(
					"192.168.1.113", 32100);
			socket.connect(socketAddress, 3000);
			
			Message msg = new Message();
			msg.what = CONNECTION_SUCCESSFUL;
			myToast.sendMessage(msg);

			String message = "From Hugedata:TcpClientService";
			PrintWriter out = new PrintWriter(new OutputStreamWriter(
					socket.getOutputStream()), true);
			out.println(message);
			Log.i("Hugedata:TcpClientService", "Message Sent");
		} catch (SocketTimeoutException e) {
			Log.i("Hugedata:TCPClientService", "S: Error", e);
			Message msg = new Message();
			msg.what = CONNECTION_FAILED;
			myToast.sendMessage(msg);
		} catch (IOException e) {
			Message msg = new Message();
			msg.what = CONNECTION_FAILED;
			myToast.sendMessage(msg);
			e.printStackTrace();
		}
	}

	public void closeTCPClient() {
		try {
			if(tcpStart && !socket.isClosed())
			socket.close();
			tcpStart=false;
			Message msg = new Message();
			msg.what = CLOSE_SUCCESSFUL;
			myToast.sendMessage(msg);
			//new Thread(new MyThreadTcp()).start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Message msg = new Message();
			msg.what = CLOSE_FAILED;
			myToast.sendMessage(msg);
			e.printStackTrace();
		}
	}

}
