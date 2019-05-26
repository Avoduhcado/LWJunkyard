package com.avogine.junkyard.scene.render.data;

import java.nio.ByteBuffer;

public class TextureData {

	private int width;
	private int height;
	
	private ByteBuffer data;
	
	public TextureData(int width, int height, ByteBuffer data) {
		this.width = width;
		this.height = height;
		this.data = data;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public ByteBuffer getData() {
		return data;
	}

	public void setData(ByteBuffer data) {
		this.data = data;
	}
	
}
