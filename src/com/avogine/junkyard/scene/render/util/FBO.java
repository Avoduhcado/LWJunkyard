package com.avogine.junkyard.scene.render.util;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

import com.avogine.junkyard.io.Window;
import com.avogine.junkyard.memory.MemoryManaged;

public class FBO implements MemoryManaged {

	private int width, height;
	private Window window;
	
	private int fboId;
	
	private int colorTexture;
	private int depthTexture;
	
	private int depthBuffer;
	
	private FBO(int id, int width, int height, Window window) {
		this.fboId = id;
		this.width = width;
		this.height = height;
		this.window = window;
	}
	
	public static FBO create(int width, int height, Window window) {
		int id = GL30.glGenFramebuffers();
		return new FBO(id, width, height, window);
	}
	
	// TODO Throw IllegalStateException?
	public void verifyCreate() {
		if(GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
			System.err.println("Framebuffer configuration error!");
		}
	}
	
	public void bindFramebuffer() {
		// Clear out any textures
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fboId);
		GL11.glViewport(0, 0, width, height);
	}
	
	public void unbindFramebuffer() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
	}
	
	public void unbindFramebufferAndSetViewport(int width, int height) {
		unbindFramebuffer();
		GL11.glViewport(0, 0, width, height);
	}
	
	public int createColorTextureAttachment() {
		colorTexture = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorTexture);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, colorTexture, 0);
		
		return colorTexture;
	}
	
	public int createDepthTextureAttachment() {
		depthTexture = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTexture);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_DEPTH_COMPONENT24, width, height, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (ByteBuffer) null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, depthTexture, 0);
		
		return depthTexture;
	}
	
	public int createDepthBufferAttachment() {
		depthBuffer = GL30.glGenRenderbuffers();
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, depthBuffer);
		GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL11.GL_DEPTH_COMPONENT, width, height);
		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, depthBuffer);
		
		return depthBuffer;
	}
	
	public void resolveToScreen() {
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, fboId);
		GL11.glDrawBuffer(GL11.GL_BACK);
		// TODO Use proper dimension values
		GL30.glBlitFramebuffer(0, 0, 1280, 720, 0, 0, 1280, 720, GL11.GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST);
		unbindFramebuffer();
	}

	public int getFboId() {
		return fboId;
	}
	
	public int getColorTexture() {
		return colorTexture;
	}
	
	public int getDepthTexture() {
		return depthTexture;
	}
	
	public int getDepthBuffer() {
		return depthBuffer;
	}
	
	@Override
	public void cleanUp() {
		GL30.glDeleteFramebuffers(fboId);
		GL11.glDeleteTextures(colorTexture);
		GL11.glDeleteTextures(depthTexture);
		GL30.glDeleteRenderbuffers(depthBuffer);
	}
	
}
