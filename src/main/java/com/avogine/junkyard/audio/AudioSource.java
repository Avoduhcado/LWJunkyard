package com.avogine.junkyard.audio;

import java.nio.FloatBuffer;
import java.util.Arrays;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.SOFTDirectChannels;

import com.avogine.junkyard.memory.MemoryManaged;

public class AudioSource implements MemoryManaged {

	private int id;
	
	private float volume = 1f;
	
	public AudioSource() {
		this.id = AL10.alGenSources();
		AL10.alSourcef(id, AL10.AL_GAIN, volume);
		AL10.alSourcef(id, AL10.AL_PITCH, 1f);
		AL10.alSource3f(id, AL10.AL_POSITION, 0, 0, 0);
		AL10.alSource3f(id, AL10.AL_DIRECTION, 0, 1, 0);
		
		AL10.alSourcef(id, AL10.AL_ROLLOFF_FACTOR, 1);
		AL10.alSourcef(id, AL10.AL_REFERENCE_DISTANCE, 256);
		AL10.alSourcef(id, AL10.AL_MAX_DISTANCE, 512);
		//AL10.alSourcei(id, AL10.AL_MAX_DISTANCE, 500);
		//AL10.alSourcef(id, AL10.AL_MIN_GAIN, 0.2f);
		
		AL10.alSourcei(id, SOFTDirectChannels.AL_DIRECT_CHANNELS_SOFT, AL10.AL_TRUE);
	}
	
	public void play(AudioBuffer buffer) {
		stop();
		AL10.alSourcei(id, AL10.AL_BUFFER, buffer.getId());
		AL10.alSourcePlay(id);
	}
	
	/** XXX idk when to unqueue, probably manage that in the object that is queuing up sounds?
	* Use with caution
	*/
	public void playQueue(AudioBuffer buffer) {
		AL10.alSourceQueueBuffers(id, buffer.getId());
		AL10.alSourcePlay(id);
	}
	
	public void stop() {
		AL10.alSourceStop(id);
	}
	
	public void pause() {
		AL10.alSourcePause(id);
	}
	
	public void setLooping(boolean loop) {
		AL10.alSourcei(id, AL10.AL_LOOPING, loop ? AL10.AL_TRUE : AL10.AL_FALSE);
	}

	public void setPosition(Vector3f position) {
		AL10.alSource3f(id, AL10.AL_POSITION, position.x, position.y, position.z);
	}
	
	public void addPosition(Vector3f position) {
		FloatBuffer[] positionBuffers = new FloatBuffer[3];
		Arrays.fill(positionBuffers, BufferUtils.createFloatBuffer(1));
		AL10.alGetSource3f(id, AL10.AL_POSITION, positionBuffers[0], positionBuffers[1], positionBuffers[2]);
		AL10.alSource3f(id, AL10.AL_POSITION, positionBuffers[0].get() + position.x, positionBuffers[1].get() + position.y, positionBuffers[2].get() + position.z);
	}
	
	public int getId() {
		return id;
	}
	
	@Override
	public void cleanUp() {
		AL10.alDeleteSources(id);
	}
	
}
