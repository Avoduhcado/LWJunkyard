package com.avogine.junkyard.scene.entity.light;

import org.joml.Vector3f;

import com.avogine.junkyard.Theater;
import com.avogine.junkyard.scene.entity.EntityComponent;
import com.avogine.junkyard.scene.entity.event.EntityEvent;
import com.avogine.junkyard.util.MathUtils;

public class SpotLight extends EntityComponent {

	private PointLight pointLight;
	private Vector3f coneDirection;
	private float cutOff;
	
	// XXX
	private float spotInc = 1f;
	private float spotAngle = 0.0f;

	public SpotLight() {
		this(new PointLight(), new Vector3f(), 0);
	}

	public SpotLight(PointLight pointLight, Vector3f coneDirection, float cutOffAngle) {
		super(pointLight.getEntityID());
		this.pointLight = pointLight;
		this.coneDirection = coneDirection;
		setCutOffAngle(cutOffAngle);
	}

	public SpotLight(SpotLight spotLight) {
		this(new PointLight(spotLight.getPointLight()), new Vector3f(spotLight.getConeDirection()), 0);
		setCutOff(spotLight.getCutOff());
	}
	
	public void update() {
		// Update spot light direction
		spotAngle = MathUtils.clamp(spotAngle + Theater.getDeltaChange(spotInc), -2, 2);
		spotAngle += Theater.getDeltaChange(spotInc);
		if (spotAngle >= 2) {
			spotInc = -1;
		} else if (spotAngle <= -2) {
			spotInc = 1;
		}
		double spotAngleRad = Math.toRadians(spotAngle);
		coneDirection.z = (float) Math.sin(spotAngleRad);
	}

	@Override
	public void fireEvent(EntityEvent event) {
		// TODO Auto-generated method stub
		
	}

	public PointLight getPointLight() {
		return pointLight;
	}

	public void setPointLight(PointLight pointLight) {
		this.pointLight = pointLight;
	}

	public Vector3f getConeDirection() {
		return coneDirection;
	}

	public void setConeDirection(Vector3f coneDirection) {
		this.coneDirection = coneDirection;
	}

	public float getCutOff() {
		return cutOff;
	}

	public void setCutOff(float cutOff) {
		this.cutOff = cutOff;
	}

	public final void setCutOffAngle(float cutOffAngle) {
		this.setCutOff((float) Math.cos(Math.toRadians(cutOffAngle)));
	}

}
