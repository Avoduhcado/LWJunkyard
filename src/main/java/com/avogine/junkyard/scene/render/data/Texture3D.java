package com.avogine.junkyard.scene.render.data;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class Texture3D extends Texture {

	@Override
	public void bindToUnit(int unit) {
		GL13.glActiveTexture(GL13.GL_TEXTURE0 + unit);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, id);
	}
	
	@Override
	public void setParameter(int name, int value) {
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, name, value);
	}
	
	@Override
	public void setParameterf(int name, float value) {
		GL11.glTexParameterf(GL13.GL_TEXTURE_CUBE_MAP, name, value);
	}
	
	public void uploadData(int target, int internalFormat, int width, int height, int format, ByteBuffer data) {
		GL11.glTexImage2D(target, 0, internalFormat, width, height, 0, format, GL11.GL_UNSIGNED_BYTE, data);
	}
	
}
