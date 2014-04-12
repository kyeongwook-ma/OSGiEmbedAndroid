package com.example;

import java.util.logging.Level;
import java.util.logging.Logger;


import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	private final static Logger logger = Logger.getLogger(Activator.class.getName());

	public void start(BundleContext context) throws Exception {
		System.out.println("Activator start");
		logger.log(Level.WARNING, "Activator start " );		
	}
	public void stop(BundleContext context) throws Exception {
		System.out.println("Activator stop");
		logger.log(Level.WARNING, "Activator stop ");
	}
}
