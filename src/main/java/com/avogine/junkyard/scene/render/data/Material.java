package com.avogine.junkyard.scene.render.data;

import org.joml.Vector4f;

import com.avogine.junkyard.memory.MemoryManaged;

// TODO Make this abstract and have custom material implementations
public class Material implements MemoryManaged {

	public static final Vector4f DEFAULT_COLOR = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f); 
	
	private Vector4f ambient;
	private Vector4f diffuse;
	private Vector4f specular;

	private float shininess;
	private float reflectance;

	private Texture texture;
	private Texture normalMap;

	public Material() {
		ambient = DEFAULT_COLOR;
		diffuse = DEFAULT_COLOR;
		specular = DEFAULT_COLOR;
		texture = null;
		reflectance = 0f;
	}
	
	public Material(Vector4f color, float reflectance) {
		this(color, color, color, null, reflectance);
	}

	public Material(Texture texture) {
		this(DEFAULT_COLOR, DEFAULT_COLOR, DEFAULT_COLOR, texture, 0);
	}

	public Material(Texture texture, float reflectance) {
		this(DEFAULT_COLOR, DEFAULT_COLOR, DEFAULT_COLOR, texture, reflectance);
	}

	public Material(Vector4f ambientColor, Vector4f diffuseColor, Vector4f specularColor, float reflectance) {
		this(ambientColor, diffuseColor, specularColor, null, reflectance);
	}

	public Material(Vector4f ambient, Vector4f diffuse, Vector4f specular, Texture texture, float reflectance) {
		this.ambient = ambient;
		this.diffuse = diffuse;
		this.specular = specular;
		this.texture = texture;
		this.reflectance = reflectance;
	}

	public void bindTextures() {
		if(isTextured()) {
			texture.bindToUnit(0);
		}
		if(isNormalMapped()) {
			normalMap.bindToUnit(1);
		}
	}
	
	@Override
	public void cleanUp() {
		if(isTextured()) {
			texture.cleanUp();
		}
		if(isNormalMapped()) {
			normalMap.cleanUp();
		}
	}
	
	public Vector4f getAmbient() {
		return ambient;
	}
	
	public void setAmbient(Vector4f ambient) {
		this.ambient = ambient;
	}
	
	public Vector4f getDiffuse() {
		return diffuse;
	}
	
	public void setDiffuse(Vector4f diffuse) {
		this.diffuse = diffuse;
	}
	
	public Vector4f getSpecular() {
		return specular;
	}
	
	public void setSpecular(Vector4f specular) {
		this.specular = specular;
	}
	
	public float getShininess() {
		return shininess;
	}
	
	public void setShininess(float shininess) {
		this.shininess = shininess;
	}
	
	public float getReflectance() {
		return reflectance;
	}
	
	public void setReflectance(float reflectance) {
		this.reflectance = reflectance;
	}
	
	public boolean isTextured() {
		return this.texture != null;
	}
	
	public Texture getTexture() {
		return texture;
	}
	
	public void setTexture(Texture texture) {
		this.texture = texture;
	}
	
	public boolean isNormalMapped() {
		return this.normalMap != null;
	}
	
	public Texture getNormalMap() {
		return normalMap;
	}
	
	public void setNormalMap(Texture normalMap) {
		this.normalMap = normalMap;
	}
	
}
