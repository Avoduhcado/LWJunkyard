package com.avogine.junkyard.scene.render.data;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import com.avogine.junkyard.memory.MemoryManaged;
import com.avogine.junkyard.scene.entity.Renderable;
import com.avogine.junkyard.scene.render.util.VAO;

public class RawMesh implements Renderable, MemoryManaged {

	private VAO vao;
	private int[] vaoAttributeList;
	private Material material;
	private int texture;
	
	public RawMesh(VAO vao, int vaoAttributeSize, Material material) {
		this.vao = vao;
		vaoAttributeList = new int[vaoAttributeSize];
		for(int i = 0; i < vaoAttributeSize; i++) {
			vaoAttributeList[i] = i;
		}
		this.material = material;
	}
	
	public RawMesh(VAO vao, int vaoAttributeSize, int texture) {
		this.vao = vao;
		vaoAttributeList = new int[vaoAttributeSize];
		for(int i = 0; i < vaoAttributeSize; i++) {
			vaoAttributeList[i] = i;
		}
		this.texture = texture;
	}
	
	protected void initRender() {
		// Bind main texture
		if(material != null) {
			material.bindTextures();
		} else if(texture != 0) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
		}
		// Bind VAO and attributes
		getVao().bind(vaoAttributeList);
	}

	@Override
	public void render() {
		initRender();
		
		GL11.glDrawElements(GL11.GL_TRIANGLES, getVao().getIndexCount(), GL11.GL_UNSIGNED_INT, 0);
		
		endRender();
	}
	
	protected void endRender() {
		// Restore state
		getVao().unbind(vaoAttributeList);
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}
	
	public void setVao(VAO vao) {
		this.vao = vao;
	}
	
	public VAO getVao() {
		return vao;
	}
	
	public Material getMaterial() {
		return material;
	}
	
	public void setMaterial(Material material) {
		this.material = material;
	}
	
	@Override
	public void cleanUp() {
		vao.cleanUp();
		material.cleanUp();
	}
	
}
