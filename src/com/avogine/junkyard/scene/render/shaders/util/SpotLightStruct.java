package com.avogine.junkyard.scene.render.shaders.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.joml.Vector3f;

import com.avogine.junkyard.scene.entity.light.SpotLight;

public class SpotLightStruct extends UniformStruct {

	protected PointLightStruct pointLight;
	protected UniformVec3 coneDirection;
	protected UniformFloat cutOff;
	
	public SpotLightStruct(String name) {
		pointLight = new PointLightStruct(name + ".pointLight");
		coneDirection = new UniformVec3(name + ".coneDirection");
		cutOff = new UniformFloat(name + ".cutOff");
	}
	
	public void loadSpotLight(SpotLight spotLight, Vector3f lightPosition) {
		pointLight.loadPointLight(spotLight.getPointLight(), lightPosition);
		coneDirection.loadVec3(spotLight.getConeDirection());
		cutOff.loadFloat(spotLight.getCutOff());
	}
	
	@Override
	public List<Uniform> getAllUniforms() {
		List<Uniform> uniforms = new ArrayList<>(Arrays.asList(coneDirection, cutOff));
		uniforms.addAll(pointLight.getAllUniforms());
		return uniforms;
	}

}
