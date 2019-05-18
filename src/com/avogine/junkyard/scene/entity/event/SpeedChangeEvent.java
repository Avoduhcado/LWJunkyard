package com.avogine.junkyard.scene.entity.event;

public class SpeedChangeEvent extends EntityEvent {

	public static final int ADD_SPEED = 1;
	public static final int SET_SPEED = 2;
	
	private final float speedChange;
	
	public SpeedChangeEvent(int type, float speedChange) {
		super(type);
		this.speedChange = speedChange;
	}
	
	public float getSpeedChange() {
		return speedChange;
	}

}
