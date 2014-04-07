package com.example.androidproject.adaptation;

import com.example.androidproject.R;

public class ContextMonitor {

	private ConfigurationManager configurationManager;
	
	public void monitor() {
		
		boolean timeEnable = true;
		boolean gpsEnable = true;

		if (timeEnable) {
			
			//configurationManager.loadBundle(R.raw.timebundle , "time", "com.example.androidproject.adaptation.bundle.time.TimeBundle");
			configurationManager.loadBundle(R.raw.timebundle , "time", "com.example.androidproject.adaptation.broadcast.TimeBroadcast");
		}
		else {

			configurationManager.unloadBundle("time");
		}
		
		/*if (gpsEnable) {
			
			configurationManager.loadBundle("gps", "com.example.androidproject.adaptation.bundle.gps.GpsBundle");
		}
		else {

			configurationManager.unloadBundle("gps");
		}*/
	}

	public void setConfigurationManager(ConfigurationManager configurationManager) {
		this.configurationManager = configurationManager;
	}
}
