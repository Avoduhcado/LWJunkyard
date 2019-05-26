package com.avogine.junkyard.scene.render.load;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import com.avogine.junkyard.scene.render.data.Texture;
import com.avogine.junkyard.scene.render.data.Texture3D;
import com.avogine.junkyard.scene.render.data.TextureData;
import com.avogine.junkyard.util.IOUtil;
import com.avogine.junkyard.util.ResourceConstants;

public class TextureLoader {

	private static final String DEFAULT_TEXTURE = "AGDG Logo.png";
	
	/**
	 * Creates a texture with specified width, height and data.
	 *
	 * @param textureData Image width, height, and pixel data in RGBA format
	 *
	 * @return Texture from the specified data
	 */
	public static Texture createTexture(String textureName) {
		TextureData textureData = loadTexture(textureName, true);
		
		Texture texture = new Texture();
		texture.setWidth(textureData.getWidth());
		texture.setHeight(textureData.getHeight());

		texture.bindToUnit(0);

		texture.setParameter(GL11.GL_TEXTURE_WRAP_S, GL13.GL_CLAMP_TO_EDGE);
		texture.setParameter(GL11.GL_TEXTURE_WRAP_T, GL13.GL_CLAMP_TO_EDGE);
		texture.setParameter(GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		texture.setParameter(GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

		texture.uploadData(GL11.GL_RGBA8, textureData.getWidth(), textureData.getHeight(), GL11.GL_RGBA, textureData.getData());

		GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
		texture.setParameter(GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
		texture.setParameterf(GL14.GL_TEXTURE_LOD_BIAS, -4f);
		if(GL.getCapabilities().GL_EXT_texture_filter_anisotropic) {
			// TODO: Load this from an options menu/file
			// Will probably require a restart to actually create all the textures again?
			float amount = Math.min(4f, GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
			texture.setParameterf(EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, amount);
		} else {
			System.out.println("Anisotropic filtering not supported.");
		}

		return texture;
	}
	
	public static Texture createTexture(int width, int height, int pixelFormat) {
		Texture texture = new Texture();
		texture.setWidth(width);
		texture.setHeight(height);
		
		texture.bindToUnit(0);
		texture.uploadData(GL14.GL_DEPTH_COMPONENT32, width, height, pixelFormat, (ByteBuffer) null);
		
		texture.setParameter(GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		texture.setParameter(GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		texture.setParameter(GL11.GL_TEXTURE_WRAP_S, GL13.GL_CLAMP_TO_EDGE);
		texture.setParameter(GL11.GL_TEXTURE_WRAP_T, GL13.GL_CLAMP_TO_EDGE);
		
		return texture;
	}
	
	/**
	 * Load a cubemap from array of files.
	 * </br>
	 * Files must be ordered in position from RIGHT, LEFT, TOP, BOTTOM, BACK, FRONT.
	 * 
	 * @param textureFiles The list of images to load into a cube map
	 * @return 
	 */
	public static Texture3D createCubeMap(String[] textureFiles) {
		Texture3D texture = new Texture3D();
		texture.bindToUnit(0);
		
		// Right, Left, Top, Bottom, Back, Front
		for (int i = 0; i < textureFiles.length; i++) {
			//TextureData textureData = loadTexture(textureFiles[i], (i == 2 || i == 3));
			TextureData textureData = loadTexture(textureFiles[i], false);
			texture.uploadData(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, GL11.GL_RGBA, textureData.getWidth(), textureData.getHeight(), GL11.GL_RGBA, textureData.getData());
		}
		
		texture.setParameter(GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		texture.setParameter(GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		texture.setParameter(GL11.GL_TEXTURE_WRAP_S, GL13.GL_CLAMP_TO_EDGE);
		texture.setParameter(GL11.GL_TEXTURE_WRAP_T, GL13.GL_CLAMP_TO_EDGE);
		
		return texture;
	}
	
	/**
	 * Load texture from file.
	 *
	 * @param path File path of the texture
	 * @param flip Flip the texture vertically on load
	 *
	 * @return Texture from specified file
	 */
	public static TextureData loadTexture(String textureName, boolean flip) {
		ByteBuffer image;
		int width, height;
		try (MemoryStack stack = MemoryStack.stackPush()) {
			/* Prepare image buffers */
			IntBuffer w = stack.mallocInt(1);
			IntBuffer h = stack.mallocInt(1);
			IntBuffer comp = stack.mallocInt(1);

			String imagePath = ResourceConstants.TEXTURE_PATH + textureName;
			ByteBuffer imageBuffer = null;
			try {
				imageBuffer = IOUtil.ioResourceToByteBuffer(imagePath, 8 * 1024);
			} catch (FileNotFoundException e) {
				if(textureName.equals(DEFAULT_TEXTURE)) {
					throw new RuntimeException("Default texture failed to load, something critical is broken.");
				}
				return loadTexture(DEFAULT_TEXTURE, true);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

			/*if (!STBImage.stbi_info_from_memory(imageBuffer, w, h, comp)) {
                throw new RuntimeException("Failed to read image information: " + STBImage.stbi_failure_reason());
            } else {
                System.out.println("OK with reason: " + STBImage.stbi_failure_reason());
            }*/

			/* Load image */
			STBImage.stbi_set_flip_vertically_on_load(flip);
			image = STBImage.stbi_load_from_memory(imageBuffer, w, h, comp, 4);
			if (image == null) {
				throw new RuntimeException("Failed to load texture file: " + imagePath
						+ System.lineSeparator() + STBImage.stbi_failure_reason());
			}

			/* Get width and height of image */
			width = w.get();
			height = h.get();
		}

		return new TextureData(width, height, image);
	}

}
