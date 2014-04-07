package com.felix.utils;



/**
 * Callback interface to notify about installation/uninstallation of a bundle
 * and for the launch/shutdown of an application
 * 
 * @author Telefonica I+D
 */
public interface IApplicationCallback {
	
	/**
	 * Notifies that the MUSIC middleware is already launched
	 */
	public void onLaunchedMiddleware();
	
	/**
	 * Notifies that the application has been registered in the MUSIC platform
	 * 
	 * @param applicationType
	 */
	public void onInstalledApplication(String applicationType);
	
	/**
	 * Notifies that the application has been unregistered in the MUSIC platform
	 * 
	 * @param applicationType
	 */
	public void onUninstalledApplication(String applicationType);
	
	/**
	 * Notifies that the application has been launched
	 * 
	 * @param applicationType MUSIC application type
	 */
	public void onLaunchedApplication(String applicationType);
	
	/**
	 * Notifies that the application has been shutdown
	 * 
	 * @param applicationType MUSIC application type
	 */
	public void onShutdownApplication(String applicationType);
	
	/**
	 * Notifies that the bundle has been installed in the OSGi framework
	 * 
	 * @param bundleName Bundle-Name attribute of the OSGi bundle
	 */
	public void onInstalledBundle(String bundleName);
	
	/**
	 * Notifies that the bundle has been uninstalled in the OSGi framework
	 * 
	 * @param bundleName Bundle-Name attribute of the OSGi bundle
	 */
	public void onUninstalledBundle(String bundleName);
}