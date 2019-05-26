package com.avogine.junkyard.scene.render.data;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import com.avogine.junkyard.memory.MemoryManaged;

public class Texture implements MemoryManaged {
	
	/**
	 * Stores the handle of the texture.
	 */
	protected final int id;

	/**
	 * Width of the texture.
	 */
	protected int width;
	/**
	 * Height of the texture.
	 */
	protected int height;

	/** Creates a texture. */
	public Texture() {
		id = GL11.glGenTextures();
	}

	/**
	 * Binds the texture.
	 */
	public void bindToUnit(int unit) {
		GL13.glActiveTexture(GL13.GL_TEXTURE0 + unit);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
	}

	/**
	 * Sets a parameter of the texture.
	 *
	 * @param name  Name of the parameter
	 * @param value Value to set
	 */
	public void setParameter(int name, int value) {
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, name, value);
	}
	
	public void setParameterf(int name, float value) {
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, name, value);
	}

	/**
	 * Uploads image data with specified width and height.
	 *
	 * @param width  Width of the image
	 * @param height Height of the image
	 * @param data   Pixel data of the image
	 */
	public void uploadData(int width, int height, ByteBuffer data) {
		uploadData(GL11.GL_RGBA8, width, height, GL11.GL_RGBA, data);
	}

	/**
	 * Uploads image data with specified internal format, width, height and
	 * image format.
	 *
	 * @param internalFormat Internal format of the image data
	 * @param width          Width of the image
	 * @param height         Height of the image
	 * @param format         Format of the image data
	 * @param data           Pixel data of the image
	 */
	public void uploadData(int internalFormat, int width, int height, int format, ByteBuffer data) {
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, internalFormat, width, height, 0, format, GL11.GL_UNSIGNED_BYTE, data);
	}

	/**
	 * Delete the texture.
	 */
	@Override
	public void cleanUp() {
		GL11.glDeleteTextures(id);
	}

	/**
	 * Get the texture ID
	 * 
	 * @return Texture ID
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Gets the texture width.
	 *
	 * @return Texture width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Sets the texture width.
	 *
	 * @param width The width to set
	 */
	public void setWidth(int width) {
		if (width > 0) {
			this.width = width;
		}
	}

	/**
	 * Gets the texture height.
	 *
	 * @return Texture height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Sets the texture height.
	 *
	 * @param height The height to set
	 */
	public void setHeight(int height) {
		if (height > 0) {
			this.height = height;
		}
	}

}
