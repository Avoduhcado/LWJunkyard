package com.avogine.junkyard.scene.render.data;

import java.util.Arrays;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import com.avogine.junkyard.memory.MemoryManaged;
import com.avogine.junkyard.scene.entity.Renderable;
import com.avogine.junkyard.scene.render.util.VAO;

public class Line implements Renderable, MemoryManaged {

	protected VAO vao;
	
	public Line(Vector3f start, Vector3f end, Vector3f color) {
		setVao(VAO.create());
		getVao().bind();
		
		float[] positions = new float[6];
		positions[0] = start.x;
		positions[1] = start.y;
		positions[2] = start.z;
		positions[3] = end.x;
		positions[4] = end.y;
		positions[5] = end.z;
		
		float[] colors = new float[6];
		colors[0] = color.x;
		colors[1] = color.y;
		colors[2] = color.z;
		colors[3] = color.x;
		colors[4] = color.y;
		colors[5] = color.z;
		
		int[] indices = new int[2];
		indices[0] = 0;
		indices[1] = 1;
		
		getVao().createAttribute(0, positions, 3);
		getVao().createAttribute(1, colors, 3);
		//getVao().createAttribute(2, createEmptyFloatArray(6, 0), 3);
		getVao().createIndexBuffer(indices);
		
		getVao().unbind();
	}
	
	@Override
	public void render() {
		getVao().bind(0, 1);

		GL11.glDrawArrays(GL11.GL_LINES, 0, 2);
		
		getVao().unbind(0, 1);
	}

	@Override
	public void cleanUp() {
		vao.cleanUp();
	}
	
	protected VAO getVao() {
		return vao;
	}
	
	protected void setVao(VAO vao) {
		this.vao = vao;
	}

	protected static float[] createEmptyFloatArray(int length, float defaultValue) {
		float[] result = new float[length];
		Arrays.fill(result, defaultValue);
		return result;
	}
	
}
