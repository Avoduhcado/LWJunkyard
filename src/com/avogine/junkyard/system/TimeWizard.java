package com.avogine.junkyard.system;

import java.util.ArrayList;
import java.util.List;

import com.avogine.junkyard.io.event.TimeEvent;
import com.avogine.junkyard.io.event.TimeListener;

public class TimeWizard {

private static List<TimeListener> listeners = new ArrayList<>();
	
	public static List<TimeListener> getListeners() {
		return listeners;
	}
	
	public static synchronized void addListener(TimeListener l) {
		listeners.add(l);
	}
	
	public static synchronized void removeListener(TimeListener l) {
		listeners.remove(l);
	}
	
	public static void fireEvent(TimeEvent e) {
		listeners.stream()
			.forEach(t -> t.timePassed(e));
	}
	
}
