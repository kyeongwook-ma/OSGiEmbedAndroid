package com.example.androidproject.adaptation;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

import com.example.androidproject.R;
import com.example.androidproject.adaptation.broadcast.BroadcastRepository;
import com.example.androidproject.adaptation.broadcast.TimeBroadcast;
import com.example.androidproject.db.MMRSDBHelper;
import com.felix.utils.FelixUtils;
import com.felix.utils.NoMusicServiceException;

public class AdaptationService extends Service {

	private boolean mQuit;
	
	private FelixUtils utils;
	private BroadcastReceiver mTimeReceiver = new TimeBroadcast();
	
	private LocationManager locationManager;
	private String provider;
	
	private AdaptationContorller adaptationContorller;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		provider = locationManager.getBestProvider(new Criteria(), true);
		
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		mQuit = true;
		/*
		 * 
		 */
		BroadcastRepository.getInstance().removeBroadcastReceiver("time");
		
		unregisterReceiver(mTimeReceiver);
		/*
		 * 
		 */
		locationManager.removeUpdates(locationListener);
		
		Toast.makeText(AdaptationService.this, "service end", Toast.LENGTH_SHORT).show();
		
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		mQuit = false;
		
		locationManager.requestLocationUpdates(provider, 10 * 1000, 10, locationListener);
		
		utils = null;
		
		try {
			utils = new FelixUtils(this, null);
		} catch (NoMusicServiceException e) {
			e.printStackTrace();
		}
		/*
		 * 
		 */
		utils.installBundle(R.raw.timebundle);
		
		BroadcastRepository.getInstance().addBroadcastReceiver("time", mTimeReceiver);
		
		IntentFilter filter = new IntentFilter();
		
		filter.addAction("com.example.androidproject.app.time");
		
		registerReceiver(mTimeReceiver, filter);
		/*
		 * 
		 */
		adaptationContorller = new AdaptationContorller(utils, AdaptationService.this, new MMRSDBHelper(this), locationManager, provider, mHandler);
		
		new AdaptationThread().start();
		
		return START_STICKY;
	}
	
	private class AdaptationThread extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (!mQuit) {
				
				adaptationContorller.execute();
				
				try { Thread.sleep(5 * 1000); } catch (InterruptedException e) { }
			}
		}
	}
	
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if (msg.what == 0) {
				
				String data = (String) msg.obj;
				
				Toast.makeText(AdaptationService.this, data, Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	private LocationListener locationListener = new LocationListener() {
		
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			System.out.println("onProviderEnabled");
		}
		
		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			System.out.println("onProviderDisabled");
		}
		
		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			String text = "[locationListener] : getLatitude : " + location.getLatitude() + " getLongitude : " + location.getLongitude();
			
			System.out.println(text);
		}
	};
}
