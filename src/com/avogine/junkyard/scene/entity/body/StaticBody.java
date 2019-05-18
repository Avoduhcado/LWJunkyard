package com.avogine.junkyard.scene.entity.body;

import org.joml.Vector3f;

import com.avogine.junkyard.scene.entity.Body;
import com.avogine.junkyard.scene.entity.event.EntityEvent;

public class StaticBody extends Body {

	// TODO Some sort of BodyData class to cut down on params
	
	public StaticBody(int entity, Vector3f position) {
		super(entity);
		this.position = position;
	}

	public StaticBody(int entity) {
		this(entity, new Vector3f());
	}
	
	@Override
	public void fireEvent(EntityEvent event) {
		
	}

}
