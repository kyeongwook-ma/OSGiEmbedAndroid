package com.example.androidproject.adaptation;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

public class ContextMonitor extends Service {

		private Context context;
		private LocationManager locationManager;
		private String provider;
		private MonitoringThread monitoringThread; 
		private AdaptationContorller adaptationContorller;
		

		public ContextMonitor(Context context) {
			this.context = context;			
		}
		
		@Override
		public IBinder onBind(Intent intent) {
			return null;
		}

		@Override
		public void onCreate() {			
			
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			
			adaptationContorller = new AdaptationContorller(context);
			
			/* start monitoring */
			monitoringThread = new MonitoringThread();
			monitoringThread.start();
			
			super.onCreate();
		}

		@Override
		public void onDestroy() {
	
			/* destroy existed monitoring thread */
			if(monitoringThread != null && monitoringThread.isAlive()) {
			    monitoringThread.interrupt();
			    monitoringThread = null;
			}
			
			/* prevent memory leak from broadcast receiver */
			ConfigurationController.getInstance().removeAllBroadcast();
			
			Toast.makeText(context, "service end", Toast.LENGTH_SHORT).show();
			
			super.onDestroy();
		}

		@Override
		public int onStartCommand(Intent intent, int flags, int startId) {

			locationManager.requestLocationUpdates(provider, 10 * 1000, 10, locationListener);

			// ConfigurationManager.getInstance().installBundle(R.raw.timebundle);
			
			
			
			return START_REDELIVER_INTENT;
		}
		
		class MonitoringThread extends Thread {

			@Override
			public void run() { 
				while(true) {
					try {
						adaptationContorller.receiveChangedContext(context);
						Thread.sleep(5 * 1000); 
					} catch (InterruptedException e) 
					{ e.printStackTrace(); } 
				}
			}
		}
		
		private Handler mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 0) {
					
					String data = (String) msg.obj;
					
					Toast.makeText(context, data, Toast.LENGTH_SHORT).show();
				}
			}
		};
		
		private LocationListener locationListener = new LocationListener() {
			
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				
			}
			
			@Override
			public void onProviderEnabled(String provider) {
				System.out.println("onProviderEnabled");
			}
			
			@Override
			public void onProviderDisabled(String provider) {
				System.out.println("onProviderDisabled");
			}
			
			@Override
			public void onLocationChanged(Location location) {
				String text = "[locationListener] : getLatitude : " + location.getLatitude() + " getLongitude : " + location.getLongitude();
				
				System.out.println(text);
			}
		};
		
	
	
}
