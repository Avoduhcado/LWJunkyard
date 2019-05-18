package com.avogine.junkyard.scene.render.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Font {

	private Map<Character, Glyph> glyphMap = new HashMap<>();
	private Texture texture;
	
	public Font(Map<Character, Glyph> glyphMap, List<Texture> textures) {
		this.glyphMap = glyphMap;
		// XXX Store all of the textures
		this.texture = textures.get(0);
	}
	
	public Glyph getGlyph(char character) {
		return glyphMap.get(character);
	}
	
	public Texture getTexture() {
		return texture;
	}
	
	public void printFont() {
		for(Character c : glyphMap.keySet()) {
			System.out.println("Char: " + c + "[" + glyphMap.get(c).toString() + "]");
		}
	}
	
}
