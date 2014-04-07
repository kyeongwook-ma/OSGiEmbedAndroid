package com.felix.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.felix.framework.FrameworkFactory;
import org.apache.felix.framework.cache.BundleCache;
import org.apache.felix.main.AutoProcessor;
import org.apache.felix.main.Main;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.Framework;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.androidproject.R;
import com.felix.utils.FelixUtils;
import com.felix.utils.NoMusicServiceException;

public class FelixService extends Service {
	private final static String TAG = FelixService.class.getName();

	private final static Logger logger = Logger.getLogger(FelixService.class.getName());

	private final static int NOTIFICATION_ID = 1000;
	/**
	 * Optional system property to specify the work directory for the MUSIC application. The default value will be
	 */
	public final static String DEPLOYMENT_DIR_PROPERTY = "deployment.dir";

	/**
	 * Directory for the Felix OSGi framework (subdirectory of the DEPLOYMENT_DIRECTORY)
	 */
	public final static String FELIX_DIR = "felix";

	/**
	 * Directory for the Felix cache (subdirectory of the FELIX_DIR)
	 */
	public final static String FELIX_CACHE_DIR = "felix-cache";

	/**
	 * Filename where the version code of the application is installed. This version code is used to check if a new version of the MUSIC
	 * application is installed (in that case, the felix cache directory must be removed and reinstalled all the bundles)
	 */
	public final static String VERSIONCODE_FILE = "versionCode";

	public final static String MUSIC_OSGI_FRAMEWORK = "music.osgi.framework";
	MulticastLock multicastLock;
	protected Framework felixFramework;
	FelixUtils mUtils;

	BundleListener mBundleListener = new BundleListener() {
		@Override
		public void bundleChanged(BundleEvent arg0) {
			String msg = arg0.getBundle().getSymbolicName() + " ";

			switch (arg0.getType()) {
			case Bundle.ACTIVE:
				msg += "ACTIVE";
				break;
			case Bundle.INSTALLED:
				msg += "INSTALLED";
				break;
			case Bundle.RESOLVED:
				msg += "RESOLVED";
				break;
			case Bundle.STARTING:
				msg += "STARTING";
				break;
			case Bundle.STOPPING:
				msg += "STOPPING";
				break;

			default:
				break;
			}
			msg += arg0.getType();
			Log.w(TAG, "bundleChanged " + msg);
		}
	};

