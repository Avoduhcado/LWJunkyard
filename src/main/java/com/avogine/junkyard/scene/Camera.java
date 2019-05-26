package com.avogine.junkyard.scene;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.openal.AL10;

import com.avogine.junkyard.io.event.KeyInputEvent;
import com.avogine.junkyard.io.event.KeyInputListener;
import com.avogine.junkyard.io.event.MouseMotionInputEvent;
import com.avogine.junkyard.io.event.MouseMotionInputListener;
import com.avogine.junkyard.scene.entity.body.KineticBody;
import com.avogine.junkyard.util.MathUtils;
import com.avogine.junkyard.window.Window;

public class Camera extends KineticBody implements MouseMotionInputListener, KeyInputListener {

	private Matrix4f viewMatrix = new Matrix4f();
	
	private float followRadius;
	
	public boolean invertedX = true;
	public boolean invertedY = false;

	private final float default_camera_speed = 60f;
	private float masterListenerVolume = 0.1f;
	
	public Camera(Window window, Vector3f position, Quaternionf rotation, float radius) {
		super(Cast.CAMERA_ID);
		this.position.set(position);
		this.rotation.set(rotation);
		followRadius = radius;
		
		setSpeed(default_camera_speed);
		AL10.alListenerf(AL10.AL_GAIN, masterListenerVolume);
		
		window.getInput().addInputListener(this);
	}

	@Override
	public void move() {
		super.move();
		AL10.alListener3f(AL10.AL_POSITION, position.x, position.y, position.z);
	}
	
	@Override
	public void mouseMoved(MouseMotionInputEvent e) {
		// Restrict camera tilt so you don't flip it over or anything
		rotation.x = MathUtils.clamp((float) (rotation.x - (e.getDy() / 10)), -65, 80);
		rotation.x %= 360;
		rotation.y -= e.getDx() / 10;
		rotation.y %= 360;
		
		float[] orientation = {viewMatrix.m02(), viewMatrix.m12(), viewMatrix.m22(), 0, -1, 0};
		AL10.alListenerfv(AL10.AL_ORIENTATION, orientation);
	}

	@Override
	public void mouseDragged(MouseMotionInputEvent e) {
	}

	@Override
	public void keyPressed(KeyInputEvent e) {
	}

	@Override
	public void keyReleased(KeyInputEvent e) {
	}

	@Override
	public void keyHeld(KeyInputEvent e) {
		switch(e.getKey()) {
		case GLFW.GLFW_KEY_W:
			velocity.add(0, 0, -1);
			break;
		case GLFW.GLFW_KEY_A:
			velocity.add(-1, 0, 0);
			break;
		case GLFW.GLFW_KEY_S:
			velocity.add(0, 0, 1);
			break;
		case GLFW.GLFW_KEY_D:
			velocity.add(1, 0, 0);
			break;
		case GLFW.GLFW_KEY_SPACE:
			velocity.add(0, 1, 0);
			break;
		case GLFW.GLFW_KEY_LEFT_SHIFT:
			velocity.add(0, -1, 0);
			break;
		}
		
		setAwake(true);
	}
	
	public Matrix4f getViewMatrix() {
		return viewMatrix.identity().arcball(followRadius, position, (float) Math.toRadians(rotation.x), (float) Math.toRadians(rotation.y));
	}
	
	public Matrix4f getViewMatrixNoTranslation() {
		return getViewMatrix().setTranslation(0, 0, 0);
	}
	
}
