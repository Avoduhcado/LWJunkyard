package com.avogine.junkyard.memory;

import com.avogine.junkyard.annotations.CleanMeUp;

@CleanMeUp
public interface MemoryManaged {

	public void cleanUp();
	
}
