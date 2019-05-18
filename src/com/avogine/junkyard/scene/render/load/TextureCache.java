package com.avogine.junkyard.scene.render.load;

import java.util.HashMap;
import java.util.Map;

import com.avogine.junkyard.scene.render.data.Texture;

public class TextureCache {

	private static Map<String, Texture> textures = new HashMap<>();
	
	// XXX If we clean up a texture that is referenced by 2 meshes will this fail?
	public static Texture getTexture(String textureName) {
		if(textures.containsKey(textureName)) {
			return textures.get(textureName);
		}
		
		Texture texture = TextureLoader.createTexture(textureName);
		textures.put(textureName, texture);
		return texture;
	}
	
}
