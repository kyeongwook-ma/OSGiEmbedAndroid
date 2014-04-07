/*
 * The MUSIC project (Contract No. IST-035166) is an Integrated Project (IP) 
 * within the 6th Framework Programme, Priority 2.5.5 (Software and Services).
 *
 * More information about the project is available at: http://www.ist-music.eu
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307 USA
 */
package com.felix.utils;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Utility class to simplify the integration of MUSIC applications in the
 * Android platform. This utility class encapsulates the installation/uninstallation
 * of OSGi bundles and the launch/shutdown of MUSIC applications.
 * 
 * @author Telefonica I+D
 */
public class FelixUtils {
	
	/**
	 * Reference to the callback
	 */
	private IApplicationCallback callback;
	
	/**
	 * Application context to interact with the Android platform
	 */
	private Context context;
	
	/**
	 * Menu broadcast receiver to get the status of the MUSIC middleware
	 */
	private MenuBroadcastReceiver menuBroadcastReceiver;
	
	/**
	 * Android broadcast receiver for bundle management
	 */
	private BundleBroadcastReceiver bundleBroadcastReceiver;
	
	/**
	 * Android broadcast receiver for application management
	 */
	private ApplicationBroadcastReceiver applicationBroadcastReceiver;
	
	/**
	 * Boolean indicating if the Android service corresponding to the
	 * MUSIC middleware finished to start (true) or not (false)
	 */
	private boolean isMusicServiceStarted = false;
	
	/**
	 * Boolean indicating if the bundle management is active
	 */
	private boolean isBundleManagementActivated = false;
	
	/**
	 * Boolean indicating if the application management is active
	 */
	private boolean isApplicationManagementActivated = false;
	
	/**
	 * Logger
	 */
	private final static Logger logger = Logger.getLogger(FelixUtils.class.getName());
	
	/**
	 * Constructor
	 * 
	 * @param context Android context
	 * @param callback Callback interface
	 * 
	 * @throws NoMusicServiceException When the service cannot be launch because the middleware is not installed
	 */
	public FelixUtils(Context context, IApplicationCallback callback) throws NoMusicServiceException {
		try {
			this.context = context;
			menuBroadcastReceiver = null;
			applicationBroadcastReceiver = null;
			bundleBroadcastReceiver = null;
			// Set the callback
			setCallback(callback);
			// Launch the MUSIC middleware
			launchMusicService();
		} catch (NoMusicServiceException e) {
			setCallback(null);
			throw e;
		} catch (Throwable t) {
			logger.log(Level.WARNING, "Error in the constructor", t);
			setCallback(null);
			throw new NoMusicServiceException(t.getMessage());
		}
	}

	/**
	 * Launch the Android service corresponding to the MUSIC middleware if it was not already launched.
	 * Also register the menuBroadcastReceiver to get notifications about the status of the MUSIC middleware
	 * 
	 * @throws NoMusicServiceException When the service cannot be launch because the middleware is not installed
	 */
	private void launchMusicService() throws NoMusicServiceException {
		try {
			// Start the MUSIC Android service
			ComponentName comp = new ComponentName("com.felix", "com.felix.FelixService");
			ComponentName service = context.startService(new Intent().setComponent(comp));
			if(service == null)
				throw new NoMusicServiceException("The MUSIC middleware is not installed");
			
			// Send the intent to receive the reply which confirms if the MUSIC mw is already launched
			Intent refreshIntent = new Intent(Constants.MENU_PAGE);
			refreshIntent.putExtra(Constants.ACTION, Constants.ACTION_REFRESH);
			context.sendBroadcast(refreshIntent);
		} catch (NoMusicServiceException e) {
			throw e;
		} catch (Throwable t) {
			logger.log(Level.WARNING, "Error launching the MUSIC middleware", t);
			throw new NoMusicServiceException(t.getMessage());
		}
	}
	
	/**
	 * Register the Android broadcast receivers to get notifications about
	 * bundles and applications
	 */
	private void registerBroadcastReceivers() {
		if (context != null) {
			if (menuBroadcastReceiver == null) {
				menuBroadcastReceiver = new MenuBroadcastReceiver();
				context.registerReceiver(menuBroadcastReceiver, new IntentFilter(Constants.MENU_ACTIVITY));
			}
			if (bundleBroadcastReceiver == null) {
				bundleBroadcastReceiver = new BundleBroadcastReceiver();
				context.registerReceiver(bundleBroadcastReceiver, new IntentFilter(Constants.BUNDLE_ACTIVITY));
			}
			if (applicationBroadcastReceiver == null) {
				applicationBroadcastReceiver = new ApplicationBroadcastReceiver();
				context.registerReceiver(applicationBroadcastReceiver, new IntentFilter(Constants.APPLICATION_ACTIVITY));
			}
		}
	}

