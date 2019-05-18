package com.avogine.junkyard.scene.entity.render;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.joml.Vector3f;

import com.avogine.junkyard.scene.entity.Model;
import com.avogine.junkyard.scene.entity.event.EntityEvent;
import com.avogine.junkyard.scene.render.data.Material;
import com.avogine.junkyard.scene.render.data.Mesh;
import com.avogine.junkyard.scene.render.load.ModelInfo;
import com.avogine.junkyard.scene.render.load.TextureCache;
import com.avogine.junkyard.scene.terrain.HeightsGenerator;
import com.bulletphysics.collision.shapes.StridingMeshInterface;
import com.bulletphysics.collision.shapes.TriangleIndexVertexArray;

public class TerrainModel extends Model {

	private HeightsGenerator heightGen;
	
	// XXX
	private static final int TERRAIN_SIZE_X = 100;
	private static final int TERRAIN_SIZE_Z = 100;
	private static final float TILE_SIZE = 8f;
	private static final int SEED = 343;
	
	public TerrainModel(int entity, ModelInfo modelInfo) {
		super(entity, modelInfo);
	}

	@Override
	public void loadMeshes(ModelInfo modelInfo) {
		heightGen = new HeightsGenerator(modelInfo.getX(), modelInfo.getZ(), TERRAIN_SIZE_X, SEED);
		
		List<Float> positions = new ArrayList<>();
		List<Float> texCoords = new ArrayList<>();
		List<Float> normals = new ArrayList<>();
		List<Integer> indices = new ArrayList<>();

		for(int z = 0; z < TERRAIN_SIZE_Z; z++) {
			for(int x = 0 ; x < TERRAIN_SIZE_X; x++) {
				positions.add(x * TILE_SIZE);
				positions.add(heightGen.generateHeight(x, z));
				positions.add(z * TILE_SIZE);
				
				Vector3f normal = calculateNormal(x, z, heightGen);
				normals.add(normal.x);
				normals.add(normal.y);
				normals.add(normal.z);
				
				texCoords.add(x % 2 == 0 ? 0.0f : 1.0f);
				texCoords.add(z % 2 == 0 ? 0.0f : 1.0f);
			}
		}
		
		for(int i = 0; i < TERRAIN_SIZE_X * TERRAIN_SIZE_Z; i++) {
			if((i + 1) % TERRAIN_SIZE_X == 0 || i + TERRAIN_SIZE_Z >= TERRAIN_SIZE_X * TERRAIN_SIZE_Z) {
				continue;
			}
			indices.add(i);
			indices.add(i + TERRAIN_SIZE_X);
			indices.add(i + TERRAIN_SIZE_X + 1);
			indices.add(i + TERRAIN_SIZE_X + 1);
			indices.add(i + 1);
			indices.add(i);
		}
		
		Mesh terrainMesh = new Mesh(
				ArrayUtils.toPrimitive(positions.stream().toArray(Float[]::new)), 
				ArrayUtils.toPrimitive(texCoords.stream().toArray(Float[]::new)),
				ArrayUtils.toPrimitive(normals.stream().toArray(Float[]::new)), 
				indices.stream().mapToInt(Integer::intValue).toArray());
		terrainMesh.setMaterial(new Material(TextureCache.getTexture("grass.png")));
		
		meshes = new Mesh[1];
		meshes[0] = terrainMesh;
	}

	private Vector3f calculateNormal(int x, int y, HeightsGenerator generator) {
		float heightL = getHeight(x - 1, y, generator);
		float heightR = getHeight(x + 1, y, generator);
		float heightD = getHeight(x, y - 1, generator);
		float heightU = getHeight(x, y + 1, generator);
		Vector3f normal = new Vector3f(heightL - heightR, 2f, heightD - heightU);
		normal.normalize();
		return normal;
	}
	
	private float getHeight(int x, int z, HeightsGenerator generator) {
		return generator.generateHeight(x, z);
	}
	
	public StridingMeshInterface getMeshInterface() {
		int totalVerts = TERRAIN_SIZE_X * TERRAIN_SIZE_Z;
		int totalTriangles = 2 * (TERRAIN_SIZE_X - 1) * (TERRAIN_SIZE_Z - 1);

		int vertStride = 3 * 4;
		int indexStride = 3 * 4;
		
		ByteBuffer gVertices = ByteBuffer.allocateDirect(totalVerts * 3 * 4).order(ByteOrder.nativeOrder());
		ByteBuffer gIndices = ByteBuffer.allocateDirect(totalTriangles * 3 * 4).order(ByteOrder.nativeOrder());
		
		Vector3f tmp = new Vector3f();

		for (int i = 0; i < TERRAIN_SIZE_X; i++) {
			for (int j = 0; j < TERRAIN_SIZE_Z; j++) {
				tmp.set(
						i * TILE_SIZE,
						getHeight(i, j, heightGen),
						//waveheight * (float) Math.sin((float) i + offset) * (float) Math.cos((float) j + offset),
						j * TILE_SIZE);

				int index = i + j * TERRAIN_SIZE_X;
				gVertices.putFloat((index*3 + 0) * 4, tmp.x);
				gVertices.putFloat((index*3 + 1) * 4, tmp.y);
				gVertices.putFloat((index*3 + 2) * 4, tmp.z);
			}
		}
		
		gIndices.clear();
		for(int i = 0; i < TERRAIN_SIZE_X - 1; i++) {
			for(int j = 0; j < TERRAIN_SIZE_Z - 1; j++) {
				gIndices.putInt(j * TERRAIN_SIZE_X + i);
				gIndices.putInt(j * TERRAIN_SIZE_X + i + 1);
				gIndices.putInt((j + 1) * TERRAIN_SIZE_X + i + 1);

				gIndices.putInt(j * TERRAIN_SIZE_X + i);
				gIndices.putInt((j + 1) * TERRAIN_SIZE_X + i + 1);
				gIndices.putInt((j + 1) * TERRAIN_SIZE_X + i);
			}
		}
		gIndices.flip();
		
		return new TriangleIndexVertexArray(totalTriangles,
				gIndices,
				indexStride,
				totalVerts, gVertices, vertStride);
	}
	
	@Override
	public void prepare() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fireEvent(EntityEvent event) {
		// TODO Auto-generated method stub
		
	}

}
