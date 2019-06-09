package com.avogine.junkyard.window;

import java.nio.IntBuffer;

import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.system.MemoryStack;

import com.avogine.junkyard.io.Input;
import com.avogine.junkyard.io.MusicBox;
import com.avogine.junkyard.io.util.WindowManager;
import com.avogine.junkyard.memory.MemoryManaged;
import com.avogine.junkyard.scene.Scene;
import com.avogine.junkyard.scene.Stage;
import com.avogine.junkyard.system.AvoEventQueue;
import com.avogine.junkyard.util.MathUtils;
import com.avogine.junkyard.window.util.WindowConstants;

public class Window implements MemoryManaged {

	private float fov;
	private float nearPlane;
	private float farPlane;
	
	private int refreshRate;
	private int unfocusedRefreshRate;

	private long ID;

	private int width;
	private int height;
	private String title;

	private boolean hasFocus = true;

	private Matrix4f projectionMatrix;

	private Input input;
	private MusicBox musicBox;
	private Scene scene;

	public Window(int width, int height, String title) {
		this.width = width;
		this.height = height;
		this.title = title;
		
		fov = WindowConstants.DEFAULT_FOV;
		nearPlane = WindowConstants.DEFAULT_NEAR_PLANE;
		farPlane = WindowConstants.DEFAULT_FAR_PLANE;
		
		refreshRate = WindowConstants.DEFAULT_REFRESH_RATE;
		unfocusedRefreshRate = WindowConstants.DEFAULT_UNFOCUSED_REFRESH_RATE;
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

		input = new Input(this);
		musicBox = new MusicBox(this);
		scene = new Stage(this);

		GLFW.glfwShowWindow(ID);
	}

	public void render() {
		if(GLFW.glfwGetCurrentContext() != ID) {
			GLFW.glfwMakeContextCurrent(ID);
		}

		//GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		scene.render();

		GLFW.glfwSwapBuffers(ID);
	}

	public void update() {
		input.update();
		scene.update();

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
		// TODO Change cleanup code to happen in some listener somewhere and register on instantiating the object
		scene.cleanUp();
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

	public Scene getScene() {
		return scene;
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

	public Matrix4f updateProjectionMatrix() {
		float aspectRatio = (float) (getWidth() / getHeight());
		return projectionMatrix.setPerspective(fov, aspectRatio, nearPlane, farPlane);
	}
	
	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}
	
	public float getFov() {
		return fov;
	}
	
	public void setFov(float fov) {
		this.fov = fov;
	}
	
	public float getNearPlane() {
		return nearPlane;
	}
	
	public void setNearPlane(float nearPlane) {
		this.nearPlane = nearPlane;
	}
	
	public float getFarPlane() {
		return farPlane;
	}
	
	public void setFarPlane(float farPlane) {
		this.farPlane = farPlane;
	}

}