	BroadcastReceiver mBundleReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getExtras().getString(com.felix.utils.Constants.ACTION);
			if (action.equals(com.felix.utils.Constants.ACTION_REFRESH)) {

			} else if (action.equals(com.felix.utils.Constants.ACTION_INSTALL)) {
				URL bundleUrl;
				try {
					bundleUrl = new URL(intent.getExtras().getString(com.felix.utils.Constants.BUNDLE_URL));
					Bundle b = install(bundleUrl);

					String bundleName = "";
					if (b != null) {
						bundleName = b.getSymbolicName();
						Intent i = new Intent(com.felix.utils.Constants.BUNDLE_ACTIVITY);
						i.putExtra(com.felix.utils.Constants.ACTION, com.felix.utils.Constants.ACTION_INSTALL);
						i.putExtra(com.felix.utils.Constants.BUNDLE_NAME, bundleName);
						sendBroadcast(i);
					}

				} catch (MalformedURLException e) {
					Log.e(TAG, "ACTION_INSTALL err");
					e.printStackTrace();
				}
			} else if (action.equals(com.felix.utils.Constants.ACTION_UNINSTALL)) {
				String bundleName = intent.getExtras().getString(com.felix.utils.Constants.BUNDLE_NAME);

				if (uninstallBundle(bundleName)) {
					Intent i = new Intent(com.felix.utils.Constants.BUNDLE_ACTIVITY);
					i.putExtra(com.felix.utils.Constants.ACTION, com.felix.utils.Constants.ACTION_UNINSTALL);
					i.putExtra(com.felix.utils.Constants.BUNDLE_NAME, bundleName);
					sendBroadcast(i);
				} else {
					Log.e(TAG, "ACTION_UNINSTALL err");
				}

			} else if (action.equals(com.felix.utils.Constants.ACTION_START)) {
				String bundleName = intent.getExtras().getString(com.felix.utils.Constants.BUNDLE_NAME);
				if (startBundle(bundleName)) {
					Intent i = new Intent(com.felix.utils.Constants.BUNDLE_ACTIVITY);
					i.putExtra(com.felix.utils.Constants.ACTION, com.felix.utils.Constants.ACTION_START);
					i.putExtra(com.felix.utils.Constants.BUNDLE_NAME, bundleName);
					sendBroadcast(i);
				} else {
					Log.e(TAG, "ACTION_START err");
				}

			} else if (action.equals(com.felix.utils.Constants.ACTION_STOP)) {
				String bundleName = intent.getExtras().getString(com.felix.utils.Constants.BUNDLE_NAME);
				if (stopBundle(bundleName)) {
					Intent i = new Intent(com.felix.utils.Constants.BUNDLE_ACTIVITY);
					i.putExtra(com.felix.utils.Constants.ACTION, com.felix.utils.Constants.ACTION_STOP);
					i.putExtra(com.felix.utils.Constants.BUNDLE_NAME, bundleName);
					sendBroadcast(i);
				} else {
					Log.e(TAG, "ACTION_STOP err");
				}
			}
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public void onCreate() {
		super.onCreate();

		Log.e(TAG, "onCreate ");
		Toast.makeText(this, "onCreate", Toast.LENGTH_SHORT).show();

		IntentFilter filter = new IntentFilter(com.felix.utils.Constants.BUNDLE_PAGE);
		registerReceiver(mBundleReceiver, filter);
		new Thread() {
			@Override
			public void run() {
				try {
					// Activate WiFi multicast
					WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
					multicastLock = wifiManager.createMulticastLock("MUSIC-Multicast");
					multicastLock.acquire();
				} catch (Exception e) {
					logger.log(Level.WARNING, "Exception when creating the multicast lock", e);
				}

				logger.info("Starting the Android service for the OSGi framework");
				// Get the directory for the application data and for storing
				// the felix framework
				String applicationDataDir = getApplicationDataDir();
				String felixDeploymentDir = applicationDataDir.concat(File.separator).concat(FELIX_DIR);
				logger.info("The deployment directory for the Felix framework is: ".concat(felixDeploymentDir));
				// Check if the .apk version code is already installed
				boolean isSameVersionCode = isSameVersionCode();
				// Check if it is required to (re)deploy the OSGi framework
				if (!isSameVersionCode || !new File(felixDeploymentDir).exists()) {
					// It is required to unzip the MUSIC framework
					// If the felix deployment dir already exists, remove it (to
					// avoid conflicts between different versions)
					if (new File(felixDeploymentDir).exists())
						new File(felixDeploymentDir).delete();

					unzipFile(getResources().openRawResource(R.raw.felix), applicationDataDir);

					// Replace the relative paths of the OSGi bundles in the configuration file
					parseFelixConfigurationFile(felixDeploymentDir);

					System.setProperty(Main.CONFIG_PROPERTIES_PROP,
							"file://".concat(felixDeploymentDir).concat("/conf/parsed_config.properties"));
					Log.e(TAG, "file://".concat(felixDeploymentDir).concat("/conf/parsed_config.properties"));

					// Save the version code
					if (!isSameVersionCode)
						saveVersionCode();
				} else {
					// The framework was already installed, so only restart the
					// bundles
					System.setProperty(Main.CONFIG_PROPERTIES_PROP, "file://".concat(felixDeploymentDir).concat("/conf/restart.properties"));
					Log.e(TAG, "file://".concat(felixDeploymentDir).concat("/conf/restart.properties"));
				}

				// Launch the Felix OSGi framework
				System.setProperty(Main.SYSTEM_PROPERTIES_PROP, "file://".concat(felixDeploymentDir).concat("/conf/system.properties"));

				Main.loadSystemProperties();
				Properties configProps = Main.loadConfigProperties();

				configProps.setProperty(Constants.FRAMEWORK_STORAGE, FELIX_CACHE_DIR);
				configProps.setProperty("felix.auto.deploy.dir", felixDeploymentDir + "/bundle");
				configProps.setProperty(BundleCache.CACHE_ROOTDIR_PROP, felixDeploymentDir);
				configProps.setProperty(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA, "android.util,android.content");

				try {
					logger.info("Starting the OSGi framework");

					// Create an instance of the framework.
					FrameworkFactory factory = new org.apache.felix.framework.FrameworkFactory();
					felixFramework = factory.newFramework(configProps);
					org.osgi.framework.Version v = felixFramework.getVersion();

					Log.e(TAG, "felix version " + v);

					// Initialize the framework, but don't start it yet.
					felixFramework.init();
					// Use the system bundle context to process the auto-deploy
					// and auto-install/auto-start properties.
					AutoProcessor.process(configProps, felixFramework.getBundleContext());
					// Start the framework.
					felixFramework.start();

					felixFramework.getBundleContext().addBundleListener(mBundleListener);

					logger.info("OSGi framework started");

					// Save the framework as a system property
					System.getProperties().put(MUSIC_OSGI_FRAMEWORK, felixFramework);

					// Register the service activity
					Hashtable<String, String> properties = new Hashtable<String, String>();
					properties.put("platform", "android");
					felixFramework.getBundleContext().registerService(Context.class.getName(), FelixService.this, properties);
				} catch (Throwable t) {
					logger.log(Level.WARNING, "The OSGi framework could not be started", t);
				}

				try {
					mUtils = new FelixUtils(FelixService.this, null);
				} catch (NoMusicServiceException e) {
					Log.e(TAG, "onCreate err");
					e.printStackTrace();
				}
				// log출력용
				// mUtils.installBundle(R.raw.slf4j);
				// mUtils.installBundle(R.raw.mybundle);

				// mUtils.installBundle(R.raw.example1);
			}
		}.start();

		// 필요시 사용 startForeground ();
		setServiceAsForeground();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		felixFramework.getBundleContext().removeBundleListener(mBundleListener);

		logger.info("Stopping the OSGi framework...");
		try {
			// Stop the framework
			felixFramework.stop();
		} catch (BundleException e) {
			logger.log(Level.WARNING, "Exception when stopping the OSGi framework", e);
		}
		System.getProperties().remove(MUSIC_OSGI_FRAMEWORK);
		multicastLock.release();

		// 필요시 사용 stopForeground();
		unsetServiceAsForeground();
	}

