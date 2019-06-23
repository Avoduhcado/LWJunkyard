package com.avogine.junkyard.scene.render.load;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIAnimation;
import org.lwjgl.assimp.AIBone;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIMatrix4x4;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AINode;
import org.lwjgl.assimp.AINodeAnim;
import org.lwjgl.assimp.AIQuatKey;
import org.lwjgl.assimp.AIQuaternion;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.assimp.AIVectorKey;
import org.lwjgl.assimp.AIVertexWeight;
import org.lwjgl.assimp.Assimp;

import com.avogine.junkyard.scene.render.data.AnimatedFrame;
import com.avogine.junkyard.scene.render.data.AnimationData;
import com.avogine.junkyard.scene.render.data.Bone;
import com.avogine.junkyard.scene.render.data.Material;
import com.avogine.junkyard.scene.render.data.Mesh;
import com.avogine.junkyard.scene.render.data.ModelData;
import com.avogine.junkyard.scene.render.data.Node;
import com.avogine.junkyard.scene.render.data.VertexWeight;
import com.avogine.junkyard.scene.render.util.RenderConstants;
import com.avogine.junkyard.util.ResourceConstants;

public class AnimatedModelLoader extends StaticModelLoader {

	private static void buildTransFormationMatrices(AINodeAnim aiNodeAnim, Node node) {
		int numFrames = aiNodeAnim.mNumPositionKeys();
		AIVectorKey.Buffer positionKeys = aiNodeAnim.mPositionKeys();
		AIVectorKey.Buffer scalingKeys = aiNodeAnim.mScalingKeys();
		AIQuatKey.Buffer rotationKeys = aiNodeAnim.mRotationKeys();

		for (int i = 0; i < numFrames; i++) {
			AIVectorKey aiVecKey = positionKeys.get(i);
			AIVector3D vec = aiVecKey.mValue();

			Matrix4f transfMat = new Matrix4f().translate(vec.x(), vec.y(), vec.z());

			AIQuatKey quatKey = rotationKeys.get(i);
			AIQuaternion aiQuat = quatKey.mValue();
			Quaternionf quat = new Quaternionf(aiQuat.x(), aiQuat.y(), aiQuat.z(), aiQuat.w());
			transfMat.rotate(quat);

			if (i < aiNodeAnim.mNumScalingKeys()) {
				aiVecKey = scalingKeys.get(i);
				vec = aiVecKey.mValue();
				transfMat.scale(vec.x(), vec.y(), vec.z());
			}

			node.addTransformation(transfMat);

			int animBehavior = aiNodeAnim.mPreState();
			
			node.addAnimBehavior(animBehavior);
		}
	}

	public static ModelData loadAnimatedModel(String modelName) throws Exception {
		return loadAnimatedModel(modelName, Assimp.aiProcess_GenSmoothNormals | Assimp.aiProcess_JoinIdenticalVertices | Assimp.aiProcess_Triangulate
				| Assimp.aiProcess_FixInfacingNormals | Assimp.aiProcess_LimitBoneWeights);
	}

	public static ModelData loadAnimatedModel(String modelName, int flags) throws Exception {
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

		List<Bone> boneList = new ArrayList<>();
		int numMeshes = aiScene.mNumMeshes();
		PointerBuffer aiMeshes = aiScene.mMeshes();
		Mesh[] meshes = new Mesh[numMeshes];
		for (int i = 0; i < numMeshes; i++) {
			AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
			Mesh mesh = processMesh(aiMesh, materials, boneList);
			meshes[i] = mesh;
		}

		AINode aiRootNode = aiScene.mRootNode();
		Matrix4f rootTransfromation = AnimatedModelLoader.toMatrix(aiRootNode.mTransformation());
		Node rootNode = processNodesHierarchy(aiRootNode, null);
		Map<String, AnimationData> animations = processAnimations(aiScene, boneList, rootNode, rootTransfromation);
		ModelData model = new ModelData(meshes, animations);

		Assimp.aiReleaseImport(aiScene);
		
		return model;
	}

	private static List<AnimatedFrame> buildAnimationFrames(List<Bone> boneList, Node rootNode, Matrix4f rootTransformation) {
		int numFrames = rootNode.getAnimationFrames();
		List<AnimatedFrame> frameList = new ArrayList<>();
		for (int i = 0; i < numFrames; i++) {
			AnimatedFrame frame = new AnimatedFrame();
			frameList.add(frame);

			int numBones = boneList.size();
			for (int j = 0; j < numBones; j++) {
				Bone bone = boneList.get(j);
				Node node = rootNode.findByName(bone.getBoneName());
				Matrix4f boneMatrix = Node.getParentTransforms(node, i);
				boneMatrix.mul(bone.getOffsetMatrix());
				boneMatrix = new Matrix4f(rootTransformation).mul(boneMatrix);
				frame.setMatrix(j, boneMatrix);
			}
		}

		return frameList;
	}

