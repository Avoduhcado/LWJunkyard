package com.avogine.junkyard.scene.util;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.avogine.junkyard.scene.entity.Body;
import com.avogine.junkyard.scene.render.data.ShadowBox;

public class Transformation {

	private final Matrix4f orthographic;
	private final Matrix4f orthographic2D;

	private final Matrix4f model;

	public Transformation() {
		orthographic = new Matrix4f();
		orthographic2D = new Matrix4f();

		model = new Matrix4f();
	}

	public Matrix4f updateOrthographic2DMatrix(float left, float right, float bottom, float top) {
		orthographic2D.setOrtho2D(left, right, bottom, top);
		return orthographic;
	}

	public Matrix4f updateOrthographicMatrix(float left, float right, float bottom, float top, float zNear, float zFar) {
		orthographic.setOrtho(left, right, bottom, top, zNear, zFar);
		return orthographic;
	}

	/**
	 * Creates the orthographic projection matrix. This projection matrix
	 * basically sets the width, length and height of the "view cuboid", based
	 * on the values that were calculated in the {@link ShadowBox} class.
	 * 
	 * @param width shadow box width.
	 * @param height shadow box height.
	 * @param length shadow box length.
	 */
	public void updateOrthographicMatrix(float width, float height, float length) {
		orthographic.identity();
		orthographic.m00(2f / width);
		orthographic.m11(2f / height);
		orthographic.m22(-2f / length);
		orthographic.m33(1);
	}

	public Matrix4f buildModelMatrix(Body body) {
		return model.translationRotateScale(
				body.getPosition().x, body.getPosition().y, body.getPosition().z,
				body.getRotation().x, body.getRotation().y, body.getRotation().z, 1,
				body.getScale().x, body.getScale().y, body.getScale().z);
	}
	
	public static Matrix4f updateGenericViewMatrix(Vector3f position, Vector3f rotation, Matrix4f matrix) {
		// First do the rotation so camera rotates over its position
		return matrix.rotationX((float)Math.toRadians(rotation.x))
				.rotateY((float)Math.toRadians(rotation.y))
				.translate(-position.x, -position.y, -position.z);
	}

	public Matrix4f getOrthographicMatrix() {
		return orthographic;
	}

	public Matrix4f getOrthographic2DMatrix() {
		return orthographic2D;
	}

	public Matrix4f getModel() {
		return model;
	}

}
