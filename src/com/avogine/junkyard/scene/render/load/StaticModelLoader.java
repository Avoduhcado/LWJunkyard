package com.avogine.junkyard.scene.render.load;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.joml.Vector4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIColor4D;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIFile;
import org.lwjgl.assimp.AIFileCloseProc;
import org.lwjgl.assimp.AIFileCloseProcI;
import org.lwjgl.assimp.AIFileIO;
import org.lwjgl.assimp.AIFileOpenProc;
import org.lwjgl.assimp.AIFileOpenProcI;
import org.lwjgl.assimp.AIFileReadProc;
import org.lwjgl.assimp.AIFileReadProcI;
import org.lwjgl.assimp.AIFileSeek;
import org.lwjgl.assimp.AIFileSeekI;
import org.lwjgl.assimp.AIFileTellProc;
import org.lwjgl.assimp.AIFileTellProcI;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIString;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.assimp.Assimp;
import org.lwjgl.system.MemoryUtil;

import com.avogine.junkyard.scene.render.data.Material;
import com.avogine.junkyard.scene.render.data.Mesh;
import com.avogine.junkyard.scene.render.data.ModelData;
import com.avogine.junkyard.scene.render.data.Texture;
import com.avogine.junkyard.util.IOUtil;
import com.avogine.junkyard.util.ResourceConstants;

public class StaticModelLoader {

	/**
	 * A custom AIFileIO implementation to support loading from jar files.
	 */
	protected static final AIFileIO fileIo;

	static {
		fileIo = AIFileIO.create();
		AIFileOpenProcI fileOpenProc = new AIFileOpenProc() {
			public long invoke(long pFileIO, long fileName, long openMode) {
				AIFile aiFile = AIFile.create();
				final ByteBuffer data;
				String fileNameUtf8 = MemoryUtil.memUTF8(fileName);
				try {
					data = IOUtil.ioResourceToByteBuffer(fileNameUtf8, 8 * 1024);
				} catch (IOException e) {
					throw new RuntimeException("Could not open file: " + fileNameUtf8);
				}
				AIFileReadProcI fileReadProc = new AIFileReadProc() {
					public long invoke(long pFile, long pBuffer, long size, long count) {
						long max = Math.min(data.remaining(), size * count);
						MemoryUtil.memCopy(MemoryUtil.memAddress(data) + data.position(), pBuffer, max);
						return max;
					}
				};
				AIFileSeekI fileSeekProc = new AIFileSeek() {
					public int invoke(long pFile, long offset, int origin) {
						if (origin == Assimp.aiOrigin_CUR) {
							data.position(data.position() + (int) offset);
						} else if (origin == Assimp.aiOrigin_SET) {
							data.position((int) offset);
						} else if (origin == Assimp.aiOrigin_END) {
							data.position(data.limit() + (int) offset);
						}
						return 0;
					}
				};
				AIFileTellProcI fileTellProc = new AIFileTellProc() {
					public long invoke(long pFile) {
						return data.limit();
					}
				};
				aiFile.ReadProc(fileReadProc);
				aiFile.SeekProc(fileSeekProc);
				aiFile.FileSizeProc(fileTellProc);
				return aiFile.address();
			}
		};
		AIFileCloseProcI fileCloseProc = new AIFileCloseProc() {
			public void invoke(long pFileIO, long pFile) {
				/* Nothing to do */
			}
		};
		fileIo.set(fileOpenProc, fileCloseProc, 0);
	}

	public static ModelData loadStaticModel(String modelName) throws Exception {
		return loadStaticModel(modelName, Assimp.aiProcess_GenSmoothNormals | Assimp.aiProcess_JoinIdenticalVertices | Assimp.aiProcess_Triangulate | Assimp.aiProcess_FixInfacingNormals);
	}

	public static ModelData loadStaticModel(String modelName, int flags) throws Exception {
		String modelPath = ResourceConstants.MODEL_PATH + modelName;
		AIScene aiScene = Assimp.aiImportFileEx(modelPath, flags, fileIo);
		if (aiScene == null) {
			throw new IllegalStateException(Assimp.aiGetErrorString());
		}

		int numMaterials = aiScene.mNumMaterials();
		PointerBuffer aiMaterials = aiScene.mMaterials();
		List<Material> materials = new ArrayList<>();
		for (int i = 0; i < numMaterials; i++) {
			AIMaterial aiMaterial = AIMaterial.create(aiMaterials.get(i));
			processMaterial(aiMaterial, materials);
		}

		int numMeshes = aiScene.mNumMeshes();
		PointerBuffer aiMeshes = aiScene.mMeshes();
		Mesh[] meshes = new Mesh[numMeshes];
		for (int i = 0; i < numMeshes; i++) {
			AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
			Mesh mesh = processMesh(aiMesh, materials);
			meshes[i] = mesh;
		}
		
		Assimp.aiReleaseImport(aiScene);
		
		return new ModelData(meshes);
	}

