package com.example.androidproject.adaptation;

import android.content.Context;
import android.location.LocationManager;

public class ContextAnalyzer {

	private Context context;
	private AdaptationPlanner adaptationPlanner;
	private LocationManager locationManager;
	
	public void analyze() {
				
		// ConfigurationManager.getInstance().sendBroadcastToBundle();
				
	}
	
	public void setAdaptationPlanner(AdaptationPlanner adaptationPlanner) {
		this.adaptationPlanner = adaptationPlanner;
	}


	public void setLocationManager(LocationManager locationManager) {
		this.locationManager = locationManager;
	}

	public void setProvider(String provider) {
	}
	
	public void setContext(Context context) {
		this.context = context;
	}
}
