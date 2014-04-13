package com.example.androidproject.adaptation.bundle.time;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;

import com.example.androidproject.R;
import com.example.androidproject.adaptation.bundle.BundleType;
import com.example.androidproject.db.MMRSDBHelper;
import com.example.androidproject.schedule.Schedule;

public class TimeBundle extends BundleType {
	
	private long diff;
	private MMRSDBHelper dbHelper;
	
	public TimeBundle() {
		spec.setRawID(R.raw.mybundle);
		registerReceiver();
	}
	
	private void registerReceiver() {
		broadcastReceiver = new TimeBroadcast();	
		filter.addAction("com.example.androidproject.app.time");
		context.registerReceiver(broadcastReceiver, filter);
	}
	
	@Override
	public void sendMessage() {
		List<Schedule> schedules = dbHelper.selectSchedules();

		for (Schedule schedule : schedules) {
			
			Date now  = new Date();
			
			Intent intent = new Intent();
			
			intent.setAction("com.example.androidproject.bundle.time");
			
			intent.putExtra("now", now.toString());
			intent.putExtra("date", schedule.getDate().toString());
			
			context.sendBroadcast(intent);
			
			long diffOfMinute = getDiff();
			
			if (diffOfMinute > 0 && diffOfMinute <= 100) {
			
				String text = "[Diff] : " + diffOfMinute + "\n" + "[Time rule] : diffOfMinute > 0 && diffOfMinute <= 100";
				
				// adaptationPlanner.plan(1, text);
			}
		}
	}

	public long getDiff() {
		return diff;
	}


}
