package com.avogine.junkyard.scene.render.shaders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.avogine.junkyard.scene.render.data.ShadowMap;
import com.avogine.junkyard.scene.render.shaders.util.DirectionalLightStruct;
import com.avogine.junkyard.scene.render.shaders.util.MaterialStruct;
import com.avogine.junkyard.scene.render.shaders.util.PointLightStruct;
import com.avogine.junkyard.scene.render.shaders.util.ShaderProgram;
import com.avogine.junkyard.scene.render.shaders.util.SpotLightStruct;
import com.avogine.junkyard.scene.render.shaders.util.Uniform;
import com.avogine.junkyard.scene.render.shaders.util.UniformFloat;
import com.avogine.junkyard.scene.render.shaders.util.UniformInteger;
import com.avogine.junkyard.scene.render.shaders.util.UniformMat4Array;
import com.avogine.junkyard.scene.render.shaders.util.UniformMatrix;
import com.avogine.junkyard.scene.render.shaders.util.UniformSampler;
import com.avogine.junkyard.scene.render.shaders.util.UniformVec3;
import com.avogine.junkyard.scene.render.util.RenderConstants;
import com.avogine.junkyard.window.util.WindowConstants;

// TODO Convert MAX_CONSTS into uniforms and load them at the start

public class SimpleLightingShader extends ShaderProgram {
	
	// Material texture
	public UniformSampler colorTexture = new UniformSampler("colorTexture");
	// Basic positioning matrices
	public UniformMatrix model = new UniformMatrix("model");
	public UniformMatrix view = new UniformMatrix("view");
	public UniformMatrix projection = new UniformMatrix("projection");
	// Background lighting
	public UniformVec3 ambientLight = new UniformVec3("ambientLight");
	public UniformFloat specularPower = new UniformFloat("specularPower");
	// Animation
	public UniformMat4Array jointsMatrix = new UniformMat4Array("jointsMatrix", RenderConstants.MAX_JOINTS);
	// Direct lighting
	public PointLightStruct[] pointLights = new PointLightStruct[RenderConstants.MAX_POINT_LIGHTS];
	public SpotLightStruct[] spotLights = new SpotLightStruct[RenderConstants.MAX_SPOT_LIGHTS];
	public DirectionalLightStruct directionalLight = new DirectionalLightStruct("directionalLight");
	// Model material
	public MaterialStruct material = new MaterialStruct("material");
	// Shadows
	//public UniformSampler shadowMap = new UniformSampler("shadowMap");
	public UniformSampler[] shadowMaps = new UniformSampler[4];
	//public UniformMatrix toShadowMapSpace = new UniformMatrix("toShadowMapSpace");
	public UniformMat4Array shadowSpaceMatrices = new UniformMat4Array("shadowSpaceMatrix", 4);
	public UniformFloat[] cascadeFarPlanes = new UniformFloat[4];
	//public UniformFloat shadowDistance = new UniformFloat("shadowDistance");
	public UniformInteger shadowMapSize = new UniformInteger("shadowMapSize");
	
	public SimpleLightingShader(String vertexFile, String fragmentFile, String...inVariables) {
		super(vertexFile, fragmentFile, inVariables);
		
		// TODO UniformStructArray type?
		for(int i = 0; i < pointLights.length; i++) {
			pointLights[i] = new PointLightStruct("pointLights[" + i + "]");
		}
		for(int i = 0; i < spotLights.length; i++) {
			spotLights[i] = new SpotLightStruct("spotLights[" + i + "]");
		}
		
		for(int i = 0; i < shadowMaps.length; i++) {
			shadowMaps[i] = new UniformSampler("shadowMaps[" + i + "]");
		}
		for(int i = 0; i < cascadeFarPlanes.length; i++) {
			cascadeFarPlanes[i] = new UniformFloat("cascadeFarPlanes[" + i + "]");
		}
		
		List<Uniform> uniforms = new ArrayList<>(Arrays.asList(colorTexture, model, view, projection, ambientLight, specularPower, jointsMatrix, shadowSpaceMatrices, shadowMapSize));
		for(PointLightStruct pointLight : pointLights) {
			uniforms.addAll(pointLight.getAllUniforms());
		}
		for(SpotLightStruct spotLight : spotLights) {
			uniforms.addAll(spotLight.getAllUniforms());
		}
		// Just implement a custom storeUniformLocation method in these structs durr
		uniforms.addAll(Arrays.asList(cascadeFarPlanes));
		uniforms.addAll(Arrays.asList(shadowMaps));
		uniforms.addAll(directionalLight.getAllUniforms());
		uniforms.addAll(material.getAllUniforms());
		storeAllUniformLocations(uniforms.stream().toArray(Uniform[]::new));
		connectTextureUnits();
		loadShadowConstants();
	}
	
	protected void connectTextureUnits() {
		super.start();
		colorTexture.loadTexUnit(0);
		//shadowMap.loadTexUnit(1);
		for(int i = 0; i < shadowMaps.length; i++) {
			shadowMaps[i].loadTexUnit(1 + i);
		}
		super.stop();
	}
	
	protected void loadShadowConstants() {
		super.start();
		//shadowDistance.loadFloat(ShadowBox.SHADOW_DISTANCE);
		//shadowDistance.loadFloat(Window.FAR_PLANE / 30.0f);
		// TODO
		shadowMapSize.loadInteger(ShadowMap.TEXTURE_SIZE);
		cascadeFarPlanes[0].loadFloat(WindowConstants.DEFAULT_FAR_PLANE / 30.0f);
		cascadeFarPlanes[1].loadFloat(WindowConstants.DEFAULT_FAR_PLANE / 20.0f);
		cascadeFarPlanes[2].loadFloat(WindowConstants.DEFAULT_FAR_PLANE / 10.0f);
		cascadeFarPlanes[3].loadFloat(WindowConstants.DEFAULT_FAR_PLANE);
		super.stop();
	}

}
