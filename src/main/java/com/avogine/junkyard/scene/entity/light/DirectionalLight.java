package com.avogine.junkyard.scene.entity.light;

import org.joml.Vector3f;

import com.avogine.junkyard.Theater;
import com.avogine.junkyard.scene.entity.EntityComponent;
import com.avogine.junkyard.scene.entity.event.EntityEvent;

public class DirectionalLight extends EntityComponent {

	private Vector3f color;
	private Vector3f direction;
	private float intensity;
	
	private double lightAngle;
	
	public DirectionalLight(int entity, Vector3f color, Vector3f direction, float intensity) {
		super(entity);
		this.color = color;
        this.direction = direction;
        this.intensity = intensity;
	}
	
	public DirectionalLight(DirectionalLight light) {
		this(light.getEntityID(), light.getColor(), light.getDirection(), light.getIntensity());
	}
	
	public void update() {
		// TODO Put this in a time listener yo
		lightAngle += Theater.getDelta() * 10;
		if (lightAngle > 90) {
			setIntensity(0);
			if (lightAngle >= 360) {
				lightAngle = 0;
			}
		} else if (lightAngle <= -80 || lightAngle >= 80) {
			float factor = 1 - (float)(Math.abs(lightAngle) - 80) / 10.0f;
			setIntensity(factor);
			getColor().y = Math.max(factor, 0.9f);
			getColor().z = Math.max(factor, 0.5f);
		} else {
			setIntensity(1);
			getColor().x = 1;
			getColor().y = 1;
			getColor().z = 1;
		}
		double angRad = Math.toRadians(lightAngle);
		getDirection().x = (float) Math.sin(angRad);
		getDirection().y = (float) Math.cos(angRad);
	}
	
	@Override
	public void fireEvent(EntityEvent event) {
		// TODO Auto-generated method stub
		
	}
	
	public Vector3f getColor() {
		return color;
	}
	
	public void setColor(Vector3f color) {
		this.color = color;
	}
	
	public Vector3f getDirection() {
		return direction;
	}
	
	public void setDirection(Vector3f direction) {
		this.direction = direction;
	}
	
	public float getIntensity() {
		return intensity;
	}
	
	public void setIntensity(float intensity) {
		this.intensity = intensity;
	}
	
}
