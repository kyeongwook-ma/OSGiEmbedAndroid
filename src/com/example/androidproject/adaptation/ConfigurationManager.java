package com.example.androidproject.adaptation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.OSGiEmbedApp;
import com.example.androidproject.adaptation.bundle.BundleType;
import com.felix.utils.FelixUtils;
import com.felix.utils.IApplicationCallback;
import com.felix.utils.NoMusicServiceException;

public class ConfigurationManager {

	private FelixUtils utils;
	private static ConfigurationManager instance;
	private Map<String, BundleType> bundles = new HashMap<String, BundleType>();
	
	static {
		instance = new ConfigurationManager();
	}

	private ConfigurationManager() {
		try {
			utils = new FelixUtils(OSGiEmbedApp.getContext() , new IApplicationCallback() {

				@Override
				public void onUninstalledBundle(String bundleName) {

				}

				@Override
				public void onUninstalledApplication(String applicationType) {

				}

				@Override
				public void onStopedBundle(String bundleName) {

				}

				@Override
				public void onStartedBundle(String bundleName) {

				}

				@Override
				public void onShutdownApplication(String applicationType) {

				}

				@Override
				public void onLaunchedMiddleware() {

				}

				@Override
				public void onLaunchedApplication(String applicationType) {

				}

				@Override
				public void onInstalledBundle(String bundleName) {

				}

				@Override
				public void onInstalledApplication(String applicationType) {

				}
			});
		} catch (NoMusicServiceException e) {
			e.printStackTrace();
		}
	}

	public static ConfigurationManager getInstance() {
		if(instance != null) {
			return instance;
		} else {
			return new ConfigurationManager();
		}
	}

	public synchronized void installBundle(int bundleID) {
		utils.installBundle(bundleID);
	}

	public synchronized void startBundle(String bundleName) {
		bundles.put(bundleName, newBundle(bundleName));
		utils.startBundle(bundleName);
	}

	public synchronized void stopBundle(String bundleName) {
		bundles.remove(bundleName);
		utils.stopBundle(bundleName);
	}	
	
	public void readConfigFromFile() {
		
		String bundleName = null;
		int bundleID = 0;
		
		installBundle(bundleID);
		startBundle(bundleName);
		
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
	
	public void removeAllBundle() {
		/* before uninstall bundle remove all broadcast */
		removeAllBroadcast();
		
		/* uninstall all bundle */
		Iterator<String> bundleKey = bundles.keySet().iterator();
		
		while(bundleKey.hasNext()) {
			String key = bundleKey.next();
			utils.uninstallBundle(key);
		}
		
	}



}
