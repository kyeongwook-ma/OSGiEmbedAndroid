package com.example.androidproject.adaptation.bundle;

import com.example.androidproject.adaptation.bundle.gps.GpsBundle;

public abstract class BundleType {

	public abstract void loadBundle();
	
	
	public static BundleType getBundleType(String className) {
		if(className.equals(GpsBundle.class.getSimpleName())) {
			return new GpsBundle();
		}
		return null;
	}


	
}
