package selab.dev;

import java.util.logging.Logger;

import android.content.Context;

public class TimeBundle {
	private final static Logger logger = Logger.getLogger(TimeBundle.class.getName());
	private Context mAndroidContext;
	private TimeBroadcast receiver = new TimeBroadcast();
	
	public void setAndroidContext(Context context) {
		mAndroidContext = context;

		System.out.println("MyBundle setAndroidContext");
		logger.info("MyBundle setAndroidContext" + context.getApplicationContext().getApplicationInfo().toString());
	}

	public void unsetAndroidContext() {
		mAndroidContext = null;
	}
}