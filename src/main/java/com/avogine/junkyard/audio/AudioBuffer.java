package com.avogine.junkyard.audio;

import org.lwjgl.openal.AL10;

import com.avogine.junkyard.audio.data.OggData;
import com.avogine.junkyard.audio.data.WaveData;
import com.avogine.junkyard.memory.MemoryManaged;

public class AudioBuffer implements MemoryManaged {

	private int id;
	
	public AudioBuffer() {
		this.id = AL10.alGenBuffers();
	}
	
	// XXX BLEH
	public void loadToBuffer(WaveData data) {
		AL10.alBufferData(id, data.getFormat(), data.getData(), data.getFrequency());
	}
	
	public void loadToBuffer(OggData data) {
		AL10.alBufferData(id, data.getFormat(), data.getData(), data.getFrequency());
	}
	
	public int getId() {
		return id;
	}
	
	@Override
	public void cleanUp() {
		AL10.alDeleteBuffers(id);
	}

}
