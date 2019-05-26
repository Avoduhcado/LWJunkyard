package com.avogine.junkyard.scene.render.data;

public class Glyph {

	private String page;
	private int x, y;
	private int width, height;
	private int xOffset, yOffset;
	private int xAdvance;
	
	public Glyph(String page, int x, int y, int width, int height, int xOffset, int yOffset, int xAdvance) {
		this.page = page;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.xAdvance = xAdvance;
	}
	
	public String getPage() {
		return page;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getxOffset() {
		return xOffset;
	}
	
	public int getyOffset() {
		return yOffset;
	}
	
	public int getxAdvance() {
		return xAdvance;
	}
	
	/**
	 * Print info about the glyph in the format of:
	 * </br>
	 * Page, X, Y, Width, Height, Xoffset, Yoffset, Xadvance
	 */
	@Override
	public String toString() {
		return page + ", " + x + ", " + y + ", " + width + ", " + height + ", " + xOffset + ", " + yOffset + ", " + xAdvance;
	}
	
}