	/**
	 * This is a wrapper around the new startForeground method from Android 2.0+, using the older APIs if it is not available.
	 */
	private void setServiceAsForeground() {
		// Prepare arguments for the method
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		Notification notification = new NotificationCompat.Builder(this).setContentTitle(getString(R.string.app_name)).setContentText("")
				.setSmallIcon(R.drawable.ic_launcher).build();

		PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(), 0);

		startForeground(NOTIFICATION_ID, notification);
		notificationManager.notify(NOTIFICATION_ID, notification);

	}

	private void unsetServiceAsForeground() {
		stopForeground(true);
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	}

	/*
	 * Unzip an input stream into a directory
	 * 
	 * @param inputStream Input stream corresponding to the zip file
	 * 
	 * @param directory Directory to store the unzipped content
	 */
	protected void unzipFile(InputStream inputStream, String directory) {
		try {
			logger.info("Unzip the OSGi framework for MUSIC");
			JarInputStream jarInputStream = new JarInputStream(inputStream);
			JarEntry jarEntry;
			byte[] buffer = new byte[2048];
			while ((jarEntry = jarInputStream.getNextJarEntry()) != null) {
				File jarEntryFile = new File(directory + File.separator + jarEntry.getName());
				if (jarEntry.isDirectory()) {
					jarEntryFile.mkdirs();
					continue;
				}

				jarEntryFile.getParentFile().mkdirs();

				FileOutputStream fos = new FileOutputStream(jarEntryFile);
				while (true) {
					int read = jarInputStream.read(buffer);
					if (read == -1)
						break;
					fos.write(buffer, 0, read);
				}
				fos.close();
			}
			jarInputStream.close();
		} catch (Throwable t) {
			logger.log(Level.WARNING, "Error unzipping the MUSIC zip file", t);
		}
	}

	protected String getApplicationDataDir() {
		String applicationDataDir = null;
		try {
			// Get the directory if the system property is set
			applicationDataDir = System.getProperty(DEPLOYMENT_DIR_PROPERTY);
			if (applicationDataDir == null)
				// Take the default directory
				applicationDataDir = super.getFilesDir().getAbsolutePath();

		} catch (Throwable t) {
			logger.log(Level.WARNING, "Error getting the directory for the application data", t);
		}
		return applicationDataDir;
	}

	public Bundle install(URL url) throws RuntimeException {
		Log.e(TAG, "install " + url);

		if (url == null)
			throw new RuntimeException("To install a bundle you need to specify a valid location");
		String location = url.toString();
		logger.fine("Installing bundle from: ".concat(location));
		try {
			// Install the bundle
			Bundle bundle = felixFramework.getBundleContext().installBundle(location);
			// Start the bundle
			bundle.start();

			logger.info("Installed bundle from: ".concat(location));

			return bundle;
		} catch (Throwable t) {
			String msg = "Error when installing the bundle from: ".concat(location) + " " + t;
			logger.log(Level.WARNING, msg, t);
			Log.e(TAG, msg);
			throw new RuntimeException(msg);
		}
	}

	boolean uninstallBundle(String bundleName) {
		Log.e(TAG, "uninstallBundle " + bundleName);

		try {
			Bundle bundle = findBundle(bundleName);
			if (bundle != null) {
				bundle.uninstall();
				Log.e(TAG, "uninstallBundle " + bundleName);
				return true;
			}
		} catch (BundleException e) {
			Log.e(TAG, "uninstallBundle err " + bundleName);
			e.printStackTrace();
		}
		return false;
	}

	boolean startBundle(String bundleName) {
		Bundle bundle = findBundle(bundleName);
		if (bundle != null) {
			return startBundle(bundle);
		}

		return false;
	}

	boolean startBundle(Bundle bundle) {
		try {
			bundle.start();
			return true;
		} catch (BundleException e) {
			e.printStackTrace();
			Log.e(TAG, "startBundle err " + bundle);
		}
		return false;
	}

	boolean stopBundle(String bundleName) {
		Bundle bundle = findBundle(bundleName);
		if (bundle != null) {
			return stopBundle(bundle);
		}

		return false;
	}

	boolean stopBundle(Bundle bundle) {
		try {
			bundle.stop();
			return true;
		} catch (BundleException e) {
			e.printStackTrace();
			Log.e(TAG, "startBundle err " + bundle);
		}
		return false;
	}

	Bundle[] getBundle() {
		return felixFramework.getBundleContext().getBundles();
	}

	Bundle findBundle(String bundleName) {
		Bundle[] bundles = getBundle();
		for (Bundle bundle : bundles) {
			if (bundle.getSymbolicName().equals(bundleName)) {
				Log.e(TAG, "findBundle found " + bundleName);
				return bundle;
			}
		}
		return null;
	}

	protected int getVersionCodeFromManifest() {
		int versionCode = -1;
		try {
			PackageInfo packageInfo = super.getPackageManager().getPackageInfo(super.getPackageName(), PackageManager.GET_META_DATA);
			versionCode = packageInfo.versionCode;
		} catch (Throwable t) {
			logger.log(Level.WARNING, "Error getting the version code from the AndroidManifest XML file", t);
		}
		return versionCode;
	}

	protected boolean isSameVersionCode() {
		boolean isSaveVersionCode = false;
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = super.openFileInput(VERSIONCODE_FILE);
			byte[] buffer = new byte[128];
			int count;
			String savedVersionCodeString = "";
			while ((count = fileInputStream.read(buffer)) != -1)
				savedVersionCodeString += new String(buffer, 0, count);
			int savedVersionCode = Integer.parseInt(savedVersionCodeString);
			int manifestVersionCode = getVersionCodeFromManifest();
			if (manifestVersionCode != -1 && manifestVersionCode == savedVersionCode)
				isSaveVersionCode = true;
		} catch (Throwable t) {
			// Error reading the saved version code (perhaps it is the first installation of the MUSIC application)
			logger.warning("The version code is not saved yet. Is it the first installation?");
		} finally {
			if (fileInputStream != null) {
				try {
					fileInputStream.close();
				} catch (Throwable th) {
					logger.log(Level.WARNING, "The versionCode file reader could not be closed", th);
				}
			}
		}
		return isSaveVersionCode;
	}

	protected void saveVersionCode() {
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = super.openFileOutput(VERSIONCODE_FILE, MODE_PRIVATE);
			int versionCode = getVersionCodeFromManifest();
			String versionCodeString = String.valueOf(versionCode);
			fileOutputStream.write(versionCodeString.getBytes());
			logger.info("The current version code of MUSIC is ".concat(versionCodeString));
		} catch (Throwable t) {
			logger.log(Level.WARNING, "The version code could not be saved", t);
		} finally {
			if (fileOutputStream != null) {
				try {
					fileOutputStream.close();
				} catch (Throwable th) {
					logger.log(Level.WARNING, "The versionCode file writer could not be closed", th);
				}
			}
		}
	}

	protected boolean parseFelixConfigurationFile(String felixDeploymentDir) {
		boolean isParsed = false;
		BufferedReader bufferedReader = null;
		BufferedWriter bufferedWriter = null;

		try {
			// Paths to the original and parsed configuration files
			String originalConfigFilePath = felixDeploymentDir.concat("/conf/config.properties");
			String parsedConfigFilePath = felixDeploymentDir.concat("/conf/parsed_config.properties");
			// Open a stream to read the original configuration file
			bufferedReader = new BufferedReader(new FileReader(originalConfigFilePath));
			bufferedWriter = new BufferedWriter(new FileWriter(parsedConfigFilePath));
			Pattern pattern = Pattern.compile("file:bundle/");
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				Matcher matcher = pattern.matcher(line);
				line = matcher.replaceAll("file:".concat(felixDeploymentDir).concat("/bundle/"));
				bufferedWriter.write(line);
				bufferedWriter.newLine();
			}
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (Throwable th) {
					logger.log(Level.WARNING, "The buffered reader for the original configuration file could not be closed", th);
				}
			}
			if (bufferedWriter != null) {
				try {
					bufferedWriter.close();
				} catch (Throwable th) {
					logger.log(Level.WARNING, "The buffered writer for the parsed configuration file could not be closed", th);
				}
			}
		}

		return isParsed;
	}

}