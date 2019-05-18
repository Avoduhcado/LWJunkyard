package com.avogine.junkyard.scene.render;

import javax.vecmath.Vector3f;

import com.avogine.junkyard.scene.render.data.Line;
import com.bulletphysics.linearmath.IDebugDraw;

public class AvoDebugDraw extends IDebugDraw {
	
	// XXX This class is super inefficient, probs because the line renderer is real bad
	
	private int debugMode;
	
	private Renderer renderer;
	
	public AvoDebugDraw(Renderer renderer) {
		this.renderer = renderer;
	}
	
	@Override
	public void drawLine(Vector3f from, Vector3f to, Vector3f color) {
		if(debugMode > 0) {
			renderer.renderLine(new Line(from, to, color), renderer.defaultWindow, renderer.defaultCamera);
		}
	}

	@Override
	public void drawContactPoint(Vector3f PointOnB, Vector3f normalOnB, float distance, int lifeTime, Vector3f color) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reportErrorWarning(String warningString) {
		System.err.println(warningString);
	}

	@Override
	public void draw3dText(Vector3f location, String textString) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDebugMode(int debugMode) {
		this.debugMode = debugMode;
	}

	@Override
	public int getDebugMode() {
		return debugMode;
	}

}
