package com.example.androidproject.adaptation;

import com.example.androidproject.adaptation.behavior.DefaultBehavior;

import android.os.Handler;


public class AdaptationManager {

	private Handler handler;
	
	public void excuteBehavior(String text) {
		
		DefaultBehavior behavior = new DefaultBehavior();
		
		behavior.setHandler(handler);
		
		behavior.runAction(text);
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}
}
