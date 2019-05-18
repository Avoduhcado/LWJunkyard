package com.avogine.junkyard.io;

import java.nio.IntBuffer;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.system.MemoryStack;

import com.avogine.junkyard.io.util.WindowManager;
import com.avogine.junkyard.memory.MemoryManaged;
import com.avogine.junkyard.scene.Stage;
import com.avogine.junkyard.scene.render.data.ShadowBox;
import com.avogine.junkyard.system.AvoEventQueue;
import com.avogine.junkyard.util.MathUtils;

public class Window implements MemoryManaged {

	// TODO Expose in options
	public static final float FOV = 70;
	public static final float NEAR_PLANE = 0.1f;
	public static final float FAR_PLANE = 10000.0f;

	private Matrix4f projectionMatrix;
	private Matrix4f orthographicMatrix;
	// XXX
	private Matrix4f lightViewMatrix;
	private Matrix4f offsetMatrix;

	// TODO Expose in options
	private int refreshRate = 60;
	private int unfocusedRefreshRate = 60;

	private long ID;

	private int width;
	private int height;
	private String title;

	private boolean hasFocus = true;

	private Input input;
	private MusicBox musicBox;
	private Stage stage;

	public Window(int width, int height, String title) {
		this.width = width;
		this.height = height;
		this.title = title;
	}

