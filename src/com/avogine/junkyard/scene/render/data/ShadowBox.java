package com.avogine.junkyard.scene.render.data;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import com.avogine.junkyard.io.Window;
import com.avogine.junkyard.scene.Camera;

/**
 * Represents the 3D cuboidal area of the world in which objects will cast
 * shadows (basically represents the orthographic projection area for the shadow
 * render pass). It is updated each frame to optimise the area, making it as
 * small as possible (to allow for optimal shadow map resolution) while not
 * being too small to avoid objects not having shadows when they should.
 * Everything inside the cuboidal area represented by this object will be
 * rendered to the shadow map in the shadow render pass. Everything outside the
 * area won't be.
 * 
 * @author Karl
 *
 */
public class ShadowBox {

	private static final float OFFSET = 15;
	private static final Vector3f UP = new Vector3f(0, 1, 0);
	private static final Vector3f FORWARD = new Vector3f(0, 0, -1);
	//public static final float SHADOW_DISTANCE = 300;

	private float zNear, zFar;
	private float minX, maxX;
	private float minY, maxY;
	private float minZ, maxZ;
	private Window window;
	
	private final Matrix4f projViewMatrix;
	private final Matrix4f orthoProjMatrix;
	private final Matrix4f lightViewMatrix;

	private float farHeight, farWidth, nearHeight, nearWidth;

	/**
	 * Creates a new shadow box and calculates some initial values relating to
	 * the camera's view frustum, namely the width and height of the near plane
	 * and (possibly adjusted) far plane.
	 * 
	 * @param window
	 *            - the game window.
	 */
	public ShadowBox(float zNear, float zFar, Window window) {
		this.zNear = zNear;
		this.zFar = zFar;
		this.window = window;
		this.projViewMatrix = new Matrix4f();
		this.orthoProjMatrix = new Matrix4f();
		this.lightViewMatrix = new Matrix4f();
		calculateWidthsAndHeights();
	}

	/**
	 * Updates the bounds of the shadow box based on the light direction and the
	 * camera's view frustum, to make sure that the box covers the smallest area
	 * possible while still ensuring that everything inside the camera's view
	 * (within a certain range) will cast shadows.
	 */
	public void update(Camera camera) {
		Matrix4f rotation = calculateCameraRotationMatrix(camera);
		Vector3f forwardVector = rotation.transformDirection(FORWARD, new Vector3f());
		
		projViewMatrix.setPerspective(Window.FOV, getAspectRatio(), zNear, zFar);
		projViewMatrix.mul(camera.getViewMatrix());
		//projViewMatrix.frustumCorner(corner, point)
		
		Vector3f toFar = new Vector3f(forwardVector);
		toFar.mul(zFar);
		Vector3f toNear = new Vector3f(forwardVector);
		toNear.mul(zNear);
		Vector3f centerNear = toNear.add(camera.getPosition(), new Vector3f());
		Vector3f centerFar = toFar.add(camera.getPosition(), new Vector3f());
		
		Vector4f[] points = calculateFrustumVertices(rotation, forwardVector, centerNear, centerFar);

		boolean first = true;
		for (Vector4f point : points) {
			if (first) {
				minX = point.x;
				maxX = point.x;
				minY = point.y;
				maxY = point.y;
				minZ = point.z;
				maxZ = point.z;
				first = false;
				continue;
			}
			if (point.x > maxX) {
				maxX = point.x;
			} else if (point.x < minX) {
				minX = point.x;
			}
			if (point.y > maxY) {
				maxY = point.y;
			} else if (point.y < minY) {
				minY = point.y;
			}
			if (point.z > maxZ) {
				maxZ = point.z;
			} else if (point.z < minZ) {
				minZ = point.z;
			}
		}
		maxZ += OFFSET;

		//orthoProjMatrix.setOrtho(minX, maxX, minY, maxY, 0, getLength());
		orthoProjMatrix.identity();
		orthoProjMatrix.m00(2f / getWidth());
		orthoProjMatrix.m11(2f / getHeight());
		orthoProjMatrix.m22(-2f / getLength());
		orthoProjMatrix.m33(1);
	}

	public void updateLightView(Vector3f direction) {
		direction.normalize();
		Vector3f center = getCenter().negate();
		lightViewMatrix.identity();
		float pitch = (float) Math.acos(new Vector2f(direction.x, direction.z).length());
		lightViewMatrix.rotate(pitch, new Vector3f(1, 0, 0));
		float yaw = (float) Math.toDegrees(((float) Math.atan(Float.isNaN(direction.x / direction.z) ? 0f : direction.x / direction.z)));
		yaw = direction.z > 0 ? yaw - 180 : yaw;
		lightViewMatrix.rotate((float) -Math.toRadians(yaw), new Vector3f(0, 1, 0));
		lightViewMatrix.translate(center);
	}
	
	/**
	 * Calculates the center of the "view cuboid" in light space first, and then
	 * converts this to world space using the inverse light's view matrix.
	 * 
	 * @return The center of the "view cuboid" in world space.
	 */
	public Vector3f getCenter() {
		float x = (minX + maxX) / 2f;
		float y = (minY + maxY) / 2f;
		float z = (minZ + maxZ) / 2f;
		Vector4f cen = new Vector4f(x, y, z, 1);
		Matrix4f invertedLight = new Matrix4f();
		//window.getLightViewMatrix().invert(invertedLight);
		lightViewMatrix.invert(invertedLight);
		Vector4f inv4 = invertedLight.transform(cen, new Vector4f());
		return new Vector3f(inv4.x, inv4.y, inv4.z);
	}

