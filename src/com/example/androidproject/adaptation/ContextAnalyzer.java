package com.example.androidproject.adaptation;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

import com.example.androidproject.adaptation.broadcast.BroadcastRepository;
import com.example.androidproject.adaptation.broadcast.TimeBroadcast;
import com.example.androidproject.db.MMRSDBHelper;
import com.example.androidproject.schedule.Schedule;

public class ContextAnalyzer {

	private Context context;
	
	private AdaptationPlanner adaptationPlanner;
	
	private MMRSDBHelper dbHelper;
	
	private LocationManager locationManager;
	private String provider;

	public void analyze() {
		
		//BundleRepository repository = BundleRepository.getInstance();
		BroadcastRepository repository = BroadcastRepository.getInstance();
		
		Map<String, BroadcastReceiver> bundles = repository.getBroadcastReceivers();
		
		Iterator<String> iterator = bundles.keySet().iterator();

		while (iterator.hasNext()) {
			
			String key = (String) iterator.next();
			
			if (key.equals("time")) {
			
				TimeBroadcast timeBundle = (TimeBroadcast) bundles.get(key);
				
				List<Schedule> schedules = dbHelper.selectSchedules();

				for (Schedule schedule : schedules) {
					
					Date now  = new Date();
					
					Intent intent = new Intent();
					
					intent.setAction("com.example.androidproject.bundle.time");
					
					intent.putExtra("now", now.toString());
					intent.putExtra("date", schedule.getDate().toString());
					
					context.sendBroadcast(intent);
					
					//long diffOfMinute = (Long) timeBundle.analyzeContext(now, schedule.getDate());
					long diffOfMinute = timeBundle.getDiff();
					
					if (diffOfMinute > 0 && diffOfMinute <= 100) {
					
						String text = "[Diff] : " + diffOfMinute + "\n" + "[Time rule] : diffOfMinute > 0 && diffOfMinute <= 100";
						
						adaptationPlanner.plan(1, text);
					}
				}
			}
			/*else if (key.equals("gps")) {
				
				Bundle gpsBundle = bundles.get(key);
				
				List<Schedule> schedules = dbHelper.selectSchedules();

				for (Schedule schedule : schedules) {
					
					Location src = locationManager.getLastKnownLocation(provider);
					
					Location dest = new Location("dest");
					
					dest.setLongitude(schedule.getLongitude());
					dest.setLatitude(schedule.getLatitude());
					
					double distance = (Double) gpsBundle.analyzeContext(src, dest);
					
					if (distance > 0 && distance <= 100) {
						
						String text = "[Distance] : " + distance + "m \n" + "[GPS rule] : distance > 0 && distance <= 100";
						
						adaptationPlanner.plan(2, text);
					}
					if (distance > 100 && distance <= 200) {
						
						String text = "[Distance] : " + distance + "m \n" + "[GPS rule] : distance > 100 && distance <= 200";
						
						adaptationPlanner.plan(2, text);
					}
				}
			}*/
		}
	}
	
	public void setAdaptationPlanner(AdaptationPlanner adaptationPlanner) {
		this.adaptationPlanner = adaptationPlanner;
	}

	public void setDbHelper(MMRSDBHelper dbHelper) {
		this.dbHelper = dbHelper;
	}

	public void setLocationManager(LocationManager locationManager) {
		this.locationManager = locationManager;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}
	
	public void setContext(Context context) {
		this.context = context;
	}
}
