package com.avogine.junkyard.scene.render;

import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import com.avogine.junkyard.Theater;
import com.avogine.junkyard.memory.MemoryManaged;
import com.avogine.junkyard.scene.Camera;
import com.avogine.junkyard.scene.Cast;
import com.avogine.junkyard.scene.ComponentMap;
import com.avogine.junkyard.scene.Stage;
import com.avogine.junkyard.scene.entity.Body;
import com.avogine.junkyard.scene.entity.Model;
import com.avogine.junkyard.scene.entity.light.DirectionalLight;
import com.avogine.junkyard.scene.entity.light.PointLight;
import com.avogine.junkyard.scene.entity.light.SpotLight;
import com.avogine.junkyard.scene.entity.render.Animatable;
import com.avogine.junkyard.scene.light.StageLighting;
import com.avogine.junkyard.scene.render.data.AnimatedFrame;
import com.avogine.junkyard.scene.render.data.Cube;
import com.avogine.junkyard.scene.render.data.Line;
import com.avogine.junkyard.scene.render.data.Mesh;
import com.avogine.junkyard.scene.render.data.RawMesh;
import com.avogine.junkyard.scene.render.data.ShadowBox;
import com.avogine.junkyard.scene.render.data.ShadowMapCascade;
import com.avogine.junkyard.scene.render.load.TextureLoader;
import com.avogine.junkyard.scene.render.shaders.ColorShader;
import com.avogine.junkyard.scene.render.shaders.DepthShader;
import com.avogine.junkyard.scene.render.shaders.SimpleFontShader;
import com.avogine.junkyard.scene.render.shaders.SimpleLightingShader;
import com.avogine.junkyard.scene.render.shaders.SimpleOrthoShader;
import com.avogine.junkyard.scene.render.shaders.SkyboxShader;
import com.avogine.junkyard.scene.render.util.RenderConstants;
import com.avogine.junkyard.scene.render.util.VAO;
import com.avogine.junkyard.scene.text.Text;
import com.avogine.junkyard.scene.util.Transformation;
import com.avogine.junkyard.util.MathUtils;
import com.avogine.junkyard.window.Window;

public class Renderer implements MemoryManaged {

	private Transformation transformation;
	
	private SimpleLightingShader entityShader;
	private SkyboxShader skyboxShader;
	private SimpleFontShader fontShader;
	private ColorShader colorShader;
	private SimpleOrthoShader guiShader;
	private DepthShader depthShader;
	
	private List<ShadowBox> shadowBoxes = new ArrayList<>();
	private ShadowMapCascade shadowMapCascade;
	private RawMesh guiMesh;
	
	// XXX
	private Cube skyboxMesh;
	
	// XXX
	private Text text;
	private float colorLerpTime = 0;
	private int colorLerpSlope = 1;
	private float colorLerpDuration = 2.5f;
	private float textRotation;
	private int textRotationSlope;
	
	private float ocillate = 0;
	private int flip = 1;
	
	public Renderer(Window window) {
		transformation = new Transformation();
		
		setupEntityShader();
		setupSkyboxShader();
		setupTextShader();
		setupDepthShader(window);
		setupGuiShader();
		
		setupLineShader();
	}

	private void setupEntityShader() {
		// Create shader
		entityShader = new SimpleLightingShader("simpleLightingVertex.glsl", "simpleLightingFragment.glsl", "position", "textureCoords", "normal", "weights", "jointIndices");
	}
	
	private void setupSkyboxShader() {
		skyboxShader = new SkyboxShader("skyboxVertex.glsl", "skyboxFragment.glsl", "position");
		
		skyboxMesh = new Cube();
		skyboxMesh.setTexture(TextureLoader.createCubeMap(new String[] {"miramar_rt.tga", "miramar_lf.tga", "miramar_up.tga", "miramar_dn.tga", "miramar_bk.tga", "miramar_ft.tga"}));
	}
	
	private void setupTextShader() {
		fontShader = new SimpleFontShader("simpleFontVertex.glsl", "simpleFontFragment.glsl", "position", "textureCoords", "normal");
		
		text = new Text("Sup!");
	}

