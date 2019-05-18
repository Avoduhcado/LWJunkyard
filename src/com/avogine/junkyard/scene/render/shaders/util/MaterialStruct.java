package com.avogine.junkyard.scene.render.shaders.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.avogine.junkyard.scene.render.data.Material;

public class MaterialStruct extends UniformStruct {

	protected UniformVec4 ambient;
	protected UniformVec4 diffuse;
	protected UniformVec4 specular;
	protected UniformBoolean hasTexture;
	protected UniformFloat reflectance;

	public MaterialStruct(String name) {
		ambient = new UniformVec4(name + ".ambient");
		diffuse = new UniformVec4(name + ".diffuse");
		specular = new UniformVec4(name + ".specular");
		hasTexture = new UniformBoolean(name + ".hasTexture");
		reflectance = new UniformFloat(name + ".reflectance");
	}
	
	public void loadMaterial(Material material) {
		ambient.loadVec4(material.getAmbient());
		diffuse.loadVec4(material.getDiffuse());
		specular.loadVec4(material.getSpecular());
		hasTexture.loadBoolean(material.isTextured());
		reflectance.loadFloat(material.getReflectance());
	}
	
	@Override
	public List<Uniform> getAllUniforms() {
		return new ArrayList<>(Arrays.asList(ambient, diffuse, specular, hasTexture, reflectance));
	}

}
