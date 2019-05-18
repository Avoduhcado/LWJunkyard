package com.avogine.junkyard.scene.entity;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;

import com.avogine.junkyard.scene.entity.event.MovementEvent;

public abstract class Body extends EntityComponent {

	protected Vector3f position = new Vector3f();
	// TODO Quaternion?
	/** Pitch / Yaw / Roll */
	protected Vector3f rotation = new Vector3f();
	
	protected Vector3f scale = new Vector3f(1);
	
	protected List<EntityComponent> movementListeners = new ArrayList<>();
	
	public Body(int entity) {
		super(entity);
	}
	
	public Vector3f getPosition() {
		return position;
	}
	
	public void setPosition(Vector3f position) {
		this.position = position;
		
		MovementEvent event = new MovementEvent(MovementEvent.IMMEDIATE, position);
		movementListeners.stream().forEach(e -> e.fireEvent(event));
	}
	
	public Vector3f getRotation() {
		return rotation;
	}
	
	public void setRotation(Vector3f rotation) {
		this.rotation = rotation;
	}
	
	public Vector3f getScale() {
		return scale;
	}
	
	public void setScale(Vector3f scale) {
		this.scale = scale;
	}
	
	public void addListener(EntityComponent component) {
		this.movementListeners.add(component);
	}
	
	public void removeListener(EntityComponent component) {
		this.movementListeners.remove(component);
	}
	
}