	private void setupDepthShader(Window window) {
		depthShader = new DepthShader("depthVertex.glsl", "depthFragment.glsl", "position", "textureCoords", "normal", "weights", "jointIndices");
		
		float[] CASCADE_SPLITS = new float[] {Window.FAR_PLANE / 30.0f, Window.FAR_PLANE / 20.0f, Window.FAR_PLANE / 10.0f, Window.FAR_PLANE};
		float zNear = Window.NEAR_PLANE;
		for(int i = 0; i < CASCADE_SPLITS.length; i++) {
			ShadowBox shadowCascade = new ShadowBox(zNear, CASCADE_SPLITS[i], window);
			shadowBoxes.add(shadowCascade);
			zNear = CASCADE_SPLITS[i];
		}
		shadowMapCascade = new ShadowMapCascade(window, CASCADE_SPLITS.length);
	}
	
	private void setupGuiShader() {
		guiShader = new SimpleOrthoShader("simpleOrthoVertex.glsl", "simpleOrthoFragment.glsl", "position", "textureCoords");
		
		VAO rawVao = VAO.create();
		rawVao.bind(0, 1);
		rawVao.createAttribute(0, new float[] {
				-1, 1,
				1, 1,
				1, -1,
				-1, -1
		}, 2);
		rawVao.createAttribute(1, new float[] {
				0, 0,
				1, 0,
				1, 1,
				0, 1
		}, 2);
		rawVao.createIndexBuffer(new int[] {
				0, 1, 2,
				2, 3, 0
		});
		guiMesh = new RawMesh(rawVao, 2, shadowMapCascade.getTextureIds()[3]);
	}
	
	private void setupLineShader() {
		colorShader = new ColorShader("colorVertex.glsl", "colorFragment.glsl", "position", "colors", "normals");
	}
	
	public void render(Window window, Camera camera, Stage stage) {
		GL11.glViewport(0, 0, window.getWidth(), window.getHeight());

		shadowMapCascade.getFbo().bindFramebuffer();
		shadowMapCascade.prepare();
		renderShadows(window, camera, stage);
		shadowMapCascade.getFbo().unbindFramebuffer();

		// Update projection matrix once per render cycle
		window.updateProjectionMatrix();
		
		clear();
		
		renderStage(window, camera, stage);
		renderSkybox(window, camera, stage);

		transformation.updateOrthographic2DMatrix(0, window.getWidth(), window.getHeight(), 0);
		
		renderGui(window, camera);
		//renderText(window, camera);
	}

	public void renderStage(Window window, Camera camera, Stage stage) {		
		entityShader.start();

		entityShader.view.loadMatrix(camera.getViewMatrix());
		entityShader.projection.loadMatrix(window.getProjectionMatrix());
		
		// TODO Cascades don't work because this is what we send up, if we only do it once they're right
		// This needs to be a uniform array
		Matrix4f[] projectionViewArray = new Matrix4f[shadowBoxes.size()];
		for(int i = 0; i < projectionViewArray.length; i++) {
			projectionViewArray[i] = new Matrix4f();
			projectionViewArray[i].set(shadowBoxes.get(i).getOrthoProjMatrix());
			projectionViewArray[i].mul(shadowBoxes.get(i).getLightViewMatrix());
		}
		entityShader.shadowSpaceMatrices.loadMatrixArray(projectionViewArray);

		shadowMapCascade.bindTextures(GL13.GL_TEXTURE1);
		
		renderLights(camera, stage.getStageLighting(), stage.getCast());
		
		// TODO Should definitely look into batching the rendering so that it renders a model for each model with the unique transform for them and then renders the next unique model
		for(ComponentMap entity : stage.getCast().getEntitiesWithComponents(Model.class, Body.class)) {
			Body body = entity.getAs(Body.class);
			Model model = entity.getAs(Model.class);
			
			entityShader.model.loadMatrix(transformation.buildModelMatrix(body));
			
			model.prepare();
			
			if(model instanceof Animatable) {
				AnimatedFrame frame = ((Animatable) model).getCurrentFrame();
				entityShader.jointsMatrix.loadMatrixArray(frame.getJointMatrices());
			}

			for(Mesh mesh : model.getMeshes()) {
				entityShader.material.loadMaterial(mesh.getMaterial());
				
				mesh.render();
			}
		}

		entityShader.stop();
	}
	
