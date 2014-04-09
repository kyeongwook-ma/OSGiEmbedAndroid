package com.example.androidproject.adaptation;

import com.example.androidproject.R;
import com.example.androidproject.adaptation.bundle.time.TimeBroadcast;

public class ContextMonitor {

	
	public void monitor() {
		
		boolean timeEnable = true;
		boolean gpsEnable = true;

		if (timeEnable) {
			
			//configurationManager.loadBundle(R.raw.timebundle , "time", "com.example.androidproject.adaptation.bundle.time.TimeBundle");
			ConfigurationManager.getInstance().startBundle(TimeBroadcast.class.getName());
		}
		else {

			
		}
		
		/*if (gpsEnable) {
			
			configurationManager.loadBundle("gps", "com.example.androidproject.adaptation.bundle.gps.GpsBundle");
		}
		else {

			configurationManager.unloadBundle("gps");
		}*/
	}


	
}
