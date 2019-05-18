package com.avogine.junkyard.scene.text;

import java.awt.geom.Rectangle2D;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.joml.Vector3f;

import com.avogine.junkyard.scene.render.data.Font;
import com.avogine.junkyard.scene.render.data.Glyph;
import com.avogine.junkyard.scene.render.data.Material;
import com.avogine.junkyard.scene.render.data.Mesh;
import com.avogine.junkyard.scene.render.load.FontCache;

public class Text {

	private static final float ZPOS = 0.0f;

	private static final int VERTICES_PER_QUAD = 4;

	private Mesh mesh;
	private String text;
	
	private Rectangle2D boundingBox;

	// XXX
	public Vector3f position = new Vector3f(50, 50, 0);
	public Vector3f rotation = new Vector3f();
	public Vector3f scale = new Vector3f(1f);

	public Text(String text) {
		this.text = text;
		mesh = buildMesh(FontCache.getFont("Avocado"));
	}

	private Mesh buildMesh(Font font) {
		byte[] chars = text.getBytes(Charset.forName("ISO-8859-1"));
		int numChars = chars.length;

		List<Float> positions = new ArrayList<>();
		List<Float> textCoords = new ArrayList<>();
		float[] normals   = new float[0];
		List<Integer> indices   = new ArrayList<>();

		float textureWidth = font.getTexture().getWidth();
		float textureHeight = font.getTexture().getHeight();
		
		float textWidth = 0;
		
		for(int i = 0; i < numChars; i++) {
			Glyph currentGlyph = font.getGlyph((char) chars[i]);

			// Build a character tile composed by two triangles
			// We need to flip the y textCoords because we're flipping textures in the texture loader

			// Left Top vertex
			positions.add(textWidth + currentGlyph.getxOffset()); // x
			positions.add((float) currentGlyph.getyOffset()); //y
			positions.add(ZPOS); //z
			textCoords.add((float) currentGlyph.getX() / textureWidth);
			textCoords.add(1 - ((float) currentGlyph.getY() / textureHeight));
			indices.add(i * VERTICES_PER_QUAD);

			// Left Bottom vertex
			positions.add(textWidth + currentGlyph.getxOffset()); // x
			positions.add((float) currentGlyph.getHeight() + currentGlyph.getyOffset()); //y
			positions.add(ZPOS); //z
			textCoords.add((float) currentGlyph.getX() / textureWidth);
			textCoords.add(1 - ((float) (currentGlyph.getY() + currentGlyph.getHeight()) / textureHeight));
			indices.add(i * VERTICES_PER_QUAD + 1);

			// Right Bottom vertex
			positions.add(textWidth + (float) currentGlyph.getxAdvance()); // x
			positions.add((float) currentGlyph.getHeight() + currentGlyph.getyOffset()); //y
			positions.add(ZPOS); //z
			textCoords.add((float) (currentGlyph.getX() + currentGlyph.getWidth()) / textureWidth);
			textCoords.add(1 - ((float) (currentGlyph.getY() + currentGlyph.getHeight()) / textureHeight));
			indices.add(i * VERTICES_PER_QUAD + 2);

			// Right Top vertex
			positions.add(textWidth + (float) currentGlyph.getxAdvance()); // x
			positions.add((float) currentGlyph.getyOffset()); //y
			positions.add(ZPOS); //z
			textCoords.add((float) (currentGlyph.getX() + currentGlyph.getWidth()) / textureWidth);
			textCoords.add(1 - ((float) currentGlyph.getY() / textureHeight));
			indices.add(i * VERTICES_PER_QUAD + 3);

			// Add indices for left top and bottom right vertices
			indices.add(i * VERTICES_PER_QUAD);
			indices.add(i * VERTICES_PER_QUAD + 2);
			
			textWidth += currentGlyph.getxAdvance();
		}

		Mesh textMesh = new Mesh(ArrayUtils.toPrimitive(positions.stream().toArray(Float[]::new)), ArrayUtils.toPrimitive(textCoords.stream().toArray(Float[]::new)),
				normals, indices.stream().mapToInt(Integer::intValue).toArray());
		textMesh.setMaterial(new Material(font.getTexture()));

		setBoundingBox(new Rectangle2D.Float(positions.get(0), positions.get(1), positions.get(positions.size() - 6), positions.get(positions.size() - 5)));
		
		return textMesh;
	}

	public Mesh getMesh() {
		return mesh;
	}

	public String getText() {
		return text;
	}
	
	public Rectangle2D getBoundingBox() {
		return boundingBox;
	}
	
	protected void setBoundingBox(Rectangle2D boundingBox) {
		this.boundingBox = boundingBox;
	}

}