	private static Map<String, AnimationData> processAnimations(AIScene aiScene, List<Bone> boneList, Node rootNode, Matrix4f rootTransformation) {
		Map<String, AnimationData> animations = new HashMap<>();

		// Process all animations
		int numAnimations = aiScene.mNumAnimations();
		PointerBuffer aiAnimations = aiScene.mAnimations();
		for (int i = 0; i < numAnimations; i++) {
			AIAnimation aiAnimation = AIAnimation.create(aiAnimations.get(i));

			// Calculate transformation matrices for each node
			int numChanels = aiAnimation.mNumChannels();
			PointerBuffer aiChannels = aiAnimation.mChannels();
			for (int j = 0; j < numChanels; j++) {
				AINodeAnim aiNodeAnim = AINodeAnim.create(aiChannels.get(j));
				String nodeName = aiNodeAnim.mNodeName().dataString();
				Node node = rootNode.findByName(nodeName);
				buildTransFormationMatrices(aiNodeAnim, node);
			}

			List<AnimatedFrame> frames = buildAnimationFrames(boneList, rootNode, rootTransformation);
			AnimationData animation = new AnimationData(aiAnimation.mName().dataString(), frames, aiAnimation.mDuration());
			//Animation animation = new Animation(aiAnimation.mName().dataString(), frames, aiAnimation.mDuration());
			animations.put(animation.getName(), animation);
			
			rootNode.clear();
		}
		return animations;
	}

	private static void processBones(AIMesh aiMesh, List<Bone> boneList, List<Integer> boneIds, List<Float> weights) {
		Map<Integer, List<VertexWeight>> weightSet = new HashMap<>();
		int numBones = aiMesh.mNumBones();
		PointerBuffer aiBones = aiMesh.mBones();
		for (int i = 0; i < numBones; i++) {
			AIBone aiBone = AIBone.create(aiBones.get(i));
			int id = boneList.size();
			Bone bone = new Bone(id, aiBone.mName().dataString(), toMatrix(aiBone.mOffsetMatrix()));
			boneList.add(bone);
			int numWeights = aiBone.mNumWeights();
			AIVertexWeight.Buffer aiWeights = aiBone.mWeights();
			for (int j = 0; j < numWeights; j++) {
				AIVertexWeight aiWeight = aiWeights.get(j);
				VertexWeight vw = new VertexWeight(bone.getBoneId(), aiWeight.mVertexId(),
						aiWeight.mWeight());
				List<VertexWeight> vertexWeightList = weightSet.get(vw.getVertexId());
				if (vertexWeightList == null) {
					vertexWeightList = new ArrayList<>();
					weightSet.put(vw.getVertexId(), vertexWeightList);
				}
				vertexWeightList.add(vw);
			}
		}

		int numVertices = aiMesh.mNumVertices();
		for (int i = 0; i < numVertices; i++) {
			List<VertexWeight> vertexWeightList = weightSet.get(i);
			int size = vertexWeightList != null ? vertexWeightList.size() : 0;
			for (int j = 0; j < RenderConstants.MAX_WEIGHTS; j++) {
				if (j < size) {
					VertexWeight vw = vertexWeightList.get(j);
					weights.add(vw.getWeight());
					boneIds.add(vw.getBoneId());
				} else {
					weights.add(0.0f);
					boneIds.add(0);
				}
			}
		}
	}

	private static Mesh processMesh(AIMesh aiMesh, List<Material> materials, List<Bone> boneList) {
		List<Float> vertices = new ArrayList<>();
		List<Float> textures = new ArrayList<>();
		List<Float> normals = new ArrayList<>();
		List<Integer> indices = new ArrayList<>();
		List<Integer> boneIds = new ArrayList<>();
		List<Float> weights = new ArrayList<>();

		processVertices(aiMesh, vertices);
		processNormals(aiMesh, normals);
		processTextureCoords(aiMesh, textures);
		processIndices(aiMesh, indices);
		processBones(aiMesh, boneList, boneIds, weights);

		Mesh mesh = new Mesh(
				ArrayUtils.toPrimitive(vertices.stream().toArray(Float[]::new)),
				ArrayUtils.toPrimitive(textures.stream().toArray(Float[]::new)),
				ArrayUtils.toPrimitive(normals.stream().toArray(Float[]::new)),
				ArrayUtils.toPrimitive(weights.stream().toArray(Float[]::new)),
				boneIds.stream().mapToInt(Integer::intValue).toArray(),
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

	private static Node processNodesHierarchy(AINode aiNode, Node parentNode) {
		String nodeName = aiNode.mName().dataString();
		Node node = new Node(nodeName, parentNode);

		int numChildren = aiNode.mNumChildren();
		PointerBuffer aiChildren = aiNode.mChildren();
		for (int i = 0; i < numChildren; i++) {
			AINode aiChildNode = AINode.create(aiChildren.get(i));
			Node childNode = processNodesHierarchy(aiChildNode, node);
			node.addChild(childNode);
		}

		return node;
	}

	private static Matrix4f toMatrix(AIMatrix4x4 aiMatrix4x4) {
		Matrix4f result = new Matrix4f();
		result.m00(aiMatrix4x4.a1());
		result.m10(aiMatrix4x4.a2());
		result.m20(aiMatrix4x4.a3());
		result.m30(aiMatrix4x4.a4());
		result.m01(aiMatrix4x4.b1());
		result.m11(aiMatrix4x4.b2());
		result.m21(aiMatrix4x4.b3());
		result.m31(aiMatrix4x4.b4());
		result.m02(aiMatrix4x4.c1());
		result.m12(aiMatrix4x4.c2());
		result.m22(aiMatrix4x4.c3());
		result.m32(aiMatrix4x4.c4());
		result.m03(aiMatrix4x4.d1());
		result.m13(aiMatrix4x4.d2());
		result.m23(aiMatrix4x4.d3());
		result.m33(aiMatrix4x4.d4());

		return result;
	}

}
