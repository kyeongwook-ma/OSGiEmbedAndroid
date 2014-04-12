package com.example.androidproject.adaptation;

import android.content.Context;
import android.location.LocationManager;
import android.os.Handler;

import com.example.androidproject.db.MMRSDBHelper;
import com.felix.utils.FelixUtils;

public class AdaptationContorller {

	private AdaptionReasoner adaptationReasonor;
	private AdaptationManager adaptationManager;

	public AdaptationContorller(Context context) {
		adaptationManager = new AdaptationManager();
		adaptationReasonor = new AdaptionReasoner();
		
	}
	
	public void receiveChangedContext(Context context) {
		Context analyzedContext = adaptationReasonor.analyzeContext(context);
		adaptationManager.plan(analyzedContext);
	}

}
