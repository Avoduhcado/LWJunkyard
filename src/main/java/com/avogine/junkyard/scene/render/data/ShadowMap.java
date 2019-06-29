package com.avogine.junkyard.scene.render.data;

import org.lwjgl.opengl.GL11;

import com.avogine.junkyard.memory.MemoryManaged;
import com.avogine.junkyard.scene.render.util.FBO;

public class ShadowMap implements MemoryManaged {

	public static final int TEXTURE_SIZE = 4096;
	
	private FBO fbo;
	
	public ShadowMap() {
		setFbo(FBO.create(TEXTURE_SIZE, TEXTURE_SIZE));
		
		getFbo().bindFramebuffer();
		
		getFbo().createDepthTextureAttachment();
		GL11.glDrawBuffer(GL11.GL_NONE);
		
		getFbo().verifyCreate();
		getFbo().unbindFramebuffer();
	}
	
	public void prepare() {
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
	}
	
	public FBO getFbo() {
		return fbo;
	}
	
	public void setFbo(FBO fbo) {
		this.fbo = fbo;
	}
	
	public int getShadowTexture() {
		return fbo.getDepthTexture();
	}
	
	@Override
	public void cleanUp() {
		fbo.cleanUp();
	}
	
}
