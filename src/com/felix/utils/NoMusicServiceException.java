package com.felix.utils;



/**
 * Custom exception for the integration of MUSIC in Android
 * 
 * @author Telefonica I+D
 */
public class NoMusicServiceException extends Exception {
	private static final long serialVersionUID = -1853501842461096373L;

	/**
	 * Constructor
	 * 
	 * @param message
	 */
	public NoMusicServiceException(String message) {
		super(message);
	}
}
