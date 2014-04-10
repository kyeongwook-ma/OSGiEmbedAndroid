package com.example.androidproject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.example.androidproject.adaptation.ConfigurationController;
import com.example.androidproject.adaptation.bundle.time.TimeBundle;
import com.felix.utils.FelixUtils;
import com.felix.utils.IApplicationCallback;
import com.felix.utils.NoMusicServiceException;

public class MainActivity extends Activity implements IApplicationCallback {
	final static String TAG = "MainActivity";
	FelixUtils mUtils;

	Button mInstall, mStart, mStop, mUninstall, mList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		try {
			mUtils = new FelixUtils(this, this);
		} catch (NoMusicServiceException e) {
			e.printStackTrace();
		}

		mInstall = (Button) findViewById(R.id.install);
		mUninstall = (Button) findViewById(R.id.uninstall);
		mStart = (Button) findViewById(R.id.start);
		mStop = (Button) findViewById(R.id.stop);
		mList = (Button) findViewById(R.id.get_bundle);
		mInstall.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mUtils.installBundle(R.raw.mybundle);

			}
		});
		mUninstall.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mUtils.uninstallBundle("com.example");
			}
		});
		mStart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ConfigurationController.getInstance().startBundle("com.example");
			}
		});
		mStop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mUtils.stopBundle("com.example");
			}
		});
		mList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mUtils.stopBundle("com.example");
			}
		});
		

	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mUtils = null;
	}

	@Override
	public void onLaunchedMiddleware() {
		// not supported

	}

	@Override
	public void onInstalledApplication(String applicationType) {
		// not supported

	}

	@Override
	public void onUninstalledApplication(String applicationType) {
		// not supported

	}

	@Override
	public void onLaunchedApplication(String applicationType) {
		// not supported

	}

	@Override
	public void onShutdownApplication(String applicationType) {
		// not supported

	}

	@Override
	public void onInstalledBundle(String bundleName) {
		Toast.makeText(this, "onInstalledBundle " + bundleName, Toast.LENGTH_SHORT).show();
		Log.e(TAG, "onInstalledBundle " + bundleName);

	}

	@Override
	public void onUninstalledBundle(String bundleName) {
		Toast.makeText(this, "onUninstalledBundle " + bundleName, Toast.LENGTH_SHORT).show();
		Log.e(TAG, "onUninstalledBundle " + bundleName);

	}

	@Override
	public void onStartedBundle(String bundleName) {
		Toast.makeText(this, "onStartedBundle " + bundleName, Toast.LENGTH_SHORT).show();
		Log.e(TAG, "onStartedBundle " + bundleName);

	}


	
	@Override
	public void onStopedBundle(String bundleName) {
		Toast.makeText(this, "onStopedBundle " + bundleName, Toast.LENGTH_SHORT).show();
		Log.e(TAG, "onStopedBundle " + bundleName);
	}
}
