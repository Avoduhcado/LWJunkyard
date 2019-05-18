package com.avogine.junkyard.scene.render.shaders.util;

import org.lwjgl.opengl.GL20;

public class UniformInteger extends Uniform {

	private int currentValue;
	private boolean used = false;
	
	public UniformInteger(String name) {
		super(name);
	}
	
	public void loadInteger(int value){
		if(!used || currentValue != value){
			GL20.glUniform1i(super.getLocation(), value);
			used = true;
			currentValue = value;
		}
	}

}
