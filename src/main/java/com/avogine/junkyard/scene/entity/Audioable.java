package com.avogine.junkyard.scene.entity;

public abstract class Audioable extends EntityComponent {

	protected float gain;
	// TODO Rolloff/other stuff
	
	protected Audioable(int entity) {
		super(entity);
	}
	
	public abstract void play();
	
	public float getGain() {
		return gain;
	}
	
	public void setGain(float gain) {
		this.gain = gain;
	}

}
