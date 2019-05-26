package com.avogine.junkyard.scene.entity;

import com.avogine.junkyard.scene.entity.event.EntityEvent;

public abstract class EntityComponent {

	protected final int ID;
	
	protected EntityComponent(int entity) {
		this.ID = entity;
	}
	
	public abstract void fireEvent(EntityEvent event);
	
	public int getEntityID() {
		return ID;
	}
	
	public <T extends EntityComponent> Class<?> toSuperclass()  {
		Class<?> clazz = getClass();
		while(clazz.getSuperclass() != null && !clazz.getSuperclass().equals(EntityComponent.class)) {
			clazz = clazz.getSuperclass();
		}
		return clazz;
	}
	
}