	public void createWindow() {
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);
		GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, 8);
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
		//GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_DEBUG_CONTEXT, GLFW.GLFW_TRUE);

		ID = GLFW.glfwCreateWindow(width, height, title, 0, 0);
		if(ID == 0) {
			throw new IllegalStateException("Failed to create window!");
		}

		/*try {
			PNGDecoder decoder = new PNGDecoder(ClassLoader.getSystemResourceAsStream("graphics/LDIcon.png"));

			int iconWidth = decoder.getWidth();
			int iconHeight = decoder.getHeight();
			ByteBuffer buffer = BufferUtils.createByteBuffer(iconWidth * iconHeight * 4);
			decoder.decode(buffer, iconWidth * 4, PNGDecoder.Format.RGBA);
			buffer.flip();
			GLFWImage image = GLFWImage.malloc();
			image.set(iconWidth, iconHeight, buffer);
			GLFWImage.Buffer images = GLFWImage.malloc(1);
			images.put(0, image);

			GLFW.glfwSetWindowIcon(ID, images);

			images.free();
			image.free();
		} catch (IOException e) {
			System.err.println("Failed to load icon image.");
			e.printStackTrace();
		}*/

		GLFW.glfwSetFramebufferSizeCallback(ID, new GLFWFramebufferSizeCallback() {
			@Override
			public void invoke(long window, int width, int height) {
				Window.this.width = width;
				Window.this.height = height;
				//GL11.glViewport(0, 0, width, height);
			}
		});

		/*GLFW.glfwSetWindowSizeCallback(ID, (window, width, height) -> {
			GL11.glViewport(0, 0, width, height);
		});*/

		GLFW.glfwSetWindowFocusCallback(ID, (window, focused) -> {
			hasFocus = focused;
			if(hasFocus && WindowManager.getWindowInFocus() != this) {
				WindowManager.setWindowInFocus(ID);
			}
		});

		GLFW.glfwSetWindowCloseCallback(ID, (w) -> {
			AvoEventQueue.doLater(() -> {
				cleanUp();
			});
		});

		GLFW.glfwMakeContextCurrent(ID);
		GL.createCapabilities();

		// TODO: Set a target monitor instead of just using primary
		GLFWVidMode videoMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
		setRefreshRate(videoMode.refreshRate());

		try(MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			GLFW.glfwGetWindowSize(ID, pWidth, pHeight);

			// Center the window
			GLFW.glfwSetWindowPos(ID, (videoMode.width() - pWidth.get(0)) / 2, (videoMode.height() - pHeight.get(0)) / 2);
		}

		// Enable backface culling
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);

		GL11.glEnable(GL13.GL_MULTISAMPLE);
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);

		//GL11.glEnable(GL43.GL_DEBUG_OUTPUT);

		GL11.glClearColor((float) MathUtils.clamp((float) Math.random(), 0, 0.75f), (float) Math.random(), (float) Math.random(), 1);

		projectionMatrix = new Matrix4f();
		orthographicMatrix = new Matrix4f();
		lightViewMatrix = new Matrix4f();
		offsetMatrix = createOffset();

		input = new Input(this);
		musicBox = new MusicBox(this);
		stage = new Stage(this);

		GLFW.glfwShowWindow(ID);
	}

	public void render() {
		if(GLFW.glfwGetCurrentContext() != ID) {
			GLFW.glfwMakeContextCurrent(ID);
		}

		//GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		stage.render();

		GLFW.glfwSwapBuffers(ID);
	}

	public void update() {
		input.update();
		stage.update();

		GLFW.glfwPollEvents();
	}

	public boolean shouldClose() {
		return GLFW.glfwWindowShouldClose(ID);
	}

	@Override
	public void cleanUp() {
		if(WindowManager.requestWindow(ID) == null) {
			return;
		}
		GLFW.glfwMakeContextCurrent(ID);
		stage.cleanUp();
		musicBox.cleanUp();
		GLFW.glfwDestroyWindow(ID);
		WindowManager.removeWindow(ID);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		GLFW.glfwSetWindowTitle(ID, title);
	}

	public Input getInput() {
		return input;
	}
	
	public MusicBox getMusicBox() {
		return musicBox;
	}

	public Stage getStage() {
		return stage;
	}

	public int getRefreshRate() {
		if(hasFocus) {
			return refreshRate;
		} else {
			return unfocusedRefreshRate;
		}
	}

	public void setRefreshRate(int refreshRate) {
		this.refreshRate = refreshRate;
	}

	public long getID() {
		return ID;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	// TODO These should probably be moved out of Window at this point and into some sort of utility
	public Matrix4f updateOrthographicMatrix() {
		orthographicMatrix.setOrtho2D(0, getWidth(), getHeight(), 0);
		return orthographicMatrix;
	}
	
	// XXX This might screw up the 2D matrix
	public Matrix4f updateOrthographicMatrix(float left, float right, float bottom, float top, float zNear, float zFar) {
		orthographicMatrix.setOrtho(left, right, bottom, top, zNear, zFar);
		return orthographicMatrix;
	}
	
	/**
	 * Creates the orthographic projection matrix. This projection matrix
	 * basically sets the width, length and height of the "view cuboid", based
	 * on the values that were calculated in the {@link ShadowBox} class.
	 * 
	 * @param width
	 *            - shadow box width.
	 * @param height
	 *            - shadow box height.
	 * @param length
	 *            - shadow box length.
	 */
	public void updateOrthoProjectionMatrix(float width, float height, float length) {
		orthographicMatrix.identity();
		orthographicMatrix.m00(2f / width);
		orthographicMatrix.m11(2f / height);
		orthographicMatrix.m22(-2f / length);
		orthographicMatrix.m33(1);
	}

	public Matrix4f getOrthographicMatrix() {
		return orthographicMatrix;
	}

	public Matrix4f updateProjectionMatrix() {
		/*Matrix4f projectionMatrix = new Matrix4f();
		float aspectRatio = (float) 1280 / (float) 720;
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))));
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;

		projectionMatrix.m00(x_scale);
		projectionMatrix.m11(y_scale);
		projectionMatrix.m22(-((FAR_PLANE + NEAR_PLANE) / frustum_length));
		projectionMatrix.m23(-1);
		projectionMatrix.m32(-((2 * NEAR_PLANE * FAR_PLANE) / frustum_length));
		projectionMatrix.m33(0);

		return projectionMatrix;*/
		float aspectRatio = (float) (getWidth() / getHeight());
		return projectionMatrix.setPerspective(FOV, aspectRatio, NEAR_PLANE, FAR_PLANE);
	}
	
	public Matrix4f customProjectionMatrix(float near, float far) {
		float aspectRatio = (float) (getWidth() / getHeight());
		return projectionMatrix.setPerspective(FOV, aspectRatio, near, far);
	}

	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}
	
	/**
	 * Updates the "view" matrix of the light. This creates a view matrix which
	 * will line up the direction of the "view cuboid" with the direction of the
	 * light. The light itself has no position, so the "view" matrix is centered
	 * at the center of the "view cuboid". The created view matrix determines
	 * where and how the "view cuboid" is positioned in the world. The size of
	 * the view cuboid, however, is determined by the projection matrix.
	 * 
	 * @param direction
	 *            - the light direction, and therefore the direction that the
	 *            "view cuboid" should be pointing.
	 * @param center
	 *            - the center of the "view cuboid" in world space.
	 */
	public void updateLightViewMatrix(Vector3f direction, Vector3f center) {
		direction.normalize();
		center.negate();
		lightViewMatrix.identity();
		float pitch = (float) Math.acos(new Vector2f(direction.x, direction.z).length());
		lightViewMatrix.rotate(pitch, new Vector3f(1, 0, 0));
		float yaw = (float) Math.toDegrees(((float) Math.atan(Float.isNaN(direction.x / direction.z) ? 0f : direction.x / direction.z)));
		yaw = direction.z > 0 ? yaw - 180 : yaw;
		lightViewMatrix.rotate((float) -Math.toRadians(yaw), new Vector3f(0, 1, 0));
		lightViewMatrix.translate(center);
	}
	
	public Matrix4f getLightViewMatrix() {
		return lightViewMatrix;
	}
	
	/**
	 * TODO Move this somewhere else
	 * Create the offset for part of the conversion to shadow map space. This
	 * conversion is necessary to convert from one coordinate system to the
	 * coordinate system that we can use to sample to shadow map.
	 * 
	 * @return The offset as a matrix (so that it's easy to apply to other matrices).
	 */
	private Matrix4f createOffset() {
		Matrix4f offset = new Matrix4f();
		offset.translate(new Vector3f(0.5f, 0.5f, 0.5f));
		offset.scale(new Vector3f(0.5f, 0.5f, 0.5f));
		return offset;
	}
	
	public Matrix4f getOffsetMatrix() {
		return offsetMatrix;
	}

}
