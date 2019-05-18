package com.avogine.junkyard.scene.audio;

import org.lwjgl.openal.AL10;

import com.avogine.junkyard.audio.AudioSource;
import com.avogine.junkyard.audio.load.AudioCache;
import com.avogine.junkyard.memory.MemoryManaged;

public class BackgroundMusic implements MemoryManaged {

	private AudioSource source;
	
	private String trackName;
	
	public BackgroundMusic(String trackName) {
		source = new AudioSource();
		// TODO Make this nicer
		AL10.alSourcei(source.getId(), AL10.AL_SOURCE_RELATIVE, AL10.AL_TRUE);
		source.setLooping(true);
		this.trackName = trackName;
	}
	
	public void play() {
		source.play(AudioCache.get().getSound(trackName));
	}
	
	@Override
	public void cleanUp() {
		source.cleanUp();
	}
	
}
