package com.avogine.junkyard.scene.render.shaders;

import com.avogine.junkyard.scene.render.shaders.util.ShaderProgram;
import com.avogine.junkyard.scene.render.shaders.util.UniformMatrix;

public class ColorShader extends ShaderProgram {

	//public UniformMatrix model = new UniformMatrix("model");
	public UniformMatrix view = new UniformMatrix("view");
	public UniformMatrix projection = new UniformMatrix("projection");
	
	public ColorShader(String vertexFile, String fragmentFile, String...inVariables) {
		super(vertexFile, fragmentFile, inVariables);
		storeAllUniformLocations(view, projection);
	}

}
