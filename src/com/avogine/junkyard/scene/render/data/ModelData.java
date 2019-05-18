package com.avogine.junkyard.scene.render.data;

import java.util.HashMap;
import java.util.Map;

public class ModelData {

	private Mesh[] meshes;
	private Map<String, AnimationData> animations = new HashMap<>();
	
	public ModelData(Mesh[] meshes) {
		this.meshes = meshes;
	}
	
	public ModelData(Mesh[] meshes, Map<String, AnimationData> animations) {
		this(meshes);
		this.animations = animations;
	}
	
	public Mesh[] getMeshes() {
		return meshes;
	}
	
	public Map<String, AnimationData> getAnimations() {
		return animations;
	}
	
}
