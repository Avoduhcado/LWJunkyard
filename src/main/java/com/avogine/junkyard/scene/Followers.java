package com.avogine.junkyard.scene;

import com.avogine.junkyard.scene.entity.Body;
import com.avogine.junkyard.scene.entity.body.Follower;

public class Followers {

	private Cast cast;
	
	public Followers(Cast cast) {
		this.cast = cast;
	}
	
	public void followTheLeader() {
		for(ComponentMap entity : cast.getEntitiesWithComponents(Body.class, Follower.class)) {
			Body body = entity.getAs(Body.class);
			Follower follower = entity.getAs(Follower.class);
			
			cast.getComponent(follower.getLeader(), Body.class).ifPresent(b -> {
				follower.follow(body, b);
			});
		}
	}
	
}
