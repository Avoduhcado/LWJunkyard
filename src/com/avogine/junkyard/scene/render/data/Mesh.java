package com.avogine.junkyard.scene.render.data;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.lwjgl.opengl.GL11;

import com.avogine.junkyard.memory.MemoryManaged;
import com.avogine.junkyard.scene.entity.Renderable;
import com.avogine.junkyard.scene.render.util.VAO;

public class Mesh implements Renderable, MemoryManaged {

	public static final int MAX_WEIGHTS = 4;
	
	protected VAO vao;
	protected Material material;
	
	protected float boundingRadius;

	public Mesh(float[] positions, float[] textureCoords, float[] normals, int[] indices) {
		this(positions, textureCoords, normals, createEmptyFloatArray(MAX_WEIGHTS * positions.length / 3, 0), createEmptyIntArray(MAX_WEIGHTS * positions.length / 3, 0), indices);
	}

	/**
	 * Instantiate a Mesh with values supplied for position, textureCoordinates, normals, weights, and jointIndices.
	 * @param positions
	 * @param textureCoords
	 * @param normals
	 * @param weights
	 * @param jointIndices
	 * @param indices
	 */
	public Mesh(float[] positions, float[] textureCoords, float[] normals, float[] weights, int[] jointIndices, int[] indices) {
		calculateBoundingRadius(positions);
		
		setVao(VAO.create());
		getVao().bind();
		
		getVao().createAttribute(0, positions, 3);
		getVao().createAttribute(1, textureCoords, 2);
		getVao().createAttribute(2, normals, 3);
		getVao().createAttribute(3, weights, 4);
		getVao().createIntAttribute(4, jointIndices, 4);
		getVao().createIndexBuffer(indices);

		getVao().unbind();
	}
	
	public void initRender() {
		// Bind main texture
		if(material != null) {
			material.bindTextures();
		}
		// Bind VAO and attributes
		getVao().bind(0, 1, 2, 3, 4);
	}
	
	@Override
	public void render() {
		initRender();

		GL11.glDrawElements(GL11.GL_TRIANGLES, getVao().getIndexCount(), GL11.GL_UNSIGNED_INT, 0);

		endRender();
	}
	
	public void renderList(List<ModelData> modelList, Consumer<ModelData> consumer) {
		initRender();
		
		for(ModelData model : modelList) {
			// Set up data required by Model
			consumer.accept(model);
			// Render this game item
			GL11.glDrawElements(GL11.GL_TRIANGLES, getVao().getIndexCount(), GL11.GL_UNSIGNED_INT, 0);
		}
		
		endRender();
	}

	public void endRender() {
		// Restore state
		getVao().unbind(0, 1, 2, 3, 4);
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}
	
	@Override
	public void cleanUp() {
		vao.cleanUp();
	}

	protected void calculateBoundingRadius(float positions[]) {
		int length = positions.length;
		boundingRadius = 0;
		for(int i = 0; i < length; i++) {
			float pos = positions[i];
			boundingRadius = Math.max(Math.abs(pos), boundingRadius);
		}
	}

	protected VAO getVao() {
		return vao;
	}
	
	protected void setVao(VAO vao) {
		this.vao = vao;
	}
	
	public Material getMaterial() {
		return material;
	}
	
	public void setMaterial(Material material) {
		this.material = material;
	}

	public float getBoundingRadius() {
		return boundingRadius;
	}
	
	public void setBoundingRadius(float boundingRadius) {
		this.boundingRadius = boundingRadius;
	}
	
	protected static float[] createEmptyFloatArray(int length, float defaultValue) {
		float[] result = new float[length];
		Arrays.fill(result, defaultValue);
		return result;
	}

	protected static int[] createEmptyIntArray(int length, int defaultValue) {
		int[] result = new int[length];
		Arrays.fill(result, defaultValue);
		return result;
	}
}