	/**
	 * Unregister the broadcast receivers
	 */
	private void unregisterBroadcastReceivers() {
		if (context != null) {
			if (menuBroadcastReceiver != null)
				context.unregisterReceiver(menuBroadcastReceiver);
			if (bundleBroadcastReceiver != null)
				context.unregisterReceiver(bundleBroadcastReceiver);
			if (applicationBroadcastReceiver != null)
				context.unregisterReceiver(applicationBroadcastReceiver);
		}
	}
	
	/**
	 * Set the callback to notify about events related to OSGi bundles and MUSIC applications
	 * 
	 * @param callback Callback instance, or <code>null</code> to unregister the previous callback
	 */
	public void setCallback(IApplicationCallback callback) {
		this.callback = callback;
		if (callback != null) {
			// Register broadcast receivers for the applications and bundles
			registerBroadcastReceivers();
		}
		else
			unregisterBroadcastReceivers();
	}

	public void installBundle(String bundleUrl) {
		if (context != null) {
			Intent installIntent = new Intent(Constants.BUNDLE_PAGE);
			installIntent.putExtra(Constants.ACTION, Constants.ACTION_INSTALL);
			installIntent.putExtra(Constants.BUNDLE_URL, bundleUrl);
			context.sendBroadcast(installIntent);
		}
	}
	
	/**
	 * Install an OSGi bundle in the MUSIC platform
	 * 
	 * @param rawResourceId Resource Id which identifies the OSGi bundle file
	 */
	public void installBundle(int rawResourceId) {
		try {
			// Get the bundle file name from the properties of the bundle (manifest file)
			JarInputStream jis = new JarInputStream(context.getResources().openRawResource(rawResourceId));
			Attributes bundleAttributes = jis.getManifest().getMainAttributes();
			String bundleSymbolicName = bundleAttributes.getValue("Bundle-SymbolicName");
			String bundleFileName;
			if (bundleSymbolicName != null && bundleSymbolicName.length() > 0)
				bundleFileName = bundleSymbolicName + ".jar";
			else
				bundleFileName = Integer.toString(rawResourceId) + ".jar";
			jis.close();
			
			logger.info("Copying the bundle with the raw id: " + rawResourceId + " with the name " + bundleFileName);
			
			// Write the OSGi bundle file in the filesystem with MODE_WORLD_READABLE
			InputStream is = context.getResources().openRawResource(rawResourceId);
			OutputStream os = context.openFileOutput(bundleFileName, Context.MODE_WORLD_READABLE);
			byte[] b = new byte[1024];
			int read;
			while ((read = is.read(b)) != -1)
				os.write(b, 0, read);
			os.close();
			is.close();
							
			// Once the bundle is available with a bundlePath, install the bundle
			try {
				// Get the bundle file path
				String bundleUrl = "file://" + context.getFileStreamPath(bundleFileName).getAbsolutePath();
				installBundle(bundleUrl);
			} catch (Throwable t) {
				logger.log(Level.WARNING, "The bundle is not available in the filesystem", t);
			}
		} catch(Throwable t) {
			logger.log(Level.WARNING, "Error writing a raw resource id, identifying a bundle, in the file system", t);
		}
	}
	
	/**
	 * Uninstall an OSGi bundle in the MUSIC platform
	 * 
	 * @param bundleName Bundle name (corresponding to the attribute Bundle-Name in the manifest file)
	 */
	public void uninstallBundle(String bundleName) {
		if (context != null && bundleName != null && bundleName.length() > 0) {
			Intent uninstallIntent = new Intent(Constants.BUNDLE_PAGE);
			uninstallIntent.putExtra(Constants.ACTION, Constants.ACTION_UNINSTALL);
			uninstallIntent.putExtra(Constants.BUNDLE_NAME, bundleName);
			context.sendBroadcast(uninstallIntent);
		}
	}
	
	/**
	 * Launch a MUSIC application
	 * 
	 * @param applicationType MUSIC application type
	 */
	public void launchApplication(String applicationType) {
		if (context != null && applicationType != null && applicationType.length() > 0) {
			Intent launchIntent = new Intent(Constants.APPLICATION_PAGE);
			launchIntent.putExtra(Constants.APPLICATION_TYPE, applicationType);
			launchIntent.putExtra(Constants.ACTION, Constants.ACTION_LAUNCH);
			context.sendBroadcast(launchIntent);
		}
	}
	
