package com.avogine.junkyard.scene.render.shaders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.avogine.junkyard.scene.render.shaders.util.DirectionalLightStruct;
import com.avogine.junkyard.scene.render.shaders.util.MaterialStruct;
import com.avogine.junkyard.scene.render.shaders.util.PointLightStruct;
import com.avogine.junkyard.scene.render.shaders.util.ShaderProgram;
import com.avogine.junkyard.scene.render.shaders.util.SpotLightStruct;
import com.avogine.junkyard.scene.render.shaders.util.Uniform;
import com.avogine.junkyard.scene.render.shaders.util.UniformFloat;
import com.avogine.junkyard.scene.render.shaders.util.UniformMat4Array;
import com.avogine.junkyard.scene.render.shaders.util.UniformMatrix;
import com.avogine.junkyard.scene.render.shaders.util.UniformSampler;
import com.avogine.junkyard.scene.render.shaders.util.UniformVec3;
import com.avogine.junkyard.scene.render.util.RenderConstants;

public class ComplexLightingShader extends ShaderProgram {

	public UniformSampler colorTexture = new UniformSampler("colorTexture");
	public UniformSampler shadowMapTexture = new UniformSampler("shadowMap");
	public UniformMatrix model = new UniformMatrix("model");
	public UniformMatrix view = new UniformMatrix("view");
	public UniformMatrix projection = new UniformMatrix("projection");
	public UniformMatrix modelLightView = new UniformMatrix("modelLightView");
	public UniformMatrix orthographic = new UniformMatrix("orthographic");
	public UniformVec3 ambientLight = new UniformVec3("ambientLight");
	public UniformFloat specularPower = new UniformFloat("specularPower");
	public UniformMat4Array jointsMatrix = new UniformMat4Array("jointsMatrix", RenderConstants.MAX_JOINTS);
	public PointLightStruct[] pointLights = new PointLightStruct[RenderConstants.MAX_POINT_LIGHTS];
	public SpotLightStruct[] spotLights = new SpotLightStruct[RenderConstants.MAX_SPOT_LIGHTS];
	public MaterialStruct material = new MaterialStruct("material");
	public DirectionalLightStruct directionalLight = new DirectionalLightStruct("directionalLight");
	
	public ComplexLightingShader(String vertexFile, String fragmentFile, String...inVariables) {
		super(vertexFile, fragmentFile, inVariables);
		
		// TODO UniformStructArray type?
		for(int i = 0; i < pointLights.length; i++) {
			pointLights[i] = new PointLightStruct("pointLights[" + i + "]");
		}
		for(int i = 0; i < spotLights.length; i++) {
			spotLights[i] = new SpotLightStruct("spotLights[" + i + "]");
		}
		
		List<Uniform> uniforms = new ArrayList<>(Arrays.asList(colorTexture, shadowMapTexture, model, view, projection, modelLightView, orthographic, ambientLight, specularPower, jointsMatrix));
		for(PointLightStruct pointLight : pointLights) {
			uniforms.addAll(pointLight.getAllUniforms());
		}
		for(SpotLightStruct spotLight : spotLights) {
			uniforms.addAll(spotLight.getAllUniforms());
		}
		uniforms.addAll(material.getAllUniforms());
		uniforms.addAll(directionalLight.getAllUniforms());
		storeAllUniformLocations(uniforms.stream().toArray(Uniform[]::new));
		connectTextureUnits();
	}
	
	protected void connectTextureUnits() {
		super.start();
		colorTexture.loadTexUnit(0);
		shadowMapTexture.loadTexUnit(1);
		super.stop();
	}

}
