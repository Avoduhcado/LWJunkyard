package com.avogine.junkyard.scene.entity.collision;

import org.joml.Vector3f;
import org.ode4j.ode.DBody;

import com.avogine.junkyard.scene.entity.EntityComponent;
import com.avogine.junkyard.scene.entity.event.EntityEvent;

public abstract class Collider extends EntityComponent {

	protected DBody body;

	public Collider(int entity) {
		super(entity);
	}

	public DBody getBody() {
		return body;
	}

	public Vector3f getBodyPosition() {
		return new Vector3f((float) body.getPosition().get0(), (float) body.getPosition().get1(), (float) body.getPosition().get2());
	}

	@Override
	public void fireEvent(EntityEvent event) {
		// TODO Auto-generated method stub
	
	}

}