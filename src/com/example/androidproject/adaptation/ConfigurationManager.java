package com.example.androidproject.adaptation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.OSGiEmbedApp;
import com.example.androidproject.adaptation.bundle.BundleSpec;
import com.example.androidproject.adaptation.bundle.BundleType;
import com.example.androidproject.adaptation.bundle.time.TimeBroadcast;
import com.example.androidproject.adaptation.bundle.time.TimeBundle;
import com.example.androidproject.util.XMLUtil;
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
				
		List<BundleSpec> bundleList = XMLUtil.getBundleList();
		
		for(BundleSpec spec : bundleList) {
			installBundle(spec.getRawID());
			startBundle(spec.getName());
		}
		
	}

	private BundleType newBundle(String className) {
				
		if(className.equals(TimeBroadcast.class.getName())) {
			return new TimeBundle();
		}
		return null;
	
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
