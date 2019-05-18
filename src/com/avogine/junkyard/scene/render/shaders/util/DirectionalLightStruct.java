package com.avogine.junkyard.scene.render.shaders.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.avogine.junkyard.scene.entity.light.DirectionalLight;

public class DirectionalLightStruct extends UniformStruct {

	protected UniformVec3 color;
	protected UniformVec3 direction;
	protected UniformFloat intensity;
	
	public DirectionalLightStruct(String name) {
		color = new UniformVec3(name + ".color");
		direction = new UniformVec3(name + ".direction");
		intensity = new UniformFloat(name + ".intensity");
	}
	
	public void loadDirectionalLight(DirectionalLight light) {
		color.loadVec3(light.getColor());
		direction.loadVec3(light.getDirection());
		intensity.loadFloat(light.getIntensity());
	}
	
	@Override
	public List<Uniform> getAllUniforms() {
		return new ArrayList<>(Arrays.asList(color, direction, intensity));
	}

}
