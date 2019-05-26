package com.avogine.junkyard.util;

public class ResourceConstants {

	/** 
	 * The dynamic path to the resources folder depending upon whether or not we're running in dev 
	 * (determined by the presence of the environment variable "dev") or if we're released and just 
	 * looking for the resources folder next to the jar 
	 */
	public static final String RESOURCES_PATH = System.getenv("dev") == null ? "/resources/" : "/src/main/resources/";
	
	public static final String TEXTURE_PATH = "textures/";
	public static final String SHADER_PATH = "shaders/";
	public static final String MODEL_PATH = "models/";
	public static final String AUDIO_PATH = "audio/";
	
}
