package com.avogine.junkyard.scene.entity.collision;

import org.ode4j.ode.DGeom;
import org.ode4j.ode.DMass;
import org.ode4j.ode.DSpace;
import org.ode4j.ode.DWorld;
import org.ode4j.ode.OdeHelper;

import com.avogine.junkyard.scene.entity.Body;

public class KineticCollider extends Collider {

	private DGeom sphere;
	
	public KineticCollider(int entity, DWorld world, DSpace space, Body entityBody) {
		super(entity);
		body = OdeHelper.createBody(world);
		DMass mass = OdeHelper.createMass();
		mass.setSphereTotal(1.0, 0.2);
		body.setMass(mass);
		body.setPosition(entityBody.getPosition().x, entityBody.getPosition().y, entityBody.getPosition().z);
		
		sphere = OdeHelper.createSphere(space, 0.2);
		sphere.setBody(body);
	}

}