	/**
	 * @return The width of the "view cuboid" (orthographic projection area).
	 */
	public float getWidth() {
		return maxX - minX;
	}

	/**
	 * @return The height of the "view cuboid" (orthographic projection area).
	 */
	public float getHeight() {
		return maxY - minY;
	}

	/**
	 * @return The length of the "view cuboid" (orthographic projection area).
	 */
	public float getLength() {
		return maxZ - minZ;
	}

	/**
	 * Calculates the position of the vertex at each corner of the view frustum
	 * in light space (8 vertices in total, so this returns 8 positions).
	 * 
	 * @param rotation
	 *            - camera's rotation.
	 * @param forwardVector
	 *            - the direction that the camera is aiming, and thus the
	 *            direction of the frustum.
	 * @param centerNear
	 *            - the center point of the frustum's near plane.
	 * @param centerFar
	 *            - the center point of the frustum's (possibly adjusted) far
	 *            plane.
	 * @return The positions of the vertices of the frustum in light space.
	 */
	private Vector4f[] calculateFrustumVertices(Matrix4f rotation, Vector3f forwardVector, Vector3f centerNear, Vector3f centerFar) {
		Vector3f upVector = rotation.transformDirection(UP, new Vector3f());
		Vector3f rightVector = forwardVector.cross(upVector, new Vector3f());
		Vector3f downVector = new Vector3f(-upVector.x, -upVector.y, -upVector.z);
		Vector3f leftVector = new Vector3f(-rightVector.x, -rightVector.y, -rightVector.z);
		Vector3f farTop = centerFar.add(new Vector3f(upVector.x * farHeight,
				upVector.y * farHeight,
				upVector.z * farHeight),
				new Vector3f());
		Vector3f farBottom = centerFar.add(new Vector3f(downVector.x * farHeight,
				downVector.y * farHeight,
				downVector.z * farHeight),
				new Vector3f());
		Vector3f nearTop = centerNear.add(new Vector3f(upVector.x * nearHeight,
				upVector.y * nearHeight,
				upVector.z * nearHeight),
				new Vector3f());
		Vector3f nearBottom = centerNear.add(new Vector3f(downVector.x * nearHeight,
				downVector.y * nearHeight,
				downVector.z * nearHeight),
				new Vector3f());
		Vector4f[] points = new Vector4f[8];
		points[0] = calculateLightSpaceFrustumCorner(farTop, rightVector, farWidth);
		points[1] = calculateLightSpaceFrustumCorner(farTop, leftVector, farWidth);
		points[2] = calculateLightSpaceFrustumCorner(farBottom, rightVector, farWidth);
		points[3] = calculateLightSpaceFrustumCorner(farBottom, leftVector, farWidth);
		points[4] = calculateLightSpaceFrustumCorner(nearTop, rightVector, nearWidth);
		points[5] = calculateLightSpaceFrustumCorner(nearTop, leftVector, nearWidth);
		points[6] = calculateLightSpaceFrustumCorner(nearBottom, rightVector, nearWidth);
		points[7] = calculateLightSpaceFrustumCorner(nearBottom, leftVector, nearWidth);
		return points;
	}

	/**
	 * Calculates one of the corner vertices of the view frustum in world space
	 * and converts it to light space.
	 * 
	 * @param startPoint
	 *            - the starting center point on the view frustum.
	 * @param direction
	 *            - the direction of the corner from the start point.
	 * @param width
	 *            - the distance of the corner from the start point.
	 * @return - The relevant corner vertex of the view frustum in light space.
	 */
	private Vector4f calculateLightSpaceFrustumCorner(Vector3f startPoint, Vector3f direction, float width) {
		Vector3f point = startPoint.add(new Vector3f(direction.x * width, direction.y * width, direction.z * width),
				new Vector3f());
		Vector4f point4f = new Vector4f(point.x, point.y, point.z, 1f);
		//window.getLightViewMatrix().transform(point4f, point4f);
		lightViewMatrix.transform(point4f, point4f);
		return point4f;
	}

	/**
	 * @return The rotation of the camera represented as a matrix.
	 */
	private Matrix4f calculateCameraRotationMatrix(Camera camera) {
		Matrix4f rotation = new Matrix4f();
		rotation.rotate((float) Math.toRadians(-camera.getRotation().y), new Vector3f(0, 1, 0));
		rotation.rotate((float) Math.toRadians(-camera.getRotation().x), new Vector3f(1, 0, 0));
		return rotation;
	}

	/**
	 * Calculates the width and height of the near and far planes of the
	 * camera's view frustum. However, this doesn't have to use the "actual" far
	 * plane of the view frustum. It can use a shortened view frustum if desired
	 * by bringing the far-plane closer, which would increase shadow resolution
	 * but means that distant objects wouldn't cast shadows.
	 */
	private void calculateWidthsAndHeights() {
		farWidth = (float) (zFar * Math.tan(Math.toRadians(Window.FOV)));
		nearWidth = (float) (zNear * Math.tan(Math.toRadians(Window.FOV)));
		farHeight = farWidth / getAspectRatio();
		nearHeight = nearWidth / getAspectRatio();
	}

	/**
	 * @return The aspect ratio of the display (width:height ratio).
	 */
	private float getAspectRatio() {
		return (float) window.getWidth() / (float) window.getHeight();
	}

	public Matrix4f getLightViewMatrix() {
		return lightViewMatrix;
	}

	public Matrix4f getOrthoProjMatrix() {
		return orthoProjMatrix;
	}

}
