package com.avogine.junkyard.system;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AvoEventQueue {

	private static Queue<Runnable> events = new ConcurrentLinkedQueue<>();
	
	public static void doLater(Runnable run) {
		events.add(run);
	}
	
	public static void processEvents() {
		for(Iterator<Runnable> iter = events.iterator(); iter.hasNext();) {
			Runnable r = iter.next();
			r.run();
			iter.remove();
		}
	}
	
}
