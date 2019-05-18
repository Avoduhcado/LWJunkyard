package com.avogine.junkyard.io;

import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;

import com.avogine.junkyard.io.event.InputListener;
import com.avogine.junkyard.io.event.KeyInputEvent;
import com.avogine.junkyard.io.event.KeyInputListener;
import com.avogine.junkyard.io.event.MouseMotionInputEvent;
import com.avogine.junkyard.io.event.MouseMotionInputListener;
import com.avogine.junkyard.scene.Cast;
import com.avogine.junkyard.scene.entity.Body;
import com.avogine.junkyard.scene.entity.event.SpeedChangeEvent;
import com.avogine.junkyard.window.Window;
import com.bulletphysics.linearmath.DebugDrawModes;

public class Input {

	private long windowID;
	
	private boolean[] keys;

	private double mouseX;
	private double mouseY;
	private double mouseDX;
	private double mouseDY;
	private double mouseWheelDX;
	private double mouseWheelDY;

	private List<InputListener> inputListeners = new ArrayList<>();
	
	public Input(Window window) {
		windowID = window.getID();
		keys = new boolean[GLFW.GLFW_KEY_LAST];
		Arrays.fill(keys, false);
		
		GLFW.glfwSetKeyCallback(windowID, (w, key, scancode, action, mods) -> {
			switch(key) {
			case GLFW.GLFW_KEY_F1:
				if(action == GLFW.GLFW_RELEASE) {
					if(GL11.glGetInteger(GL11.GL_POLYGON_MODE) == GL11.GL_LINE) {
						GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
					} else {
						GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
					}
				}
				break;
			case GLFW.GLFW_KEY_F3:
				if(action == GLFW.GLFW_RELEASE) {
					if(window.getStage().getPhysics().getWorld().getDebugDrawer().getDebugMode() > 0) {
						window.getStage().getPhysics().getWorld().getDebugDrawer().setDebugMode(DebugDrawModes.NO_DEBUG);
					} else {
						window.getStage().getPhysics().getWorld().getDebugDrawer().setDebugMode(DebugDrawModes.DRAW_WIREFRAME);
					}
				}
				break;
			case GLFW.GLFW_KEY_F4:
				if(action == GLFW.GLFW_RELEASE) {
					window.getMusicBox().enumerateDevices();
				}
				break;
			case GLFW.GLFW_KEY_ESCAPE:
				GLFW.glfwSetInputMode(w, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
				break;
			default:
				keys[key] = action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT ? true : false;
			}
			
			fireKeyInput(new KeyInputEvent(getKeyEventType(action), key));
		});
		
		GLFW.glfwSetMouseButtonCallback(windowID, (w, button, action, mods) -> {
			switch(button) {
			case GLFW.GLFW_MOUSE_BUTTON_1:
				if(action != GLFW.GLFW_PRESS) {
					return;
				}
				try(MemoryStack stack = MemoryStack.stackPush()) {
					DoubleBuffer cursorX = stack.mallocDouble(1);
					DoubleBuffer cursorY = stack.mallocDouble(1);

					// Center the window
					GLFW.glfwGetCursorPos(w, cursorX, cursorY);
					mouseX = cursorX.get();
					mouseY = cursorY.get();
				}
				GLFW.glfwSetInputMode(w, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
				break;
			case GLFW.GLFW_MOUSE_BUTTON_2:
				GLFW.glfwSetInputMode(w, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
				break;
			}
		});
		
		GLFW.glfwSetCursorPosCallback(windowID, (w, x, y) -> {
			if(GLFW.glfwGetInputMode(w, GLFW.GLFW_CURSOR) != GLFW.GLFW_CURSOR_DISABLED) {
				return;
			}
			fireMouseMotionEvent(new MouseMotionInputEvent(
					GLFW.glfwGetMouseButton(windowID, 0) == GLFW.GLFW_PRESS ? MouseMotionInputEvent.MOUSE_DRAGGED : MouseMotionInputEvent.MOUSE_MOVED,
					x, y, mouseX - x, mouseY - y));
			// TODO Numbers are wrong here
			// I don't remember what that means
			mouseDX = x - mouseX;
			mouseDY = y - mouseY;
			mouseX = x;
			mouseY = y;
		});
		
		GLFW.glfwSetScrollCallback(windowID, (w, xOffset, yOffset) -> {
			// TODO Put this in a proper event firer
			Cast cast = window.getStage().getCast();
			cast.getComponent(Cast.CAMERA_ID, Body.class).ifPresent(body -> body.fireEvent(new SpeedChangeEvent(SpeedChangeEvent.ADD_SPEED, (float) yOffset)));
		});
	}
	
	public void update() {
		// Key_Repeat has god awful lag, so we're gonna roll our own keyDown events
		for(int i = GLFW.GLFW_KEY_SPACE; i < keys.length; i++) {
			if(isKeyDown(i)) {
				fireKeyInput(new KeyInputEvent(KeyInputEvent.KEY_HELD, i));
			}
		}
		
		mouseDX = 0;
		mouseDY = 0;
		mouseWheelDX = 0;
		mouseWheelDY = 0;
		
		for(int i = GLFW.GLFW_MOUSE_BUTTON_1; i < GLFW.GLFW_MOUSE_BUTTON_LAST; i++) {
			/*if(isMouseButtonDown(i) && !mouseButtonDelays[i]) {
				fireMouseButtonEvent(new MouseButtonInputEvent(MouseButtonInputEvent.MOUSE_HELD, i));
			} else if(isMouseButtonDown(i)) {
				mouseButtonDelays[i] = false;
			}*/
		}
		
	}
	
	public void fireKeyInput(KeyInputEvent event) {
		inputListeners.stream()
			.filter(l -> l instanceof KeyInputListener)
			.map(l -> (KeyInputListener) l)
			.forEach(l -> {
				switch(event.getEventType()) {
				case KeyInputEvent.KEY_PRESS:
					l.keyPressed(event);
					break;
				case KeyInputEvent.KEY_RELEASE:
					l.keyReleased(event);
					break;
				case KeyInputEvent.KEY_HELD:
					l.keyHeld(event);
					break;
				}
			});
	}

	public void fireMouseMotionEvent(MouseMotionInputEvent event) {
		inputListeners.stream()
			.filter(l -> l instanceof MouseMotionInputListener)
			.map(l -> (MouseMotionInputListener) l)
			.forEach(l -> {
				switch(event.getEventType()) {
				case MouseMotionInputEvent.MOUSE_MOVED:
					l.mouseMoved(event);
					break;
				case MouseMotionInputEvent.MOUSE_DRAGGED:
					l.mouseDragged(event);
					break;
				}
			});
	}
	
	public void addInputListener(InputListener l) {
		inputListeners.add(l);
	}
	
	public void removeInputListener(InputListener l) {
		inputListeners.remove(l);
	}
	
	public boolean isKeyDown(int key) {
		return GLFW.glfwGetKey(windowID, key) == GLFW.GLFW_PRESS;
	}

	public boolean isKeyPressed(int key) {
		return isKeyDown(key) && !keys[key];
	}

	public boolean isKeyReleased(int key) {
		return !isKeyDown(key) && keys[key];
	}
	
	public static int getKeyEventType(int action) {
		switch(action) {
		case GLFW.GLFW_PRESS:
			return KeyInputEvent.KEY_PRESS;
		case GLFW.GLFW_RELEASE:
			return KeyInputEvent.KEY_RELEASE;
		case GLFW.GLFW_REPEAT:
			return KeyInputEvent.KEY_HELD;
		}
		return -1;
	}
	
}
