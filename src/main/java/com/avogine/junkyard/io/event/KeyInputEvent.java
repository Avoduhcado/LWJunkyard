package com.avogine.junkyard.io.event;

public class KeyInputEvent {

	public static final int KEY_PRESS = 1;
	
	public static final int KEY_RELEASE = 2;
	
	public static final int KEY_HELD = 3;
	
	private final int eventType;
	private final int key;
	
	public KeyInputEvent(int eventType, int key) {
		this.eventType = eventType;
		this.key = key;
	}
	
	public int getEventType() {
		return eventType;
	}
	
	public int getKey() {
		return key;
	}
	
}
