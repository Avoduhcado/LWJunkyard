package com.avogine.junkyard.io.event;

public class MouseMotionInputEvent {

	public static final int MOUSE_MOVED = 1;
	
	public static final int MOUSE_DRAGGED = 2;
	
	private final int eventType;
	private final double x;
	private final double y;
	private final double dx;
	private final double dy;
	
	public MouseMotionInputEvent(int eventType, double x, double y, double dx, double dy) {
		this.eventType = eventType;
		this.x = x;
		this.y = y;
		this.dx = dx;
		this.dy = dy;
	}
	
	public int getEventType() {
		return eventType;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double getDx() {
		return dx;
	}
	
	public double getDy() {
		return dy;
	}
	
}
