package com.avogine.junkyard.scene;

import javax.vecmath.Vector3f;

import com.avogine.junkyard.Theater;
import com.avogine.junkyard.memory.MemoryManaged;
import com.avogine.junkyard.scene.entity.Body;
import com.avogine.junkyard.scene.entity.body.Movable;
import com.avogine.junkyard.scene.entity.collision.Collider;
import com.avogine.junkyard.util.ConversionUtils;
import com.bulletphysics.collision.broadphase.AxisSweep3;
import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.Transform;

public class Physics implements MemoryManaged {

	private static final int maxSubSteps = 10;
	
	private Cast cast;
	
	private DynamicsWorld world;
	
	public Physics(Cast cast) {
		this.cast = cast;
		
		CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
		CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);
		Vector3f worldMin = new Vector3f(-1000f, -1000f, -1000f);
		Vector3f worldMax = new Vector3f(1000f, 1000f, 1000f);
		AxisSweep3 sweepBP = new AxisSweep3(worldMin, worldMax);
		BroadphaseInterface broadphase = sweepBP;
		ConstraintSolver solver = new SequentialImpulseConstraintSolver();
		world = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
		world.setGravity(new Vector3f(0f, -9.8f, 0f));
	}
	
	public void doPhysics() {
		world.stepSimulation((float) Theater.getDelta(), maxSubSteps);
		
		for(ComponentMap entity : cast.getEntitiesWithComponents(Body.class, Collider.class)) {
			Collider collider = entity.getAs(Collider.class);
			RigidBody rigidBody = collider.getRigidBody();
			if(rigidBody.isActive()) {
				Transform transform = new Transform();
				transform.setIdentity();
				rigidBody.getMotionState().getWorldTransform(transform);
				Vector3f rigidOrigin = rigidBody.getMotionState().getWorldTransform(transform).origin;
				entity.getAs(Body.class).setPosition(ConversionUtils.convertVecmathToJoml(rigidOrigin));
			}
		}
		
		// XXX Should I just be grabbing the component or filtering it out?
		cast.getEntitiesWithComponent(Body.class).stream()
			.map(b -> b.getAs(Body.class))
			.filter(Movable.class::isInstance)
			.map(Movable.class::cast)
			.filter(Movable::isAwake)
			.forEach(Movable::move);
	}
	
	public DynamicsWorld getWorld() {
		return world;
	}
	
	public void setWorld(DynamicsWorld world) {
		this.world = world;
	}

	@Override
	public void cleanUp() {
		world.destroy();
	}

}
