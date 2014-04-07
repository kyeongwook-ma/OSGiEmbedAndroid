package com.example.androidproject.adaptation.bundle;

import java.util.HashMap;
import java.util.Map;

public class BundleRepository {

	private Map<String, Bundle> bundles = new HashMap<String, Bundle>();
	
	private static final class SingletonHolder {
		
		private static final BundleRepository singleton = new BundleRepository();
	}
	
	public static BundleRepository getInstance() {
		
		return SingletonHolder.singleton;
	}
	
	private Bundle newBundle(String className) {
		
		Bundle obj = null;
		
		try {
			
			Class<?> cls = Class.forName(className);
			
			obj = (Bundle) cls.newInstance();
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
	
	public void addBundle(String key, String className) {
		
		if (!bundles.containsKey(key)) {
		
			bundles.put(key, newBundle(className));
		}
	}
	
	public void removeBundle(String key) {
		
		bundles.remove(key);
	}
	
	public Map<String, Bundle> getBundles() {
		
		return bundles;
	}
}
