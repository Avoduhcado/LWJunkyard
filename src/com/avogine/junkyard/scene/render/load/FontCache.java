package com.avogine.junkyard.scene.render.load;

import java.util.HashMap;
import java.util.Map;

import com.avogine.junkyard.scene.render.data.Font;

public class FontCache {

	private static Map<String, Font> fonts = new HashMap<>();
	
	public static Font getFont(String fontName) {
		if(fonts.containsKey(fontName)) {
			return fonts.get(fontName);
		}
		
		Font font = null;
		try {
			font = FontLoader.loadFont(fontName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		fonts.put(fontName, font);
		return font;
	}
	
}
