package com.avogine.junkyard.io.event;

public class MouseScrollInputEvent {
	
	private final double xOffset;
	private final double yOffset;
	
	public MouseScrollInputEvent(double xOffset, double yOffset) {
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}
	
	public double getxOffset() {
		return xOffset;
	}
	
	public double getyOffset() {
		return yOffset;
	}

}
