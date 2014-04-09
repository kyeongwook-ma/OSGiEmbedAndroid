package com.example.androidproject.adaptation.bundle.gps;

import com.example.androidproject.adaptation.bundle.BundleType;

public class GpsBundle extends BundleType {

	public GpsBundle() {
		super();
		broadcastReceiver = new GpsBroadcast();
	}

	@Override
	public void sendMessage() {
		
	}



}
