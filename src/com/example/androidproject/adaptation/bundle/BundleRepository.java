package com.example.androidproject.adaptation.bundle;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.content.Context;

import com.example.androidproject.adaptation.ConfigurationManager;
import com.felix.utils.FelixUtils;
import com.felix.utils.NoMusicServiceException;

public class BundleRepository  {

	private Map<String, BundleType> bundles = new HashMap<String, BundleType>();
	private static BundleRepository instance;

	static {
		instance = new BundleRepository();
	}
	
	private BundleRepository() { }
	
	public static BundleRepository getInstance() {
		if(instance != null) {
			return instance;
		} else {
			return new BundleRepository();
		}
	}
	
	private BundleType newBundle(String className) {
		
		BundleType obj = null;
		
		try {
			
			Class<?> cls = Class.forName(className);
			
			obj = (BundleType) cls.newInstance();
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		catch (InstantiationException e) {
			e.printStackTrace();
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		return obj;
	}
	

	public void sendBroadcastToBundle() {
			Iterator<String> bundleKey = bundles.keySet().iterator();
			
			while(bundleKey.hasNext()) {
				String key = bundleKey.next();
				bundles.get(key).sendMessage();
			}
	}
	
	public void removeAllBroadcast() {
		Iterator<String> bundleKey = bundles.keySet().iterator();
		
		while(bundleKey.hasNext()) {
			String key = bundleKey.next();
			bundles.get(key).removeBroadcast();
		}
	}
	
	public void addBundle(String bundleName) {
		
		BundleType bundle = newBundle(bundleName);
		
		if (!bundles.containsKey(bundleName)) {
			bundles.put(bundleName, bundle);
		}
		
		ConfigurationManager.getInstance().installBundle(bundle.getBundleID());
	}
	
	public void removeBundle(String key) {
		bundles.remove(key);
	}
	
	public Map<String, BundleType> getBundles() {
		return bundles;
	}

	
}
