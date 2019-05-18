package com.avogine.junkyard;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

import com.avogine.junkyard.io.event.TimeEvent;
import com.avogine.junkyard.io.util.WindowManager;
import com.avogine.junkyard.memory.MemoryManaged;
import com.avogine.junkyard.system.AvoEventQueue;
import com.avogine.junkyard.system.TimeWizard;
import com.avogine.junkyard.window.Window;

public class Theater implements MemoryManaged {

	private static final long ONE_MILLION = 1000000L;
	private static final double ONE_THOUSAND = 1000.0;
	
	private static double currentTime;
	private static double lastTime;
	private static double frameTime;
	private static double delta;
	
	private static int fps;
	
	private double refreshRate = 60;
	private double frameLag = 0.0;
	private long milliSleep;
	private int nanoSleep;
			
	public Theater() {
		GLFWErrorCallback.createPrint(System.err).set();
		
		if(!GLFW.glfwInit()) {
			throw new IllegalStateException("Failed to initialize GLFW!");
		}
		
		/*try {
			AvoLog.setup();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
				
		WindowManager.requestNewWindow();
	}
	
	private void play() {
		lastTime = getTime();
		//LOGGER.setLevel(Level.INFO);
		//LOGGER.info("Time: " + lastTime);
		System.out.println("Time: " + lastTime);
		
		while(!WindowManager.getWindows().isEmpty()) {
			doFps();

			WindowManager.getWindows().stream()
				.forEach(w -> {
					w.update();
					w.render();
				});
			
			doSync();
			doLater();
		}
		
		cleanUp();
	}
	
	private void doFps() {
		currentTime = getTime();
		// TODO Warn of slow frame time?
		/*delta = MathUtils.clamp(currentTime - lastTime, 0, 0.0667);
		if(delta >= 0.0667) {
			System.out.println("We slow boys");
		}*/
		delta = currentTime - lastTime;
		TimeWizard.fireEvent(new TimeEvent(delta));
		lastTime = currentTime;
		frameTime += delta;
		if(frameTime >= 1.0) {
			// TODO: Put this in a time listener on the window itself?
			WindowManager.getWindowInFocus().setTitle(String.format("%s fps:%d", WindowManager.getWindowInFocus().getTitle(), fps));
			fps = 0;
			frameTime = 0;
			refreshRate = WindowManager.getWindowInFocus().getRefreshRate();
		} else {
			fps++;
		}
	}
	
	private void doSync() {
		// Get the frame difference from what we're currently displaying and what we should be displaying based on target refresh rate and progress through the current second
		frameLag = fps - (refreshRate * frameTime);
		// Don't sleep if we're already behind
		if(frameLag < 0) {
			return;
		}
		try {
			// Simple calculation to get flat milliseconds to sleep
			milliSleep = (long) (ONE_THOUSAND / (refreshRate - frameLag));
			// Get the decimal value from the total sleep time up to the 6th place
			nanoSleep = (int) (((ONE_THOUSAND / (refreshRate - frameLag)) * ONE_MILLION) - (milliSleep * ONE_MILLION));
			Thread.sleep(milliSleep, nanoSleep);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Process events after all game loop interaction has ceased for the frame.
	 */
	private void doLater() {
		AvoEventQueue.processEvents();
	}
	
	@Override
	public void cleanUp() {
		WindowManager.getWindows().stream()
			.forEach(Window::cleanUp);
		
		GLFW.glfwTerminate();
	}
	
	public static float getDeltaChange(float value) {
		return (float) (delta * value);
	}
	
	public static double getDelta() {
		return delta;
	}
	
	private static double getTime() {
		return GLFW.glfwGetTime();
	}
	
	public static void main(String[] args) {
		Theater theater = new Theater();
		theater.play();
	}
	
}
