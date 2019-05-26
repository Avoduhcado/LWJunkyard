package com.avogine.junkyard.scene.render.shaders;

import com.avogine.junkyard.scene.render.shaders.util.ShaderProgram;
import com.avogine.junkyard.scene.render.shaders.util.UniformMat4Array;
import com.avogine.junkyard.scene.render.shaders.util.UniformMatrix;
import com.avogine.junkyard.scene.render.shaders.util.UniformSampler;
import com.avogine.junkyard.scene.render.util.RenderConstants;

public class DepthShader extends ShaderProgram {

	public UniformMatrix modelViewProjectionMatrix = new UniformMatrix("modelViewProjection");
	public UniformMat4Array jointsMatrixArray = new UniformMat4Array("jointsMatrix", RenderConstants.MAX_JOINTS);
	public UniformSampler modelTexture = new UniformSampler("modelTexture");
	
	public DepthShader(String vertexFile, String fragmentFile, String...inVariables) {
		super(vertexFile, fragmentFile, inVariables);
		storeAllUniformLocations(modelViewProjectionMatrix, jointsMatrixArray, modelTexture);
		connectTextureUnits();
	}
	
	private void connectTextureUnits() {
		super.start();
		modelTexture.loadTexUnit(0);
		super.stop();
	}

}