	public void renderSkybox(Window window, Camera camera, Stage stage) {
		skyboxShader.start();
		
		skyboxShader.view.loadMatrix(camera.getViewMatrixNoTranslation());
		skyboxShader.projection.loadMatrix(window.getProjectionMatrix());
		
		skyboxMesh.render();
		
		skyboxShader.stop();
	}

	private void renderLights(Camera camera, StageLighting lighting, Cast cast) {
		int pLight = 0;
		for(ComponentMap entity : cast.getEntitiesWithComponents(RenderConstants.MAX_POINT_LIGHTS, PointLight.class, Body.class)) {
			PointLight currPointLight = new PointLight(entity.getAs(PointLight.class));
			Vector3f lightPosition = new Vector3f(entity.getAs(Body.class).getPosition());
			Vector4f aux = new Vector4f(lightPosition, 1);
			aux.mul(camera.getViewMatrix());
			lightPosition.x = aux.x;
			lightPosition.y = aux.y;
			lightPosition.z = aux.z;
			entityShader.pointLights[pLight].loadPointLight(currPointLight, lightPosition);
			pLight++;
		}
		while(pLight < RenderConstants.MAX_POINT_LIGHTS) {
			entityShader.pointLights[pLight].loadPointLight(new PointLight(), new Vector3f());
			pLight++;
		}
		
		int sLight = 0;
		for(ComponentMap entity : cast.getEntitiesWithComponents(RenderConstants.MAX_SPOT_LIGHTS, SpotLight.class, Body.class)) {
			SpotLight currSpotLight = new SpotLight(entity.getAs(SpotLight.class));
			Vector3f lightPosition = new Vector3f(entity.getAs(Body.class).getPosition());
			// Get a copy of the spot light object and transform its position and cone direction to view coordinates
			Vector4f dir = new Vector4f(currSpotLight.getConeDirection(), 0);
			dir.mul(camera.getViewMatrix());
			currSpotLight.setConeDirection(new Vector3f(dir.x, dir.y, dir.z));

			Vector4f auxSpot = new Vector4f(lightPosition, 1);
			auxSpot.mul(camera.getViewMatrix());
			lightPosition.x = auxSpot.x;
			lightPosition.y = auxSpot.y;
			lightPosition.z = auxSpot.z;
			entityShader.spotLights[sLight].loadSpotLight(currSpotLight, lightPosition);
			sLight++;
		}
		while(sLight < RenderConstants.MAX_SPOT_LIGHTS) {
			entityShader.spotLights[sLight].loadSpotLight(new SpotLight(), new Vector3f());
			sLight++;
		}
		
		// TODO: Only do one of these (Return an Optional?)
		for(ComponentMap entity : cast.getEntitiesWithComponent(DirectionalLight.class, 1)) {
			DirectionalLight directionalLight = entity.getAs(DirectionalLight.class);
			// Get a copy of the directional light object and transform its direction to view coordinates
			DirectionalLight currDirLight = new DirectionalLight(directionalLight);
			Vector4f dir = new Vector4f(currDirLight.getDirection(), 0);
			dir.mul(camera.getViewMatrix());
			currDirLight.setDirection(new Vector3f(dir.x, dir.y, dir.z));
			entityShader.directionalLight.loadDirectionalLight(currDirLight);
		}
		
		entityShader.ambientLight.loadVec3(lighting.getAmbientLight());
		entityShader.specularPower.loadFloat(lighting.getSpecularPower());
	}
	
	private void renderGui(Window window, Camera camera) {
		guiShader.start();
		
		Matrix4f guiMatrix = new Matrix4f();
		guiMatrix.translate(new Vector3f(600f, 400f, 0));
		guiMatrix.scale(new Vector3f(300f));
		guiShader.projModelMatrix.loadMatrix(transformation.getOrthographic2DMatrix().mul(guiMatrix));
		
		//guiMesh.render();
		
		guiShader.stop();
	}
	
