package com.avogine.junkyard.scene.render.shaders.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.joml.Vector3f;

import com.avogine.junkyard.scene.entity.light.PointLight;
import com.avogine.junkyard.scene.entity.light.PointLight.Attenuation;

public class PointLightStruct extends UniformStruct {

	protected UniformVec3 color;
	protected UniformVec3 position;
	protected UniformFloat intensity;
	protected UniformFloat attenuationConstant;
	protected UniformFloat attenuationLinear;
	protected UniformFloat attenuationExponent;

	public PointLightStruct(String name) {
		color = new UniformVec3(name + ".color");
		position = new UniformVec3(name + ".position");
		intensity = new UniformFloat(name + ".intensity");
		attenuationConstant = new UniformFloat(name + ".att.constant");
		attenuationLinear = new UniformFloat(name + ".att.linear");
		attenuationExponent = new UniformFloat(name + ".att.exponent");
	}
	
	public void loadPointLight(PointLight pointLight, Vector3f lightPosition) {
		color.loadVec3(pointLight.getColor());
		position.loadVec3(lightPosition);
		intensity.loadFloat(pointLight.getIntensity());
		Attenuation att = pointLight.getAttenuation();
		attenuationConstant.loadFloat(att.getConstant());
		attenuationLinear.loadFloat(att.getLinear());
		attenuationExponent.loadFloat(att.getExponent());
	}
	
	@Override
	public List<Uniform> getAllUniforms() {
		return new ArrayList<>(Arrays.asList(color, position, intensity, attenuationConstant, attenuationLinear, attenuationExponent));
	}

}
