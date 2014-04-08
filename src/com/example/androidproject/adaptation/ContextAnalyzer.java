package com.example.androidproject.adaptation;

import android.content.Context;
import android.location.LocationManager;

import com.example.androidproject.adaptation.bundle.BundleRepository;
import com.example.androidproject.db.MMRSDBHelper;

public class ContextAnalyzer {

	private Context context;
	private AdaptationPlanner adaptationPlanner;
	private LocationManager locationManager;
	
	public void analyze() {
				
		BundleRepository repository = BundleRepository.getInstance();
		repository.sendBroadcastToBundle();
				
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
