package com.avogine.junkyard.scene.entity.render;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.joml.Vector3f;
import org.ode4j.ode.DTriMeshData;
import org.ode4j.ode.OdeHelper;

import com.avogine.junkyard.scene.entity.Model;
import com.avogine.junkyard.scene.entity.event.EntityEvent;
import com.avogine.junkyard.scene.render.data.Material;
import com.avogine.junkyard.scene.render.data.Mesh;
import com.avogine.junkyard.scene.render.load.ModelInfo;
import com.avogine.junkyard.scene.render.load.TextureCache;
import com.avogine.junkyard.scene.terrain.HeightsGenerator;

public class TerrainModel extends Model {

	private HeightsGenerator heightGen;
	
	// XXX
	private static final int TERRAIN_SIZE_X = 100;
	private static final int TERRAIN_SIZE_Z = 100;
	private static final float TILE_SIZE = 8f;
	private static final int SEED = 343;
	
	private float[] vertices;
	private int[] meshIndices;
	
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
		
		vertices = ArrayUtils.toPrimitive(positions.stream().toArray(Float[]::new));
		meshIndices = indices.stream().mapToInt(Integer::intValue).toArray();
		
		Mesh terrainMesh = new Mesh(
				vertices, 
				ArrayUtils.toPrimitive(texCoords.stream().toArray(Float[]::new)),
				ArrayUtils.toPrimitive(normals.stream().toArray(Float[]::new)), 
				meshIndices);
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
	
	public DTriMeshData getTriMeshData() {
		DTriMeshData triMeshData = OdeHelper.createTriMeshData();
		
		triMeshData.build(vertices, meshIndices);

		return triMeshData;
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
