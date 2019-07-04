package com.avogine.junkyard.scene.util;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.avogine.junkyard.scene.entity.Body;

public class Transformation {

	/** A matrix used for transforming an object in model space */
	private final Matrix4f modelMatrix;

	/** A matrix used for transforming an object in model+view space */
	private final Matrix4f modelViewMatrix;

	/** A matrix used for transforming an object in model+lightView space */
	private final Matrix4f modelLightViewMatrix;

	/** A matrix used for transforming the view to lightView space */
	private final Matrix4f lightViewMatrix;

	/** A matrix used for transforming flat objects in 2D orthographic space */
	private final Matrix4f ortho2DMatrix;

	/** A matrix used for transforming objects in orthographic model space */
	private final Matrix4f orthoModelMatrix;
	
	public Transformation() {
		modelMatrix = new Matrix4f();
		modelViewMatrix = new Matrix4f();
		modelLightViewMatrix = new Matrix4f();
		lightViewMatrix = new Matrix4f();
		ortho2DMatrix = new Matrix4f();
		orthoModelMatrix = new Matrix4f();
	}

	public static Matrix4f updateGenericViewMatrix(Vector3f position, Vector3f rotation, Matrix4f matrix) {
		// First do the rotation so camera rotates over its position
		return matrix.rotationX((float)Math.toRadians(rotation.x))
				.rotateY((float)Math.toRadians(rotation.y))
				.translate(-position.x, -position.y, -position.z);
	}

	public final Matrix4f getOrtho2DProjectionMatrix(float left, float right, float bottom, float top) {
		return ortho2DMatrix.setOrtho2D(left, right, bottom, top);
	}

	public Matrix4f buildModelMatrix(Body body) {
		Quaternionf rotation = body.getRotation();
		return modelMatrix.translationRotateScale(
				body.getPosition().x, body.getPosition().y, body.getPosition().z,
				rotation.x, rotation.y, rotation.z, rotation.w,
				body.getScale().x, body.getScale().y, body.getScale().z);
	}

	public Matrix4f buildModelViewMatrix(Body body, Matrix4f viewMatrix) {
		return buildModelViewMatrix(buildModelMatrix(body), viewMatrix);
	}

	public Matrix4f buildModelViewMatrix(Matrix4f modelMatrix, Matrix4f viewMatrix) {
		return viewMatrix.mulAffine(modelMatrix, modelViewMatrix);
	}

	public Matrix4f buildModelLightViewMatrix(Body body, Matrix4f lightViewMatrix) {
		return buildModelViewMatrix(buildModelMatrix(body), lightViewMatrix);
	}

	public Matrix4f buildModelLightViewMatrix(Matrix4f modelMatrix, Matrix4f lightViewMatrix) {
		return lightViewMatrix.mulAffine(modelMatrix, modelLightViewMatrix);
	}

	public Matrix4f getLightViewMatrix() {
		return lightViewMatrix;
	}

	public void setLightViewMatrix(Matrix4f lightViewMatrix) {
		this.lightViewMatrix.set(lightViewMatrix);
	}

	public Matrix4f updateLightViewMatrix(Vector3f position, Vector3f rotation) {
		return updateGenericViewMatrix(position, rotation, lightViewMatrix);
	}

	public Matrix4f buildOrthoProjModelMatrix(Body body, Matrix4f orthoMatrix) {
		return orthoMatrix.mulOrthoAffine(buildModelMatrix(body), orthoModelMatrix);
	}
	
}
