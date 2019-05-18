package com.avogine.junkyard.util;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class MathUtils {

	public static float barryCentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos) {
		float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
		float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
		float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
		float l3 = 1.0f - l1 - l2;
		return l1 * p1.y + l2 * p2.y + l3 * p3.y;
	}
	
	public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, float rz, float scale) {
		return new Matrix4f()
				.translate(translation)
				.rotateXYZ((float) Math.toRadians(rx), (float) Math.toRadians(ry), (float) Math.toRadians(rz))
				.scale(scale);
	}
	
	public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale) {
		return new Matrix4f()
				.translate(translation.x, translation.y, 0)
				.scale(new Vector3f(scale.x, scale.y, 1f));
	}

	public static int clamp(int value, int min, int max) {
		return value < min ? min : (value > max ? max : value);
	}
	
	public static float clamp(float value, float min, float max) {
		return value < min ? min : (value > max ? max : value);
	}
	
	public static double clamp(double value, double min, double max) {
		return value < min ? min : (value > max ? max : value);
	}
	
	/**
	 * Change over time at linear rate
	 */
	public static float linearTween(float time, float begin, float change, float duration) {
		return change * time / duration + begin;
	}
	
	/**
	 * Start slow then move quickly
	 */
	public static float easeIn(float t, float b, float c, float d) {
		return c*(t/=d)*t + b;
	}
	
	/**
	 * Start fast then slow down
	 */
	public static float easeOut(float t, float b, float c, float d) {
		return -c *(t/=d)*(t-2) + b;
	}
	
	public static float easeInOut(float t, float b, float c, float d) {
		if ((t/=d/2) < 1) return c/2*t*t + b;
		return -c/2 * ((--t)*(t-2) - 1) + b;
	}
	
	public static float pingPong(float start, float end, float duration) {
		return 0;
	}
}
