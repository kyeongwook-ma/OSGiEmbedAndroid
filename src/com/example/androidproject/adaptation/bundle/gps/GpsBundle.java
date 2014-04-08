package com.example.androidproject.adaptation.bundle.gps;

import android.content.Context;
import android.location.Location;

import com.example.androidproject.adaptation.bundle.BundleType;

public class GpsBundle extends BundleType {

	public GpsBundle(Context context) {
		super(context);
		broadcastReceiver = new GpsBroadcast();
	}

	@Override
	public void sendMessage() {
		
	}



}
