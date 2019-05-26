package com.avogine.junkyard.scene.render.shaders;

import com.avogine.junkyard.scene.render.shaders.util.ShaderProgram;
import com.avogine.junkyard.scene.render.shaders.util.UniformMatrix;
import com.avogine.junkyard.scene.render.shaders.util.UniformSampler;

public class SimpleShader extends ShaderProgram {

	public UniformSampler colorTexture = new UniformSampler("colorTexture");
	public UniformMatrix model = new UniformMatrix("model");
	public UniformMatrix view = new UniformMatrix("view");
	public UniformMatrix projection = new UniformMatrix("projection");
	
	public SimpleShader(String vertexFile, String fragmentFile, String...inVariables) {
		super(vertexFile, fragmentFile, inVariables);
		storeAllUniformLocations(colorTexture, model, view, projection);
		connectTextureUnits();
	}
	
	protected void connectTextureUnits() {
		super.start();
		colorTexture.loadTexUnit(0);
		super.stop();
	}

}
