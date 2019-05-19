package com.avogine.junkyard.util;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class AvoLog {

	private static FileHandler fileTxt;
	private static SimpleFormatter formatterTxt;
	
	private static Logger logger;

	static {
		try {
			setup();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	static protected void setup() throws IOException {
		// get the global logger to configure it
		logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

		// TODO Re-enable this but enable console logging if in debug mode
		// suppress the logging output to the console
		/*Logger rootLogger = Logger.getLogger("");
		Handler[] handlers = rootLogger.getHandlers();
		if (handlers[0] instanceof ConsoleHandler) {
			rootLogger.removeHandler(handlers[0]);
		}*/

		logger.setLevel(Level.INFO);
		fileTxt = new FileHandler("avolog.log", 16 * 1024 * 1024, 1, true);

		// create a TXT formatter
		formatterTxt = new SimpleFormatter();
		fileTxt.setFormatter(formatterTxt);
		logger.addHandler(fileTxt);
	}
	
	public static void log(Level logLevel, String message, Object category) {
		logger.log(logLevel, message, category);
	}

	public static void debug(String message, Object category) {
		log(Level.FINE, message, category);
	}
	
	public static void info(String message, Object category) {
		log(Level.INFO, message, category);
	}
	
	public static void warn(String message, Object category) {
		log(Level.WARNING, message, category);
	}
	
	public static void severe(String message, Object category) {
		log(Level.SEVERE, message, category);
	}

}
