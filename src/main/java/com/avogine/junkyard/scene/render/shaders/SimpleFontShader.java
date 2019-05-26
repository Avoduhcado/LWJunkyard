package com.avogine.junkyard.scene.render.shaders;

import com.avogine.junkyard.scene.render.shaders.util.ShaderProgram;
import com.avogine.junkyard.scene.render.shaders.util.UniformMatrix;
import com.avogine.junkyard.scene.render.shaders.util.UniformSampler;
import com.avogine.junkyard.scene.render.shaders.util.UniformVec4;

public class SimpleFontShader extends ShaderProgram {

	//public UniformSampler[] fontTextures = new UniformSampler[GL13.GL_MAX_TEXTURE_UNITS];
	public UniformSampler fontTexture = new UniformSampler("fontTexture");
	public UniformMatrix projModelMatrix = new UniformMatrix("projModelMatrix");
	public UniformVec4 color = new UniformVec4("color");
	
	public SimpleFontShader(String vertexFile, String fragmentFile, String...inVariables) {
		super(vertexFile, fragmentFile, inVariables);
		/*for(int i = 0; i < fontTextures.length; i++) {
			fontTextures[i] = new UniformSampler("fontTexture[" + i + "]");
		}*/
		storeAllUniformLocations(fontTexture, projModelMatrix, color);
		connectTextureUnits();
	}
	
	protected void connectTextureUnits() {
		super.start();
		fontTexture.loadTexUnit(0);
		/*for(int i = 0; i < fontTextures.length; i++) {
			fontTextures[i].loadTexUnit(i);
		}*/
		super.stop();
	}

}
