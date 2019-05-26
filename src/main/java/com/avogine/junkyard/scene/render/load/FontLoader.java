package com.avogine.junkyard.scene.render.load;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.avogine.junkyard.scene.render.data.Font;
import com.avogine.junkyard.scene.render.data.Glyph;
import com.avogine.junkyard.scene.render.data.Texture;
import com.avogine.junkyard.util.ResourceConstants;

public class FontLoader {

	public static Font loadFont(String fontName) {
		Map<Character, Glyph> glyphMap = new HashMap<>();
		List<Texture> textures = new ArrayList<>();
		String fontFile = ResourceConstants.TEXTURE_PATH + fontName + ".fnt";
		
		try (InputStream is = FontLoader.class.getClassLoader().getResourceAsStream(fontFile)) {
			byte[] byteArray = new byte[is.available()];
			int offset = 0;
			String[] images = null;
			
			offset += is.read(byteArray, 0, 4);
			// BMFont specifications for the start of the file as read "BMF3"
			if(byteArray[0] == 66 && byteArray[1] == 77 && byteArray[2] == 70 && byteArray[3] == 3) {
				byte[] blockData = new byte[5];
				byte[] block = null;
				
				while(offset < byteArray.length) {
					offset += is.read(blockData);
					// Bitmask stuff if shit starts breaking, signed bytes and stuff, woo Java
					int length = ((blockData[1] & 0xff) << 0) | (blockData[2] << 8) | (blockData[3] << 16) | (blockData[4] << 24);
					
					switch(blockData[0]) {
					case 1: // Info
						block = new byte[length];
						offset += is.read(block);
						break;
					case 2: // Common
						block = new byte[length];
						offset += is.read(block);
						images = new String[(block[9] & 0xff) << 8 | (block[8] & 0xff)];
						break;
					case 3: // Pages
						block = new byte[length];
						offset += is.read(block);
						int pageIndex = 0;
						images[0] = "";
						
						for(byte bit : block) {
							if(bit == 0) {
								if(pageIndex < images.length - 1) {
									pageIndex++;
									images[pageIndex] = "";
								}
							} else {
								images[pageIndex] += (char) bit;
							}
						}
						
						// Loading textures for fonts
						for(String image : images) {
							textures.add(TextureCache.getTexture(image));
						}
						break;
					case 4: // Chars
						block = new byte[20];
						for(int c = 0; c < length / block.length; c++) {
							offset += is.read(block, 0, 20);
							
							char id = (char) ((block[3] & 0xff) << 24 | (block[2] & 0xff) << 16 | (block[1] & 0xff) << 8 | (block[0] & 0xff));
							int x = (block[5] & 0xff) << 8 | (block[4] & 0xff);
							int y = (block[7] & 0xff) << 8 | (block[6] & 0xff);
							int width = (block[9] & 0xff) << 8 | (block[8] & 0xff);
							int height = (block[11] & 0xff) << 8 | (block[10] & 0xff);
							int xOffset = block[13] << 8 | block[12];
							int yOffset = block[15] << 8 | block[14];
							int xAdvance = block[17] << 8 | block[16];
							String page = images[block[18] & 0xff];

							glyphMap.put(id, new Glyph(page, x, y, width, height, xOffset, yOffset, xAdvance));
						}
						break;
					case 5: // Kerning Pairs
						System.out.println("KERNING");
						block = new byte[10];
						for(int k = 0; k<length / block.length; k++) {
							offset += is.read(block);
						}
						break;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return new Font(glyphMap, textures);
	}
	
}
