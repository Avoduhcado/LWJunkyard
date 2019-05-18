package com.avogine.junkyard.scene.entity.body;

import org.joml.Vector3f;

import com.avogine.junkyard.scene.entity.Body;
import com.avogine.junkyard.scene.entity.EntityComponent;
import com.avogine.junkyard.scene.entity.event.EntityEvent;

public class Follower extends EntityComponent {

	private int leader;
	
	private Vector3f followPosition;
	private Vector3f followRotation;
	
	public Follower(int entity, int leader) {
		super(entity);
		this.leader = leader;
		
		this.followPosition = new Vector3f(0, 30, -35);
		this.followRotation = new Vector3f(25, 180, 0);
	}

	public void follow(Body ours, Body theirs) {
		theirs.getPosition().add(followPosition, ours.getPosition());
		ours.setRotation(followRotation);
	}
	
	@Override
	public void fireEvent(EntityEvent event) {
		// TODO Auto-generated method stub

	}
	
	public int getLeader() {
		return leader;
	}
	
	public void setLeader(int leader) {
		this.leader = leader;
	}

}
