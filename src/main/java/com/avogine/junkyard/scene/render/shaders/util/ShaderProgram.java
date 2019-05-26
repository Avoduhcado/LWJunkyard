package com.avogine.junkyard.scene.render.shaders.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import com.avogine.junkyard.memory.MemoryManaged;
import com.avogine.junkyard.util.ResourceConstants;
import com.avogine.junkyard.util.ResourceUtil;

public abstract class ShaderProgram implements MemoryManaged {

	private int programID;

	public ShaderProgram(String vertexFile, String fragmentFile, String... inVariables) {
		int vertexShaderID = loadShader(vertexFile, GL20.GL_VERTEX_SHADER);
		int fragmentShaderID = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER);
		programID = GL20.glCreateProgram();
		GL20.glAttachShader(programID, vertexShaderID);
		GL20.glAttachShader(programID, fragmentShaderID);
		bindAttributes(inVariables);
		GL20.glLinkProgram(programID);
		GL20.glDetachShader(programID, vertexShaderID);
		GL20.glDetachShader(programID, fragmentShaderID);
		GL20.glDeleteShader(vertexShaderID);
		GL20.glDeleteShader(fragmentShaderID);
	}
	
	protected void storeAllUniformLocations(Uniform... uniforms){
		for(Uniform uniform : uniforms) {
			uniform.storeUniformLocation(programID);
		}
		GL20.glValidateProgram(programID);
	}

	public void start() {
		GL20.glUseProgram(programID);
	}

	public void stop() {
		GL20.glUseProgram(0);
	}

	@Override
	public void cleanUp() {
		stop();
		GL20.glDeleteProgram(programID);
	}

	private void bindAttributes(String[] inVariables){
		for(int i = 0; i < inVariables.length; i++) {
			GL20.glBindAttribLocation(programID, i, inVariables[i]);
		}
	}
	
	protected static int loadShader(String file, int type) {
		StringBuilder shaderSource = new StringBuilder();
		String shaderFile = ResourceConstants.SHADER_PATH + file;

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(ResourceUtil.getInputStreamForResource(shaderFile)))) {
			String line;
			while((line = reader.readLine()) != null) {
				shaderSource.append(line).append("\n");
			}
		} catch (IOException | NullPointerException e) {
			System.err.println("Could not read file! " + shaderFile);
			e.printStackTrace();
			System.exit(1);
		}
		
		int shaderId = GL20.glCreateShader(type);
		GL20.glShaderSource(shaderId, shaderSource);
		GL20.glCompileShader(shaderId);
		if(GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			System.out.println("Shader: " + file);
			System.out.println(GL20.glGetShaderInfoLog(shaderId, 500));
			System.err.println("Could not compile shader.");
			System.exit(-1);
		}
		
		return shaderId;
	}
	
}
