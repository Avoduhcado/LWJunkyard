package com.avogine.junkyard.scene.entity.render;

import com.avogine.junkyard.scene.render.data.AnimatedFrame;

public interface Animatable {

	public void animate();
	public AnimatedFrame getCurrentFrame();

}
