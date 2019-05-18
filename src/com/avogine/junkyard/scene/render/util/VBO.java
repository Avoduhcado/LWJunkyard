package com.avogine.junkyard.scene.render.util;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL15;

import com.avogine.junkyard.memory.MemoryManaged;

public class VBO implements MemoryManaged {
	
	private final int vboId;
	private final int type;
	
	private VBO(int vboId, int type) {
		this.vboId = vboId;
		this.type = type;
	}
	
	public static VBO create(int type) {
		int id = GL15.glGenBuffers();
		return new VBO(id, type);
	}
	
	public void bind() {
		GL15.glBindBuffer(type, vboId);
	}
	
	public void unbind() {
		GL15.glBindBuffer(type, 0);
	}
	
	public void storeData(IntBuffer data) {
		GL15.glBufferData(type, data, GL15.GL_STATIC_DRAW);
	}
	
	public void storeData(FloatBuffer data) {
		GL15.glBufferData(type, data, GL15.GL_STATIC_DRAW);
	}

	@Override
	public void cleanUp() {
		unbind();
		GL15.glDeleteBuffers(vboId);
	}

}
