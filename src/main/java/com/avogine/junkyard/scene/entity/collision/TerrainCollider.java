package com.avogine.junkyard.scene.entity.collision;

import org.ode4j.ode.DGeom;
import org.ode4j.ode.DSpace;
import org.ode4j.ode.DTriMeshData;
import org.ode4j.ode.DWorld;
import org.ode4j.ode.OdeHelper;

import com.avogine.junkyard.scene.entity.Body;
import com.avogine.junkyard.scene.entity.render.TerrainModel;

public class TerrainCollider extends Collider {

	private DGeom plane;
	private DTriMeshData triMeshData;
	
	public TerrainCollider(int entity, DWorld world, DSpace space, Body entityBody, TerrainModel entityModel) {
		super(entity);
		body = OdeHelper.createBody(world);
		body.setPosition(entityBody.getPosition().x, entityBody.getPosition().y, entityBody.getPosition().z);
		body.setGravityMode(false);
		body.setKinematic();
		
		// TODO Change this into a heightfield probably
		triMeshData = entityModel.getTriMeshData();
		plane = OdeHelper.createTriMesh(space, triMeshData, null, null, null);
		//plane = OdeHelper.createPlane(space, 0, 1, 0, 0);
		plane.setBody(body);
	}

}