	protected static void processMaterial(AIMaterial aiMaterial, List<Material> materials) throws Exception {
		AIColor4D color = AIColor4D.create();

		AIString path = AIString.calloc();
		Assimp.aiGetMaterialTexture(aiMaterial, Assimp.aiTextureType_DIFFUSE, 0, path, (IntBuffer) null, null, null, null, null, null);
		String textPath = path.dataString();
		Texture texture = null;
		if (textPath != null && textPath.length() > 0) {
			texture = TextureCache.getTexture(textPath);
		}

		Vector4f ambient = Material.DEFAULT_COLOR;
		int result = Assimp.aiGetMaterialColor(aiMaterial, Assimp.AI_MATKEY_COLOR_AMBIENT, Assimp.aiTextureType_NONE, 0, color);
		if (result == 0) {
			ambient = new Vector4f(color.r(), color.g(), color.b(), color.a());
		}

		Vector4f diffuse = Material.DEFAULT_COLOR;
		result = Assimp.aiGetMaterialColor(aiMaterial, Assimp.AI_MATKEY_COLOR_DIFFUSE, Assimp.aiTextureType_NONE, 0, color);
		if (result == 0) {
			diffuse = new Vector4f(color.r(), color.g(), color.b(), color.a());
		}

		Vector4f specular = Material.DEFAULT_COLOR;
		result = Assimp.aiGetMaterialColor(aiMaterial, Assimp.AI_MATKEY_COLOR_SPECULAR, Assimp.aiTextureType_NONE, 0, color);
		if (result == 0) {
			specular = new Vector4f(color.r(), color.g(), color.b(), color.a());
		}

		Material material = new Material(ambient, diffuse, specular, 1.0f);
		material.setTexture(texture);
		materials.add(material);
	}

	private static Mesh processMesh(AIMesh aiMesh, List<Material> materials) {
		List<Float> vertices = new ArrayList<>();
		List<Float> textures = new ArrayList<>();
		List<Float> normals = new ArrayList<>();
		List<Integer> indices = new ArrayList<>();

		processVertices(aiMesh, vertices);
		processNormals(aiMesh, normals);
		processTextureCoords(aiMesh, textures);
		processIndices(aiMesh, indices);

		Mesh mesh = new Mesh(
				ArrayUtils.toPrimitive(vertices.stream().toArray(Float[]::new)),
				ArrayUtils.toPrimitive(textures.stream().toArray(Float[]::new)),
				ArrayUtils.toPrimitive(normals.stream().toArray(Float[]::new)),
				indices.stream().mapToInt(Integer::intValue).toArray());
		Material material;
		int materialIdx = aiMesh.mMaterialIndex();
		if (materialIdx >= 0 && materialIdx < materials.size()) {
			material = materials.get(materialIdx);
		} else {
			material = new Material();
		}
		mesh.setMaterial(material);

		return mesh;
	}
	
	protected static void processVertices(AIMesh aiMesh, List<Float> vertices) {
		AIVector3D.Buffer aiVertices = aiMesh.mVertices();
		while(aiVertices.remaining() > 0) {
			AIVector3D aiVertex = aiVertices.get();
			vertices.add(aiVertex.x());
			vertices.add(aiVertex.y());
			vertices.add(aiVertex.z());
		}
	}

	protected static void processTextureCoords(AIMesh aiMesh, List<Float> textures) {
		// XXX: Will only load the 1st index of texture coords, look into this for multi-texturing?
		AIVector3D.Buffer aiTextureCoords = aiMesh.mTextureCoords(0);
		int numTextCoords = aiTextureCoords != null ? aiTextureCoords.remaining() : 0;
		for(int i = 0; i < numTextCoords; i++) {
			AIVector3D textCoord = aiTextureCoords.get();
			textures.add(textCoord.x());
			textures.add(textCoord.y());
			// Load y coordinate flipped if we're not correcting it in the TextureLoader
			//textures.add(1 - textCoord.y());
		}
	}

	protected static void processNormals(AIMesh aiMesh, List<Float> normals) {
		AIVector3D.Buffer aiNormals = aiMesh.mNormals();
		while(aiNormals != null && aiNormals.remaining() > 0) {
			AIVector3D aiNormal = aiNormals.get();
			normals.add(aiNormal.x());
			normals.add(aiNormal.y());
			normals.add(aiNormal.z());
		}
	}

	protected static void processIndices(AIMesh aiMesh, List<Integer> indices) {
		int numFaces = aiMesh.mNumFaces();
		AIFace.Buffer aiFaces = aiMesh.mFaces();
		for(int i = 0; i < numFaces; i++) {
			AIFace aiFace = aiFaces.get(i);
			IntBuffer buffer = aiFace.mIndices();
			while(buffer.remaining() > 0) {
				indices.add(buffer.get());
			}
		}
	}

}
