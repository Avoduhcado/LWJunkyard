package com.avogine.junkyard.scene.entity;

import com.avogine.junkyard.scene.entity.event.EntityEvent;

public abstract class MeshComponent extends EntityComponent {

	protected MeshComponent(int entity) {
		super(entity);
	}

	@Override
	public void fireEvent(EntityEvent event) {
		// TODO Auto-generated method stub

	}

}
