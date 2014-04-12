package com.example.androidproject.adaptation;

import android.content.Context;


public class AdaptationManager {

	
	public void plan(Context analyzedContext) {
		
		
		String bundleName = "";
		executeBundle(bundleName);
	}
	
	private void executeBundle(String bundleName) {
		ConfigurationController.getInstance().startBundle(bundleName);
	}
}
