package com.avogine.junkyard.scene.render.shaders;

import com.avogine.junkyard.scene.render.shaders.util.ShaderProgram;
import com.avogine.junkyard.scene.render.shaders.util.UniformMatrix;
import com.avogine.junkyard.scene.render.shaders.util.UniformSampler;

public class SkyboxShader extends ShaderProgram {

	public UniformMatrix projection = new UniformMatrix("projection");
	public UniformMatrix view = new UniformMatrix("view");
	public UniformSampler cubeMap = new UniformSampler("cubeMap");
	
	public SkyboxShader(String vertexFile, String fragmentFile, String...inVariables) {
		super(vertexFile, fragmentFile, inVariables);
		storeAllUniformLocations(projection, view, cubeMap);
		connectTextureUnits();
	}
	
	protected void connectTextureUnits() {
		super.start();
		cubeMap.loadTexUnit(0);
		super.stop();
	}

}
