package com.example.androidproject.adaptation;

public class AdaptationPlanner {

	private AdaptationManager adaptationManager;

	public void plan(int ruleId, String text) {
		
		if (ruleId == 1) {
		
			adaptationManager.excuteBehavior(text);
		}
		else if(ruleId == 2) {
			
			adaptationManager.excuteBehavior(text);
		}
	}
	
	public void setAdaptationManager(AdaptationManager adaptationManager) {
		this.adaptationManager = adaptationManager;
	}
}
