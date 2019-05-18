package com.avogine.junkyard.scene.entity.body;

import org.joml.Matrix3f;
import org.joml.Vector3f;

import com.avogine.junkyard.Theater;
import com.avogine.junkyard.scene.entity.Body;
import com.avogine.junkyard.scene.entity.event.EntityEvent;
import com.avogine.junkyard.scene.entity.event.SpeedChangeEvent;

public class KineticBody extends Body implements Movable {

	protected Vector3f velocity = new Vector3f();
	protected float speed;
	
	protected boolean awake;
	
	public KineticBody(int entity) {
		super(entity);
		
		this.speed = 1f;
	}
	
	@Override
	public void move() {
		if(velocity.length() == 0) {
			setAwake(false);
			return;
		}
		velocity.normalize();
		
		velocity.mul(getInvertedYaw());
		velocity.mul(getSpeed());
		velocity.mul((float) Theater.getDelta());
		
		position.add(velocity);
		velocity.zero();
	}

	@Override
	public void fireEvent(EntityEvent event) {
		if(event instanceof SpeedChangeEvent) {
			SpeedChangeEvent speedEvent = (SpeedChangeEvent) event;
			switch(event.getType()) {
			case SpeedChangeEvent.ADD_SPEED:
				setSpeed(getSpeed() + speedEvent.getSpeedChange());
				break;
			case SpeedChangeEvent.SET_SPEED:
				setSpeed(speedEvent.getSpeedChange());
				break;
			}
		}
	}

	/**
	 * @return Matrix rotated on the y axis by the negative value of the y rotation (in radians).
	 */
	protected Matrix3f getInvertedYaw() {
		return new Matrix3f().rotateY((float) Math.toRadians(-rotation.y()));
	}
	
	public float getSpeed() {
		return speed;
	}
	
	public void setSpeed(float speed) {
		this.speed = speed;
	}
	
	@Override
	public boolean isAwake() {
		return awake;
	}
	
	public void setAwake(boolean awake) {
		this.awake = awake;
	}
	
}
