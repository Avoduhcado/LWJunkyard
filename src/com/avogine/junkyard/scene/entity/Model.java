package com.avogine.junkyard.scene.entity;

import org.joml.Vector3f;

import com.avogine.junkyard.scene.render.data.Mesh;
import com.avogine.junkyard.scene.render.load.ModelInfo;

public abstract class Model extends EntityComponent {

	protected Vector3f rotation = new Vector3f();
	protected Vector3f scale = new Vector3f(1, 1, 1);
	
	protected Mesh[] meshes;
	
	public Model(int entity, ModelInfo modelInfo) {
		super(entity);
		loadMeshes(modelInfo);
	}
	
	public abstract void loadMeshes(ModelInfo modelInfo);
	
	public abstract void prepare();
	
	public Mesh[] getMeshes() {
		return meshes;
	}
	
	public Vector3f getRotation() {
		return rotation;
	}
	
	public void setRotation(Vector3f rotation) {
		this.rotation = rotation;
	}
	
	public Vector3f getScale() {
		return scale;
	}
	
	public void setScale(Vector3f scale) {
		this.scale = scale;
	}
	
}
