package com.avogine.junkyard.scene.render.util;

import com.avogine.junkyard.window.util.WindowConstants;

public class RenderConstants {

	/** The maximum number of weight influence a mesh's joint can have */
	public static final int MAX_WEIGHTS = 4;
	/** The maximum number of animation joints that a mesh can be positioned with */
	public static final int MAX_JOINTS = 150;

	/** The maximum number of Point Light entities that can be rendered in a single scene */
	public static final int MAX_POINT_LIGHTS = 5;
	/** The maximum number of Spot Light entities that can be rendered in a single scene */
	public static final int MAX_SPOT_LIGHTS = 5;
	
	/** The maximum number of shadow cascades to draw */
	public static int MAX_SHADOW_CASCADES = 3;
	
	public static float[] SHADOW_CASCADES = new float[] {
			WindowConstants.DEFAULT_FAR_PLANE * 0.15f,
			WindowConstants.DEFAULT_FAR_PLANE * 0.3f,
			WindowConstants.DEFAULT_FAR_PLANE	
	};

	/** Whether or not animation tweening should be applied to models */
	public static boolean TWEENING = true;

	// TODO Source this from somewhere else probably, or drop it entirely
	public static final float SIZE = WindowConstants.DEFAULT_FAR_PLANE / 2f;
	public static final float[] CUBE_VERTICES = {
			// RIGHT
			-SIZE,  SIZE, -SIZE,
			-SIZE, -SIZE, -SIZE,
			SIZE, -SIZE, -SIZE,
			SIZE, -SIZE, -SIZE,
			SIZE,  SIZE, -SIZE,
			-SIZE,  SIZE, -SIZE,

			// LEFT
			-SIZE, -SIZE,  SIZE,
			-SIZE, -SIZE, -SIZE,
			-SIZE,  SIZE, -SIZE,
			-SIZE,  SIZE, -SIZE,
			-SIZE,  SIZE,  SIZE,
			-SIZE, -SIZE,  SIZE,

			// TOP
			SIZE, -SIZE, -SIZE,
			SIZE, -SIZE,  SIZE,
			SIZE,  SIZE,  SIZE,
			SIZE,  SIZE,  SIZE,
			SIZE,  SIZE, -SIZE,
			SIZE, -SIZE, -SIZE,

			// BOTTOM
			-SIZE, -SIZE,  SIZE,
			-SIZE,  SIZE,  SIZE,
			SIZE,  SIZE,  SIZE,
			SIZE,  SIZE,  SIZE,
			SIZE, -SIZE,  SIZE,
			-SIZE, -SIZE,  SIZE,

			// BACK
			-SIZE,  SIZE, -SIZE,
			SIZE,  SIZE, -SIZE,
			SIZE,  SIZE,  SIZE,
			SIZE,  SIZE,  SIZE,
			-SIZE,  SIZE,  SIZE,
			-SIZE,  SIZE, -SIZE,

			// FRONT
			-SIZE, -SIZE, -SIZE,
			-SIZE, -SIZE,  SIZE,
			SIZE, -SIZE, -SIZE,
			SIZE, -SIZE, -SIZE,
			-SIZE, -SIZE,  SIZE,
			SIZE, -SIZE,  SIZE
	};

}
