package com.example.androidproject.adaptation;

import android.content.Context;
import android.location.LocationManager;
import android.os.Handler;

import com.example.androidproject.db.MMRSDBHelper;
import com.felix.utils.FelixUtils;

public class AdaptationContorller {

	private ContextMonitor contextMonitor;
	private ContextAnalyzer contextAnalyzer;

	public AdaptationContorller(Context context, LocationManager locationManager, String provider, Handler handler) {

		this.contextMonitor = new ContextMonitor();
		this.contextAnalyzer = new ContextAnalyzer();

		AdaptationPlanner adaptationPlanner = new AdaptationPlanner();
		AdaptationManager adaptationManager = new AdaptationManager();


		contextAnalyzer.setAdaptationPlanner(adaptationPlanner);
		contextAnalyzer.setContext(context);
		contextAnalyzer.setLocationManager(locationManager);
		contextAnalyzer.setProvider(provider);

		adaptationManager.setHandler(handler);

		adaptationPlanner.setAdaptationManager(adaptationManager);
	}

	public void execute() {

		contextMonitor.monitor();

		contextAnalyzer.analyze();
	}
}
