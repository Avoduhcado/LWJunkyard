package com.avogine.junkyard.scene.render.shaders.util;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;

public class UniformMatrix extends Uniform{
	
	public UniformMatrix(String name) {
		super(name);
	}
	
	public void loadMatrix(Matrix4f matrix){
		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer matrixBuffer = stack.mallocFloat(16);
			matrix.get(matrixBuffer);
			GL20.glUniformMatrix4fv(super.getLocation(), false, matrixBuffer);
		}
	}
	
}
