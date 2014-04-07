package com.example.androidproject.adaptation.behavior;

import java.util.HashMap;
import java.util.Map;

public class BehaviorRepository {

	private Map<String, Behavior> behaviors = new HashMap<String, Behavior>();
	
	private static final class SingletonHolder {
		
		private static final BehaviorRepository singleton = new BehaviorRepository();
	}
	
	public static BehaviorRepository getInstance() {
		
		return SingletonHolder.singleton;
	}
	
	private Behavior newBehavior(String className) {
		
		Behavior obj = null;
		
		try {
			
			Class<?> cls = Class.forName(className);
			
			obj = (Behavior) cls.newInstance();
		}
		catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return obj;
	}
	
	public void addBehavior(String key, String className) {
		
		if (!behaviors.containsKey(key)) {
		
			behaviors.put(key, newBehavior(className));
		}
	}
	
	public void removeBehavior(String key) {
		
		behaviors.remove(key);
	}
	
	public Map<String, Behavior> getBehaviors() {
		
		return behaviors;
	}
}
