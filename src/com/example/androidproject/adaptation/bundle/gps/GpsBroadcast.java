package com.example.androidproject.adaptation.bundle.gps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class GpsBroadcast extends BroadcastReceiver {

	private double distance;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		this.distance = intent.getExtras().getDouble("distance");
		
		System.out.println("[distance]received intend msg : " + distance);
	}

	public double getDistance() {
		return distance;
	}
}
