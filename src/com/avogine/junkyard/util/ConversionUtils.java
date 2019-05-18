package com.avogine.junkyard.util;

public class ConversionUtils {

	public static javax.vecmath.Vector3f convertJomlToVecmath(org.joml.Vector3f inVec) {
		javax.vecmath.Vector3f outVec = new javax.vecmath.Vector3f(inVec.x, inVec.y, inVec.z);
		return outVec;
	}
	
	public static org.joml.Vector3f convertVecmathToJoml(javax.vecmath.Vector3f inVec) {
		org.joml.Vector3f outVec = new org.joml.Vector3f(inVec.x, inVec.y, inVec.z);
		return outVec;
	}
	
}
