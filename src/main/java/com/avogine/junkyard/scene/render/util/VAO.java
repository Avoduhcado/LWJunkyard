package com.avogine.junkyard.scene.render.util;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import com.avogine.junkyard.memory.MemoryManaged;

public class VAO implements MemoryManaged {
	
	private static final int BYTES_PER_FLOAT = 4;
	private static final int BYTES_PER_INT = 4;
	public final int id;
	private List<VBO> dataVbos = new ArrayList<VBO>();
	private VBO indexVbo;
	private int indexCount;

	public static VAO create() {
		int id = GL30.glGenVertexArrays();
		return new VAO(id);
	}

	private VAO(int id) {
		this.id = id;
	}
	
	public int getIndexCount() {
		return indexCount;
	}
	
	public void setIndexCount(int indexCount) {
		this.indexCount = indexCount;
	}

	public void bind(int... attributes) {
		bind();
		for (int i : attributes) {
			GL20.glEnableVertexAttribArray(i);
		}
	}

	public void unbind(int... attributes) {
		for (int i : attributes) {
			GL20.glDisableVertexAttribArray(i);
		}
		unbind();
	}
	
	public void createIndexBuffer(int[] indices) {
		IntBuffer indexBuffer = null;
		try {
			this.indexVbo = VBO.create(GL15.GL_ELEMENT_ARRAY_BUFFER);
			this.indexCount = indices.length;
			
			indexBuffer = MemoryUtil.memAllocInt(indices.length);
			indexBuffer.put(indices).flip();

			indexVbo.bind();
			indexVbo.storeData(indexBuffer);		
		} finally {
			if(indexBuffer != null) {
				MemoryUtil.memFree(indexBuffer);
			}
		}
	}

	public void createAttribute(int attribute, float[] data, int attrSize) {
		FloatBuffer floatBuffer = null;
		try {
			VBO dataVbo = VBO.create(GL15.GL_ARRAY_BUFFER);
			dataVbos.add(dataVbo);

			floatBuffer = MemoryUtil.memAllocFloat(data.length);
			floatBuffer.put(data).flip();

			dataVbo.bind();
			dataVbo.storeData(floatBuffer);
			GL20.glVertexAttribPointer(attribute, attrSize, GL11.GL_FLOAT, false, attrSize * BYTES_PER_FLOAT, 0);
			dataVbo.unbind();
		} finally {
			if(floatBuffer != null) {
				MemoryUtil.memFree(floatBuffer);
			}
		}
	}
	
	public void createIntAttribute(int attribute, int[] data, int attrSize) {
		IntBuffer intBuffer = null;
		try {
			VBO dataVbo = VBO.create(GL15.GL_ARRAY_BUFFER);
			dataVbos.add(dataVbo);
			
			intBuffer = MemoryUtil.memAllocInt(data.length);
			intBuffer.put(data).flip();

			dataVbo.bind();
			dataVbo.storeData(intBuffer);
			GL30.glVertexAttribIPointer(attribute, attrSize, GL11.GL_INT, attrSize * BYTES_PER_INT, 0);
			dataVbo.unbind();
		} finally {
			if(intBuffer != null) {
				MemoryUtil.memFree(intBuffer);
			}
		}
	}
	
	@Override
	public void cleanUp() {
		for(VBO vbo : dataVbos){
			vbo.cleanUp();
		}
		indexVbo.cleanUp();
		
		unbind();
		GL30.glDeleteVertexArrays(id);
	}

	private void bind() {
		GL30.glBindVertexArray(id);
	}

	private void unbind() {
		GL30.glBindVertexArray(0);
	}

}
