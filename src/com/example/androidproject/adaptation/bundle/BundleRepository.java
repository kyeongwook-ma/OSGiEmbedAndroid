package com.example.androidproject.adaptation.bundle;

import java.util.HashMap;
import java.util.Map;

public class BundleRepository {

	private Map<String, BundleType> bundles = new HashMap<String, BundleType>();
	
	private static final class SingletonHolder {
		
		private static final BundleRepository singleton = new BundleRepository();
	}
	
	public static BundleRepository getInstance() {
		
		return SingletonHolder.singleton;
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
	
	public void addBundle(String key, String className) {
		
		if (!bundles.containsKey(key)) {
		
			bundles.put(key, newBundle(className));
		}
	}
	
	public void removeBundle(String key) {
		
		bundles.remove(key);
	}
	
	public Map<String, BundleType> getBundles() {
		
		return bundles;
	}
}
