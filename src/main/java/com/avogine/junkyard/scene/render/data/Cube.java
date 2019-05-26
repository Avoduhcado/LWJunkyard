package com.avogine.junkyard.scene.render.data;

import org.lwjgl.opengl.GL11;

import com.avogine.junkyard.memory.MemoryManaged;
import com.avogine.junkyard.scene.entity.Renderable;
import com.avogine.junkyard.scene.render.util.RenderConstants;
import com.avogine.junkyard.scene.render.util.VAO;

public class Cube implements Renderable, MemoryManaged {

	private VAO vao;
	private int vertexCount;
	private Texture texture;
	
	public Cube() {
		setVao(VAO.create());
		getVao().bind();
		
		getVao().createAttribute(0, RenderConstants.CUBE_VERTICES, 3);
		vertexCount = RenderConstants.CUBE_VERTICES.length / 3;
		
		getVao().unbind();
	}
	
	@Override
	public void render() {
		getVao().bind(0);
		texture.bindToUnit(0);

		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vertexCount);
		
		getVao().unbind(0);
	}
	
	protected VAO getVao() {
		return vao;
	}
	
	protected void setVao(VAO vao) {
		this.vao = vao;
	}
	
	public Texture getTexture() {
		return texture;
	}
	
	public void setTexture(Texture texture) {
		this.texture = texture;
	}
	
	@Override
	public void cleanUp() {
		vao.cleanUp();
		texture.cleanUp();
	}

}
