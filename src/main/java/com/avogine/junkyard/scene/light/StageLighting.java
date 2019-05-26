package com.avogine.junkyard.scene.light;

import org.joml.Vector3f;

import com.avogine.junkyard.scene.Cast;
import com.avogine.junkyard.scene.entity.light.PointLight;
import com.avogine.junkyard.scene.entity.light.SpotLight;

public class StageLighting {

	private Cast cast;
	private Vector3f ambientLight;
	private float specularPower;
	
	public StageLighting(Cast cast, Vector3f ambientLight, float specularPower) {
		this.cast = cast;
		this.ambientLight = ambientLight;
		this.specularPower = specularPower;
	}
	
	public void doLighting() {
		cast.getEntitiesWithComponent(PointLight.class).stream()
			.map(p -> p.getAs(PointLight.class))
			.forEach(PointLight::update);

		cast.getEntitiesWithComponent(SpotLight.class).stream()
			.map(p -> p.getAs(SpotLight.class))
			.forEach(SpotLight::update);
		
//		cast.getEntitiesWithComponent(DirectionalLight.class).stream()
//			.map(d -> d.getAs(DirectionalLight.class))
//			.forEach(DirectionalLight::update);
	}
	
	public Vector3f getAmbientLight() {
		return ambientLight;
	}
	
	public void setAmbientLight(Vector3f ambientLight) {
		this.ambientLight = ambientLight;
	}
	
	public float getSpecularPower() {
		return specularPower;
	}
	
	public void setSpecularPower(float specularPower) {
		this.specularPower = specularPower;
	}
	
}
