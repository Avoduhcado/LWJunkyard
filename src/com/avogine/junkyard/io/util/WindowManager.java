package com.avogine.junkyard.io.util;

import java.util.Collection;
import java.util.HashMap;

import org.lwjgl.glfw.GLFW;

import com.avogine.junkyard.io.Window;

public class WindowManager {

	public static final int WIDTH = 1280;
	public static final int HEIGHT = 720;
	private static final String TITLE = "LWJunkyard";
	
	private static long windowInFocus;
	private static HashMap<Long, Window> windows = new HashMap<>();
	
	public static long requestNewWindow(int width, int height, String title) {
		Window window = new Window(width, height, title);
		window.createWindow();
		if(windows.isEmpty()) {
			windowInFocus = window.getID();
		}
		windows.put(window.getID(), window);
		
		return window.getID();
	}	
	
	public static long requestNewWindow() {
		return requestNewWindow(WIDTH, HEIGHT, TITLE);
	}
	
	public static Window requestWindow(long ID) {
		if(!windows.containsKey(ID)) {
			return null;
		}
		
		return windows.get(ID);
	}
	
	public static void removeWindow(long ID) {
		windows.remove(ID);
		
		// Select a new focus window if the current focus is removed
		if(ID == windowInFocus) {
			if(windows.isEmpty()) {
				windowInFocus = -1L;
			} else {
				windowInFocus = windows.keySet().iterator().next();
			}
		}
	}
	
	public static Window getWindowInFocus() {
		return windows.get(windowInFocus);
	}
	
	public static void setWindowInFocus(long ID) {
		windowInFocus = ID;
		GLFW.glfwFocusWindow(windowInFocus);
	}
	
	public static Collection<Window> getWindows() {
		return windows.values();
	}
	
}
