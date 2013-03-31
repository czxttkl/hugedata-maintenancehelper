package com.czxttkl.hugedata.service;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.czxttkl.hugedata.activity.StartFragment;
import com.czxttkl.hugedata.helper.StreamTool;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcel;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

public class TcpServerService extends Service {
	public static Button btnTcpClient;
	public static Button btnTcpServer;
	// Integer Parameters for Toast Display
	protected static final int SERVER_ESTABLISH_FAILED = 0;
	protected static final int SERVER_ESTABLISH_SUCCESSFUL = 1;
	protected static final int SERVER_CLOSE_FAILED = 2;
	protected static final int SERVER_CLOSE_SUCCESSFUL = 3;

	public static SharedPreferences mSharedPreferences;
	// Default TCP Server Port:32101
	public static int SERVERPORT = 32101;
	public static ServerSocket serverSocket;
	public static Socket singleTask;

	// Initialization of serverSocket and socket
	{
		try {
			serverSocket = new ServerSocket();
			serverSocket.setReuseAddress(true);
			singleTask = new Socket();
			singleTask.setReuseAddress(true);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// ThreadPool for Server sockets
	private ExecutorService executorService = Executors.newFixedThreadPool(5);

	// Provide Other Fragments or Activities with TcpServerService Instance by
	// Implementing Binder
	public class MyBinder extends Binder {
		public TcpServerService getService() {
			// Log.i("Hugedata","return TcpServerService");
			return TcpServerService.this;
		}

		@Override
		protected boolean onTransact(int code, Parcel data, Parcel reply,
				int flags) throws RemoteException {
			// TODO Auto-generated method stub
			Log.i("Hugedata:TcpServerService","onTransact");
			return super.onTransact(code, data, reply, flags);
		}
		
	}

	private final MyBinder mBinder = new MyBinder();

	@Override
	public MyBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return mBinder;
	}

	/*********** Create a Toast Handler for Toast Display *************/
	Handler myToast = new Handler() {
		StringBuilder sb = new StringBuilder();

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SERVER_ESTABLISH_FAILED:
				sb.delete(0, sb.length());
				sb.append("Server Establish Failed:\n");
				sb.append("\n1.-------Please Check Your Settings-----\n");
				sb.append("\n2.--Restart This Application and Retry--\n");
				Toast.makeText(getApplication(), sb.toString(),
						Toast.LENGTH_LONG).show();
				break;
			case SERVER_ESTABLISH_SUCCESSFUL:
				sb.delete(0, sb.length());
				sb.append("Server Establish Successful:\n");
				sb.append("\n-----------Start TCP Client-----------\n");
				Toast.makeText(getApplication(), sb.toString(),
						Toast.LENGTH_LONG).show();
				break;
			case SERVER_CLOSE_FAILED:
				sb.delete(0, sb.length());
				sb.append("Close Service Exception:\n");
				sb.append("\n--------Close TCP Server Failed-------\n");
				Toast.makeText(getApplication(), sb.toString(),
						Toast.LENGTH_LONG).show();
				break;
			case SERVER_CLOSE_SUCCESSFUL:
				sb.delete(0, sb.length());
				sb.append("Close Service Successful:\n");
				sb.append("\n-----------Close TCP Server-----------\n");
				Toast.makeText(getApplication(), sb.toString(),
						Toast.LENGTH_LONG).show();
				break;
			}
		}
	};

	public class TcpServerClose implements Runnable {
		@Override
		public void run() {
			closeTCPServer();
		}
	}

	public class TcpServerStart implements Runnable {
		public void run() {
			establishTCPServer();
		}
	}

	public void establishTCPServer() {
		try {
			/*
			 * Log.i("Hugedata:TcpServerService", serverSocket.isBound() ?
			 * "Bound" : "not Bound"); Log.i("Hugedata:TcpServerService",
			 * singleTask.isBound() ? "Bound" : "not Bound");
			 */
			if (!serverSocket.isBound()) {
				singleTask = new Socket();
				singleTask.setReuseAddress(true);

				serverSocket = new ServerSocket();
				serverSocket.setReuseAddress(true);
				serverSocket.bind(new InetSocketAddress(SERVERPORT));

				Editor editor = mSharedPreferences.edit();
				editor.putBoolean("tcpServerRunning", true);
				editor.commit();

				Message msg = new Message();
				msg.what = SERVER_ESTABLISH_SUCCESSFUL;
				myToast.sendMessage(msg);

				Message msg1 = new Message();
				msg1.what = SERVER_ESTABLISH_SUCCESSFUL;
				StartFragment.myButtonHandler.sendMessage(msg1);

				while (mSharedPreferences.getBoolean("tcpServerRunning", false)) {
					Log.i("Hugedata:TcpServerService", "Server Accepting @"
							+ SERVERPORT);
					singleTask = serverSocket.accept();

					/*
					 * ObjectInputStream in = new
					 * ObjectInputStream(client.getInputStream());
					 */
					// Socket socket = serverSocket.accept();
					executorService.execute(new SocketTask(singleTask));
				}

			} else
			/* ServerSocket is Bound so it can't establish Tcp Server Right Now */
			{
				Message msg = new Message();
				msg.what = SERVER_ESTABLISH_FAILED;
				myToast.sendMessage(msg);

				Message msg1 = new Message();
				msg1.what = SERVER_ESTABLISH_FAILED;
				StartFragment.myButtonHandler.sendMessage(msg1);

				Editor editor = mSharedPreferences.edit();
				editor.putBoolean("tcpServerRunning", false);
				editor.commit();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			// Often Throw This Exception When EnetAddress is Still in Use. The
			// Solution is to Restart the Application

			Log.i("Hugedata:TcpServerService", "Eastablish TCP:" + e.toString());
			e.printStackTrace();

			Message msg = new Message();
			msg.what = SERVER_ESTABLISH_FAILED;
			myToast.sendMessage(msg);

			Message msg1 = new Message();
			msg1.what = SERVER_ESTABLISH_FAILED;
			StartFragment.myButtonHandler.sendMessage(msg1);

			Editor editor = mSharedPreferences.edit();
			editor.putBoolean("tcpServerRunning", false);
			editor.commit();
		}
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Process process = null;
		try {
			File dir = new File("/sdcard/hugedata");
			if (!dir.exists()) {
				Log.i("Hugedata:TcpServerService", "hugedata make dir");
				File location = new File("/sdcard");
				process = Runtime.getRuntime().exec("su", null, location);
				DataOutputStream os = new DataOutputStream(
						process.getOutputStream());
				os.writeBytes("mkdir hugedata\n");
				os.writeBytes("exit \n");
			} else
				Log.i("Hugedata:TcpServerService", "hugedata dir existed");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mSharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getApplication());
		mSharedPreferences.getBoolean("tcpServerRunning", false);
	}

	public void closeTCPServer() {
		// TODO Auto-generated method stub
		try {
			/*
			 * Log.i("Hugedata:TcpServerService", "singleTask before:" +
			 * (singleTask.isClosed() ? "closed" : "not closed"));
			 * Log.i("Hugedata:TcpServerService", "singleTask before:" +
			 * (singleTask.isBound() ? "Bound" : "not Bound"));
			 */
			// if (socket != null && !socket.isClosed())
			singleTask = new Socket();
			singleTask.close();
			/*
			 * Log.i("Hugedata:TcpServerService", "singleTask after:" +
			 * (singleTask.isClosed() ? "closed" : "not closed"));
			 * Log.i("Hugedata:TcpServerService", "singleTask after:" +
			 * (singleTask.isBound() ? "Bound" : "not Bound"));
			 * 
			 * Log.i("Hugedata:TcpServerService", "serverSocket before:" +
			 * (serverSocket.isClosed() ? "closed" : "not closed"));
			 * Log.i("Hugedata:TcpServerService", "serverSocket before:" +
			 * (serverSocket.isBound() ? "Bound" : "not Bound"));
			 */
			serverSocket = new ServerSocket();
			serverSocket.close();
			Log.i("Hugedata:TcpServerService", "serverSocket after:"
					+ (serverSocket.isClosed() ? "closed" : "not closed"));
			Log.i("Hugedata:TcpServerService", "serverSocket after:"
					+ (serverSocket.isBound() ? "Bound" : "not Bound"));

			Editor editor = mSharedPreferences.edit();
			editor.putBoolean("tcpServerRunning", false);
			editor.commit();

			Message msg = new Message();
			msg.what = SERVER_CLOSE_SUCCESSFUL;
			myToast.sendMessage(msg);

			Message msg1 = new Message();
			msg1.what = SERVER_CLOSE_SUCCESSFUL;
			StartFragment.myButtonHandler.sendMessage(msg1);

		} catch (Exception e) {
			Message msg = new Message();
			msg.what = SERVER_CLOSE_FAILED;
			myToast.sendMessage(msg);

			Message msg1 = new Message();
			msg1.what = SERVER_CLOSE_FAILED;
			StartFragment.myButtonHandler.sendMessage(msg1);

			Editor editor = mSharedPreferences.edit();
			editor.putBoolean("tcpServerRunning", true);
			editor.commit();

			e.printStackTrace();
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.i("Hugedata:TcpServerService","onStartCommand");
		return START_STICKY;
	}

	public boolean find(String taskId) {
		File dir = new File("/sdcard/hugedata" + taskId + ".xml");
		return dir.exists();
	}

	private final class SocketTask implements Runnable {
		private Socket socket = null;
		public String head;
		public PushbackInputStream inStream;

		public SocketTask(Socket socket) {
			this.socket = socket;
		}

		public void run() {
			Log.i("Hugedata:TcpServerService", "Accepted connection from"
					+ socket.getInetAddress() + ":" + socket.getPort());
			try {
				inStream = new PushbackInputStream(socket.getInputStream());
				head = StreamTool.readLine(inStream);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.i("Hugedata:TcpServerService", "head:" + head);

			if (head.trim().equals("handshake"))
				handShake();
			if (head.trim().equals("task"))
				taskReceive();
		}// run

		public void handShake() {
			
		}
		
		public void taskReceive() {
			try {
				head = StreamTool.readLine(inStream);
				String[] items = head.split(";");
				String filelength = items[0];
				String taskId = items[1];
				// System.currentTimeMillis();

				File dir = new File("/sdcard/hugedata");
				boolean taskExist = false;
				if (taskId != null && !"".equals(taskId)) {
					taskExist = find(taskId);
				}
				File file = null;
				int position = 0;

				if (!taskExist) {
					Log.i("Hugedata:TcpServerService",
							"Start Receiving new Task");
					file = new File(dir, taskId + ".xml");
					// save(id, file);
				} else {
					Log.i("Hugedata:TcpServerService", "Task Existing");
					file = new File(dir, taskId + ".xml");
					File logFile = new File(dir, taskId + ".xml.log");
					Properties properties = new Properties();
					properties.load(new FileInputStream(logFile));
					position = Integer
							.valueOf(properties.getProperty("length"));
					Log.i("Hugedata:TcpServerService",
							"The file has been uploaded " + position
									+ " bytes. ");
				}

				OutputStream outStream = socket.getOutputStream();
				String response = taskId + ";" + position + "\r\n";
				outStream.write(response.getBytes());

				RandomAccessFile fileOutStream = new RandomAccessFile(file,
						"rwd");
				if (position == 0)
					fileOutStream.setLength(Integer.valueOf(filelength));
				fileOutStream.seek(position);

				byte[] buffer = new byte[1024];
				int len = -1;
				int length = position;

				while ((len = inStream.read(buffer)) != -1) {
					fileOutStream.write(buffer, 0, len);
					length += len;
					Properties properties = new Properties();
					properties.put("length", String.valueOf(length));
					FileOutputStream logFile = new FileOutputStream(new File(
							file.getParentFile(), file.getName() + ".log"));
					Log.i("Hugedata:TcpServerService",
							"new log file" + file.getName() + ".log");
					properties.store(logFile, null);
					logFile.close();
				}

				// if(length==fileOutStream.length()) delete(id);
				fileOutStream.close();
				inStream.close();
				outStream.close();
				Log.i("Hugedata:TcpServerService", "Uploaded successfully");
				file = null;
			} catch (Exception e) {
				e.printStackTrace();
				Log.i("Hugedata:TcpServerService",
						"Uploading process has been interrupted.");
			} finally {
				try {
					if (socket != null && !socket.isClosed())
						socket.close();
				} catch (IOException e) {
				}
			}
		}

	}// SocketTask

}