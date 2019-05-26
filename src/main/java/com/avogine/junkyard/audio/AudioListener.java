package com.avogine.junkyard.audio;

import org.lwjgl.openal.AL10;

public class AudioListener {

	// TODO Tie this bad boy to the camera
	
	public AudioListener() {
		AL10.alListener3f(AL10.AL_POSITION, 0, 0, 0);
		AL10.alListener3f(AL10.AL_VELOCITY, 0, 0, 0);
	}
	
}
