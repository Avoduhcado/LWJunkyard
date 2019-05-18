package com.avogine.junkyard.scene.entity.event;

import org.joml.Vector3f;

public class MovementEvent extends EntityEvent {

	public static final int IMMEDIATE = 0;
	public static final int RELATIVE = 1;
	
	private final Vector3f movement;
	
	public MovementEvent(int type, Vector3f movement) {
		super(type);
		this.movement = movement;
	}
	
	public Vector3f getMovement() {
		return movement;
	}
	
}
