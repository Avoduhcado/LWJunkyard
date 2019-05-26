package com.avogine.junkyard.scene.render.shaders;

import com.avogine.junkyard.scene.render.shaders.util.ShaderProgram;
import com.avogine.junkyard.scene.render.shaders.util.UniformMatrix;
import com.avogine.junkyard.scene.render.shaders.util.UniformSampler;

public class SimpleOrthoShader extends ShaderProgram {

	public UniformSampler colorTexture = new UniformSampler("colorTexture");
	public UniformMatrix projModelMatrix = new UniformMatrix("projModelMatrix");
	
	//public UniformFloat nearPlane = new UniformFloat("nearPlane");
	//public UniformFloat farPlane = new UniformFloat("farPlane");
	
	public SimpleOrthoShader(String vertexFile, String fragmentFile, String...inVariables) {
		super(vertexFile, fragmentFile, inVariables);
		storeAllUniformLocations(colorTexture, projModelMatrix);
		connectTextureUnits();
		//loadPlanes();
	}

	protected void connectTextureUnits() {
		super.start();
		colorTexture.loadTexUnit(0);
		super.stop();
	}
	
//	protected void loadPlanes() {
//		super.start();
//		nearPlane.loadFloat(Window.NEAR_PLANE);
//		farPlane.loadFloat(Window.FAR_PLANE);
//		super.stop();
//	}
}
