package com.avogine.junkyard.scene.render.shaders.util;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lwjgl.glfw.GLFW;

import com.avogine.junkyard.scene.render.shaders.DepthShader;
import com.avogine.junkyard.scene.render.shaders.SimpleFontShader;
import com.avogine.junkyard.scene.render.shaders.SimpleLightingShader;
import com.avogine.junkyard.scene.render.shaders.SimpleOrthoShader;
import com.avogine.junkyard.scene.render.shaders.SkyboxShader;
import com.avogine.junkyard.window.TestWindow;

class ShaderCreationTest {
	
	static TestWindow window;
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		window = new TestWindow();
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		GLFW.glfwTerminate();
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void simpleShaderTest() throws IllegalArgumentException, IllegalAccessException {
		SimpleLightingShader simpleLightingShader = new SimpleLightingShader("simpleLightingVertex.glsl", "simpleLightingFragment.glsl", "position", "textureCoords", "normal", "weights", "jointIndices");
		List<Field> fields = Arrays.asList(SimpleLightingShader.class.getDeclaredFields());

		testUniforms(fields, simpleLightingShader);
		testUniformStructs(fields, simpleLightingShader);
	}
	
	@Test
	void skyboxShaderTest() throws IllegalArgumentException, IllegalAccessException {
		SkyboxShader skyboxShader = new SkyboxShader("skyboxVertex.glsl", "skyboxFragment.glsl", "position");
		List<Field> fields = Arrays.asList(SkyboxShader.class.getDeclaredFields());
		
		testUniforms(fields, skyboxShader);
		testUniformStructs(fields, skyboxShader);
	}
	
	@Test
	void depthShaderTest() throws IllegalArgumentException, IllegalAccessException {
		DepthShader depthShader = new DepthShader("depthVertex.glsl", "depthFragment.glsl", "position", "textureCoords", "normal", "weights", "jointIndices");
		List<Field> fields = Arrays.asList(DepthShader.class.getDeclaredFields());
		
		testUniforms(fields, depthShader);
		testUniformStructs(fields, depthShader);
	}
	
	@Test
	void fontShaderTest() throws IllegalArgumentException, IllegalAccessException {
		SimpleFontShader fontShader = new SimpleFontShader("simpleFontVertex.glsl", "simpleFontFragment.glsl", "position", "textureCoords", "normal");
		List<Field> fields = Arrays.asList(SimpleFontShader.class.getDeclaredFields());
		
		testUniforms(fields, fontShader);
		testUniformStructs(fields, fontShader);
	}
	
	@Test
	void guiShaderTest() throws IllegalArgumentException, IllegalAccessException {
		SimpleOrthoShader guiShader = new SimpleOrthoShader("simpleOrthoVertex.glsl", "simpleOrthoFragment.glsl", "position", "textureCoords");
		List<Field> fields = Arrays.asList(SimpleOrthoShader.class.getDeclaredFields());
		
		testUniforms(fields, guiShader);
		testUniformStructs(fields, guiShader);
	}
	
	private void testUniforms(List<Field> fields, ShaderProgram shaderProgram) {
		fields.stream()
			.filter(field -> Uniform.class.isAssignableFrom(field.getType()))
			.map(field -> {
				try {
					return field.get(shaderProgram);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
				return null;
			})
			.map(Uniform.class::cast)
			.forEach(uniform -> assertNotEquals(Uniform.NOT_FOUND, uniform.getLocation(), "Uniform: " + uniform + " was not properly loaded into shader."));
	}
	
	private void testUniformStructs(List<Field> fields, ShaderProgram shaderProgram) {
		fields.stream()
			.filter(field -> UniformStruct.class.isAssignableFrom(field.getType()))
			.map(field -> {
				try {
					return field.get(shaderProgram);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
				return null;
			})
			.map(UniformStruct.class::cast)
			.forEach(struct -> struct.getAllUniforms()
					.stream()
					.forEach(uniform -> assertNotEquals(Uniform.NOT_FOUND, uniform.getLocation(), "Uniform: " + uniform + " was not properly loaded into shader.")));
	}

}
