package com.avogine.junkyard.scene.entity.light;

import org.joml.Vector3f;

import com.avogine.junkyard.scene.Cast;
import com.avogine.junkyard.scene.entity.EntityComponent;
import com.avogine.junkyard.scene.entity.event.EntityEvent;

public class PointLight extends EntityComponent {

	private Vector3f color;
	private float intensity;
	private Attenuation attenuation;
	
	public PointLight() {
		this(Cast.DUMMY_LIGHT_ID, new Vector3f(), 0f);
	}

	public PointLight(int entity, Vector3f color, float intensity) {
		this(entity, color, intensity, new Attenuation(1, 0, 0));
	}
	
	public PointLight(int entity, Vector3f color, float intensity, Attenuation attenuation) {
		super(entity);
		this.color = color;
		this.intensity = intensity;
		this.attenuation = attenuation;
	}	
	
	public PointLight(PointLight pointLight) {
		this(pointLight.getEntityID(), new Vector3f(pointLight.getColor()), pointLight.getIntensity(), pointLight.getAttenuation());
	}

	public void update() {
		
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

	public float getIntensity() {
		return intensity;
	}

	public void setIntensity(float intensity) {
		this.intensity = intensity;
	}

	public Attenuation getAttenuation() {
		return attenuation;
	}

	public void setAttenuation(Attenuation attenuation) {
		this.attenuation = attenuation;
	}

	public static class Attenuation {

		private float constant;
		private float linear;
		private float exponent;

		public Attenuation(float constant, float linear, float exponent) {
			this.constant = constant;
			this.linear = linear;
			this.exponent = exponent;
		}

		public float getConstant() {
			return constant;
		}

		public void setConstant(float constant) {
			this.constant = constant;
		}

		public float getLinear() {
			return linear;
		}

		public void setLinear(float linear) {
			this.linear = linear;
		}

		public float getExponent() {
			return exponent;
		}

		public void setExponent(float exponent) {
			this.exponent = exponent;
		}
	}

}
