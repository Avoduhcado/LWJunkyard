package com.avogine.junkyard.scene;

import com.avogine.junkyard.memory.MemoryManaged;

public abstract class Scene implements MemoryManaged {
	
	/**
	 * Draw elements contained in the scene to the window.
	 */
	public abstract void render();
	
	/**
	 * Respond to the latest frame's inputs and update any relevant elements in the scene.
	 */
	public abstract void update();

}
