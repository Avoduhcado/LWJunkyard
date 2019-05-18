package com.avogine.junkyard.scene.entity.collision;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.avogine.junkyard.scene.entity.Body;
import com.avogine.junkyard.scene.entity.EntityComponent;
import com.avogine.junkyard.scene.entity.event.EntityEvent;
import com.bulletphysics.collision.dispatch.CollisionFlags;
import com.bulletphysics.collision.shapes.BvhTriangleMeshShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.collision.shapes.StridingMeshInterface;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

public class Collider extends EntityComponent {

	private RigidBody rigidBody;
	
	protected Collider(int entity) {
		super(entity);
	}
	
	public Collider(int entity, DynamicsWorld world, Body body) {
		this(entity);
		
		CollisionShape shape = new SphereShape(0.5f);
		
		float mass = 1f;
		Vector3f localInertia = new Vector3f(0f, 0f, 0f);
		shape.calculateLocalInertia(mass, localInertia);
		
		Transform startTransform = new Transform();
		startTransform.setIdentity();
		startTransform.origin.set(body.getPosition().x, body.getPosition().y, body.getPosition().z);
		startTransform.setRotation(new Quat4f(0, 0, 0, 1));
		
		DefaultMotionState myMotionState = new DefaultMotionState(startTransform);
		
		RigidBodyConstructionInfo cInfo = new RigidBodyConstructionInfo(mass, myMotionState, shape, new Vector3f());
		
		rigidBody = new RigidBody(cInfo);
		rigidBody.setCollisionFlags(CollisionFlags.CHARACTER_OBJECT);
		//rigidBody.setCollisionFlags(rigidBody.getCollisionFlags() | CollisionFlags.STATIC_OBJECT);
		
		world.addRigidBody(rigidBody);
	}
	
	public Collider(int entity, DynamicsWorld world, StridingMeshInterface smi, Body body) {
		this(entity);
		BvhTriangleMeshShape trimeshShape = new BvhTriangleMeshShape(smi, true);
		trimeshShape.setMargin(0.5f);
		
		float mass = 0f;
		Transform startTransform = new Transform();
		startTransform.setIdentity();
		startTransform.origin.set(body.getPosition().x, body.getPosition().y, body.getPosition().z);
		startTransform.setRotation(new Quat4f(0, 0, 0, 1));
		
		DefaultMotionState myMotionState = new DefaultMotionState(startTransform);
		
		RigidBodyConstructionInfo cInfo = new RigidBodyConstructionInfo(mass, myMotionState, trimeshShape, new Vector3f());
		
		rigidBody = new RigidBody(cInfo);
		//rigidBody.setCollisionFlags(rigidBody.getCollisionFlags() | CollisionFlags.STATIC_OBJECT);
		
		world.addRigidBody(rigidBody);
	}

	@Override
	public void fireEvent(EntityEvent event) {
		// TODO Auto-generated method stub

	}

	public RigidBody getRigidBody() {
		return rigidBody;
	}
	
}
