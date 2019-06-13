package com.avogine.junkyard.scene.render.shaders.util;

import org.lwjgl.opengl.GL20;

public abstract class Uniform {
	
	public static final int NOT_FOUND = -1;
	
	private String name;
	private int location;
	
	protected Uniform(String name){
		this.name = name;
	}
	
	protected void storeUniformLocation(int programID){
		location = GL20.glGetUniformLocation(programID, name);
		if(location == NOT_FOUND){
			System.err.println("No uniform variable called " + name + " found!");
		}
	}
	
	protected int getLocation(){
		return location;
	}
	
	@Override
	public String toString() {
		return name;
	}

}
