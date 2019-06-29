package com.avogine.junkyard.scene.render.data;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

import com.avogine.junkyard.memory.MemoryManaged;
import com.avogine.junkyard.scene.render.util.FBO;

public class ShadowMapCascade implements MemoryManaged {

	public static final int TEXTURE_SIZE = 4096;
	
	private FBO fbo;
	private int[] textureIds;
	
	public ShadowMapCascade(int numberOfCascades) {
		textureIds = new int[numberOfCascades];
		createTextures();
		
		setFbo(FBO.create(TEXTURE_SIZE, TEXTURE_SIZE));
		
		getFbo().bindFramebuffer();
		
		bindTexture(0);
		GL11.glDrawBuffer(GL11.GL_NONE);
		GL11.glReadBuffer(GL11.GL_NONE);
		
		getFbo().verifyCreate();
		getFbo().unbindFramebuffer();
	}
	
	private void createTextures() {
		GL11.glGenTextures(textureIds);
		for(int i = 0; i < textureIds.length; i++) {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureIds[i]);
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_DEPTH_COMPONENT24, TEXTURE_SIZE, TEXTURE_SIZE, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (ByteBuffer) null);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		}
	}

	public void prepare() {
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
	}
	
	public void bindTexture(int textureUnit) {
		GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, textureIds[textureUnit], 0);
	}
	
	public void bindTextures(int start) {
		for (int i = 0; i < textureIds.length; i++) {
			GL13.glActiveTexture(start + i);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureIds[i]);
		}
	}
	
	public FBO getFbo() {
		return fbo;
	}
	
	public void setFbo(FBO fbo) {
		this.fbo = fbo;
	}
	
	public int[] getTextureIds() {
		return textureIds;
	}
	
	@Override
	public void cleanUp() {
		fbo.cleanUp();
		GL11.glDeleteTextures(textureIds);
	}

}
