package com.example.androidproject.adaptation.bundle;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public abstract class BundleType {

	protected BroadcastReceiver broadcastReceiver;	
	protected Context context;
	protected Intent intent;
	protected IntentFilter filter;
	protected String name;
	protected int ID;
		
	public BundleType(Context context) {
		this.context = context;
		this.intent = new Intent();
		this.filter = new IntentFilter();
	}
	
	public abstract void sendMessage() ;
	
	public void removeBroadcast() {
		context.unregisterReceiver(broadcastReceiver);
	}
	
	public int getBundleID() {
		return this.ID;
	}
	
}