	private void renderText(Window window, Camera camera) {
		fontShader.start();

		textRotation = MathUtils.clamp(textRotation + (float) (Theater.getDeltaChange(100) * textRotationSlope), 0f, 360f);
		if(textRotation == 360) {
			textRotationSlope = -1;
		} else if(textRotation == 0) {
			textRotationSlope = 1;
		}
		text.rotation.set(0, 0, (float) Math.toRadians(textRotation));

		Matrix4f textMatrix = new Matrix4f();
		textMatrix.translate(text.position);
		textMatrix.translate((float) text.getBoundingBox().getWidth() / 2, (float) text.getBoundingBox().getHeight() / 2, 0);
		textMatrix.rotateXYZ(text.rotation);
		textMatrix.translate((float) -text.getBoundingBox().getWidth() / 2, (float) -text.getBoundingBox().getHeight() / 2, 0);
		textMatrix.scale(text.scale);
		fontShader.projModelMatrix.loadMatrix(transformation.getOrthographic2DMatrix().mul(textMatrix));

		colorLerpTime = MathUtils.clamp(colorLerpTime + (float) (Theater.getDelta() * colorLerpSlope), 0f, colorLerpDuration);
		if(colorLerpTime == colorLerpDuration) {
			colorLerpSlope = -1;
		} else if(colorLerpTime == 0) {
			colorLerpSlope = 1;
		}
		Vector4f textColor = text.getMesh().getMaterial().getAmbient().lerp(new Vector4f(0.25f), colorLerpTime / colorLerpDuration, new Vector4f());
		fontShader.color.loadVec4(textColor);

		// Support for transparencies
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		text.getMesh().render();
		
		GL11.glDisable(GL11.GL_BLEND);

		fontShader.stop();
	}
	
	public void renderLine(Line line, Window window, Camera camera) {
		colorShader.start();
		
		colorShader.view.loadMatrix(camera.getViewMatrix());
		colorShader.projection.loadMatrix(window.getProjectionMatrix());
		
		line.render();
		
		colorShader.stop();
	}
	
	private void renderShadows(Window window, Camera camera, Stage stage) {
		depthShader.start();

//		ocillate = MathUtils.clamp(ocillate + (Theater.getDeltaChange(0.5f) * flip), -1f, 1f);
//		if(ocillate >= 1f) {
//			flip = -1;
//		} else if(ocillate <= -1f) {
//			flip = 1;
//		}
		
		for(int i = 0; i < shadowBoxes.size(); i++) {
			ShadowBox cascade = shadowBoxes.get(i);
			cascade.update(camera);
			
			shadowMapCascade.bindTexture(i);
			GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
			
			for(ComponentMap entity : stage.getCast().getEntitiesWithComponent(DirectionalLight.class, 1)) {
				DirectionalLight directionalLight = entity.getAs(DirectionalLight.class);
				cascade.updateLightView(directionalLight.getDirection().negate(new Vector3f()));
			}
			
			Matrix4f projectionViewMatrix = new Matrix4f();
			projectionViewMatrix.set(cascade.getOrthoProjMatrix());
			projectionViewMatrix.mul(cascade.getLightViewMatrix());
			
			// TODO Should definitely look into batching the rendering so that it renders a model for each model with the unique transform for them and then renders the next unique model
			for(ComponentMap entity : stage.getCast().getEntitiesWithComponents(Model.class, Body.class)) {
				Body body = entity.getAs(Body.class);
				Model model = entity.getAs(Model.class);
	
				depthShader.modelViewProjectionMatrix.loadMatrix(projectionViewMatrix.mul(transformation.buildModelMatrix(body), new Matrix4f()));
				
				if(model instanceof Animatable) {
					AnimatedFrame frame = ((Animatable) model).getCurrentFrame();
					depthShader.jointsMatrixArray.loadMatrixArray(frame.getJointMatrices());
				}
	
				for(Mesh mesh : model.getMeshes()) {
					if(mesh.getMaterial().isTextured()) {
						// TODO Check for transparency and disable backface culling for this to be working fully
						mesh.getMaterial().getTexture().bindToUnit(0);
					}
					
					mesh.render();
				}
			}
		}
		
		depthShader.stop();
	}
	
	private void clear() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}

	@Override
	public void cleanUp() {
		entityShader.cleanUp();
		skyboxShader.cleanUp();
		fontShader.cleanUp();
		colorShader.cleanUp();
		
		shadowMapCascade.cleanUp();
	}

}