	/**
	 * Shutdown a MUSIC application
	 * 
	 * @param applicationType MUSIC application type
	 */
	public void shutdownApplication(String applicationType) {	
		if (context != null && applicationType != null && applicationType.length() > 0) {
			Intent shutdownIntent = new Intent(Constants.APPLICATION_PAGE);
			shutdownIntent.putExtra(Constants.APPLICATION_TYPE, applicationType);
			shutdownIntent.putExtra(Constants.ACTION, Constants.ACTION_SHUTDOWN);
			context.sendBroadcast(shutdownIntent);
		}
	}
	
	/**
	 * Android broadcast receiver for menu notifications
	 * 
	 * @author Telefonica I+D
	 */
	private class MenuBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getExtras().getString(Constants.ACTION);
			if (action.equals(Constants.ACTION_ADD)) {
				String guiPage = intent.getExtras().getString(Constants.GUI_PAGE);
				if (guiPage.equals(Constants.BUNDLE_PAGE))
					isBundleManagementActivated = true;
				else if (guiPage.equals(Constants.APPLICATION_PAGE))
					isApplicationManagementActivated = true;
			}
			else if (action.equals(Constants.ACTION_REMOVE)) {
				String guiPage = intent.getExtras().getString(Constants.GUI_PAGE);
				if (guiPage.equals(Constants.BUNDLE_PAGE))
					isBundleManagementActivated = false;
				else if (guiPage.equals(Constants.APPLICATION_PAGE))
					isApplicationManagementActivated = false;
			}
			else if (action.equals(Constants.ACTION_REFRESH)) {
				boolean tempIsBundleManagementActivated = false;
				boolean tempIsApplicationManagementActivated = false;
				String[] guiPages = intent.getExtras().getStringArray(Constants.GUI_PAGE_ARRAY);
				if (guiPages != null) {
					for (int i=0; i<guiPages.length; i++) {
						if (guiPages[i].equals(Constants.BUNDLE_PAGE))
							tempIsBundleManagementActivated = true;
						else if (guiPages[i].equals(Constants.APPLICATION_PAGE))
							tempIsApplicationManagementActivated = true;
					}
				}
				isBundleManagementActivated = tempIsBundleManagementActivated;
				isApplicationManagementActivated = tempIsApplicationManagementActivated;
			}
			
			boolean tempIsMusicServiceStarted = (isBundleManagementActivated && isApplicationManagementActivated) ? true : false;
			if (tempIsMusicServiceStarted != isMusicServiceStarted) {
				isMusicServiceStarted = tempIsMusicServiceStarted;
				if (isMusicServiceStarted) {
					if (callback != null)
						callback.onLaunchedMiddleware();
					// Refresh the list of MUSIC applications (in case an application is already registered)
					Intent refreshIntent = new Intent(Constants.APPLICATION_PAGE);
					refreshIntent.putExtra(Constants.ACTION, Constants.ACTION_REFRESH);
					context.sendBroadcast(refreshIntent);
				}
			}
		}
	}

	/**
	 * Android broadcast receiver for bundle management notifications
	 * 
	 * @author Telefonica I+D
	 */
	private class BundleBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (callback != null) {
				String action = intent.getExtras().getString(Constants.ACTION);
				String bundleName = intent.getExtras().getString(Constants.BUNDLE_NAME);
				if (action.equals(Constants.ACTION_ADD))
					callback.onInstalledBundle(bundleName);
				else if (action.equals(Constants.ACTION_REMOVE))
					callback.onUninstalledBundle(bundleName);
			}
		}
	}

	/**
	 * Android broadcast receiver for application management notifications
	 * 
	 * @author Telefonica I+D
	 */
	private class ApplicationBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (callback != null) {
				String action = intent.getExtras().getString(Constants.ACTION);
				String applicationType = intent.getExtras().getString(Constants.APPLICATION_TYPE);
				if (action.equals(Constants.ACTION_ADD)) {
					callback.onInstalledApplication(applicationType);
				}
				else if (action.equals(Constants.ACTION_REMOVE)) {
					callback.onUninstalledApplication(applicationType);
				}
				else if (action.equals(Constants.ACTION_UPDATE)) {
					int applicationStatus = intent.getExtras().getInt(Constants.APPLICATION_STATUS);
					if (applicationStatus == 0)
						callback.onShutdownApplication(applicationType);
					else if (applicationStatus == 2)
						callback.onLaunchedApplication(applicationType);
				}
				else if (action.equals(Constants.ACTION_REFRESH)) {
					String[] applicationTypes = intent.getExtras().getStringArray("application.type.array");
					if (applicationTypes != null && applicationTypes.length > 0) {
						for (int i=0; i<applicationTypes.length; i++)
							callback.onInstalledApplication(applicationTypes[i]);
					}
				}
			}
		}
	}
}
