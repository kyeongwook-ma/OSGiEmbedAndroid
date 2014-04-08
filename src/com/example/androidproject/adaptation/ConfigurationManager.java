package com.example.androidproject.adaptation;

import com.OSGiEmbedApp;
import com.felix.utils.FelixUtils;
import com.felix.utils.IApplicationCallback;
import com.felix.utils.NoMusicServiceException;

import android.content.Context;

public class ConfigurationManager {

	private FelixUtils utils;
	private static ConfigurationManager instance;
		
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

	public synchronized void loadBundle(String bundleName) {
		utils.startBundle(bundleName);
	}

	public synchronized void unloadBundle(String bundleName) {
		utils.stopBundle(bundleName);
	}
	
	public void readConfigFromFile() {
		
	}



}
