package com.example.androidproject.adaptation.bundle;

import com.OSGiEmbedApp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public abstract class BundleType {

	protected BroadcastReceiver broadcastReceiver;	
	protected Context context;
	protected Intent intent;
	protected IntentFilter filter;
	protected BundleSpec spec;
		
	public BundleType() {
		this.context = OSGiEmbedApp.getContext();
		this.intent = new Intent();
		this.filter = new IntentFilter();
		this.spec = new BundleSpec();
	}
	
	public abstract void sendMessage() ;
	
	public void removeBroadcast() {
		context.unregisterReceiver(broadcastReceiver);
	}
	
	public int getBundleID() {
		return this.spec.getRawID();
	}
	
}
