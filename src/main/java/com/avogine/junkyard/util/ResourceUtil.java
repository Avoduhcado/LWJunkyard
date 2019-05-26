package com.avogine.junkyard.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ResourceUtil {

	/**
	 * Return an <code>InputStream</code> to read the supplied resource file from.
	 * </br></br>
	 * This will attempt to automatically determine the actual location of the file based on whether we're running in development or production.
	 * @param resource
	 * @return
	 * @throws FileNotFoundException
	 */
	public static InputStream getInputStreamForResource(String resource) throws FileNotFoundException {
		return new FileInputStream(System.getProperty("user.dir") + ResourceConstants.RESOURCES_PATH + resource);
	}
	
}
