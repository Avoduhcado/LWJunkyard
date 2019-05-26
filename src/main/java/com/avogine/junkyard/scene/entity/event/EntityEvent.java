package com.avogine.junkyard.scene.entity.event;

public abstract class EntityEvent {

	protected final int type;
	
	protected EntityEvent(int type) {
		this.type = type;
	}
	
	public int getType() {
		return type;
	}
}
