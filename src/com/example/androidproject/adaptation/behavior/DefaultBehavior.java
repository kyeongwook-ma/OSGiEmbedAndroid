package com.example.androidproject.adaptation.behavior;

import android.os.Handler;
import android.os.Message;

public class DefaultBehavior implements Behavior {

	private Handler handler;
	
	@Override
	public void runAction(String text) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		
		msg.what = 0;
		msg.obj = text;
		
		handler.sendMessage(msg);
	}
	
	public void setHandler(Handler handler) {
		this.handler = handler;
	}
}
