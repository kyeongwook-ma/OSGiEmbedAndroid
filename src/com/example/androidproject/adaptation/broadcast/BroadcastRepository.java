package com.example.androidproject.adaptation.broadcast;

import java.util.HashMap;
import java.util.Map;

import android.content.BroadcastReceiver;

public class BroadcastRepository {

	private Map<String, BroadcastReceiver> broadcastReceivers = new HashMap<String, BroadcastReceiver>();
	
	private static final class SingletonHolder {
		
		private static final BroadcastRepository singleton = new BroadcastRepository();
	}
	
	public static BroadcastRepository getInstance() {
		
		return SingletonHolder.singleton;
	}
	
	private BroadcastReceiver newBroadcastReceiver(String className) {
		
		BroadcastReceiver obj = null;
		
		try {
			
			Class<?> cls = Class.forName(className);
			
			obj = (BroadcastReceiver) cls.newInstance();
		}
		catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return obj;
	}
	
	public void addBroadcastReceiver(String key, BroadcastReceiver receiver) {
		
		if (!broadcastReceivers.containsKey(key)) {
			
			broadcastReceivers.put(key, receiver);
		}
	}
	
	public BroadcastReceiver addBroadcastReceiver(String key, String className) {
		
		BroadcastReceiver broadcast = null;
		
		if (!broadcastReceivers.containsKey(key)) {
		
			broadcast = newBroadcastReceiver(className);
			
			broadcastReceivers.put(key, broadcast);
		}
		
		return broadcast;
	}
	
	public void removeBroadcastReceiver(String key) {
		
		broadcastReceivers.remove(key);
	}
	
	public Map<String, BroadcastReceiver> getBroadcastReceivers() {
		
		return broadcastReceivers;
	}
	
	public BroadcastReceiver getBroadcastReceiver(String key) {
		
		return broadcastReceivers.get(key);
	}
}
