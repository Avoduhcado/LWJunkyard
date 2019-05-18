package com.avogine.junkyard.io.event;

public class TimeEvent {
	
	private final double delta;
	
	public TimeEvent(double delta) {
		this.delta = delta;
	}
	
	public double getDelta() {
		return delta;
	}
	
}
