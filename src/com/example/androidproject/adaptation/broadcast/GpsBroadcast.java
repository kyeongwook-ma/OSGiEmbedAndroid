package com.example.androidproject.adaptation.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class GpsBroadcast extends BroadcastReceiver {

	private double distance;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		this.distance = intent.getExtras().getDouble("distance");
		
		System.out.println("[distance]received intend msg : " + distance);
	}

	public double getDistance() {
		return distance;
	}
}
