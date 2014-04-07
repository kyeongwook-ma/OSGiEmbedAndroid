package com.example.androidproject.adaptation;

import android.content.Context;

import com.example.androidproject.adaptation.broadcast.BroadcastRepository;
import com.felix.utils.FelixUtils;

public class ConfigurationManager {

	private FelixUtils utils;
	private Context context;
	
	public void loadBundle(int rawResourceId, String id, String className) {
		// TODO Auto-generated method stub
//		BundleRepository repository = BundleRepository.getInstance();
//		
//		repository.addBundle(id, className);
		//utils.installBundle(rawResourceId);
		
		BroadcastRepository repository = BroadcastRepository.getInstance();
		
		//BroadcastReceiver receiver = repository.addBroadcastReceiver(id, className);
		
		/*IntentFilter filter = new IntentFilter();
		
		filter.addAction("com.example.androidproject.app." + id);
		
		context.registerReceiver(receiver, filter);*/
	}

	public void unloadBundle(String id) {
		// TODO Auto-generated method stub
//		BundleRepository repository = BundleRepository.getInstance();
//		
//		repository.removeBundle(id);
		BroadcastRepository repository = BroadcastRepository.getInstance();
		
		//BroadcastReceiver receiver = repository.getBroadcastReceiver(id);
		
		//context.unregisterReceiver(receiver);
	}
	
	public void setUtils(FelixUtils utils) {
		this.utils = utils;
	}

	public void setContext(Context context) {
		this.context = context;
	}
}
