package com.avogine.junkyard.scene.render.load;

public class ModelInfo {

	private String modelName;
	private int x, z;
	
	public ModelInfo(String modelName) {
		this.modelName = modelName;
	}
	
	public ModelInfo(int x, int z) {
		this.x = x;
		this.z = z;
	}
	
	public String getModelName() {
		return modelName;
	}
	
	public int getX() {
		return x;
	}
	
	public int getZ() {
		return z;
	}
	
}
