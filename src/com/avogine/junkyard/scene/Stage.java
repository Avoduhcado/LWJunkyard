package com.avogine.junkyard.scene;

import org.joml.Vector3f;

import com.avogine.junkyard.io.Window;
import com.avogine.junkyard.memory.MemoryManaged;
import com.avogine.junkyard.scene.audio.BackgroundMusic;
import com.avogine.junkyard.scene.entity.Body;
import com.avogine.junkyard.scene.entity.Model;
import com.avogine.junkyard.scene.entity.body.StaticBody;
import com.avogine.junkyard.scene.entity.collision.Collider;
import com.avogine.junkyard.scene.entity.light.DirectionalLight;
import com.avogine.junkyard.scene.entity.light.PointLight;
import com.avogine.junkyard.scene.entity.light.PointLight.Attenuation;
import com.avogine.junkyard.scene.entity.light.SpotLight;
import com.avogine.junkyard.scene.entity.render.AnimatedModel;
import com.avogine.junkyard.scene.entity.render.StaticModel;
import com.avogine.junkyard.scene.entity.render.TerrainModel;
import com.avogine.junkyard.scene.light.StageLighting;
import com.avogine.junkyard.scene.render.AvoDebugDraw;
import com.avogine.junkyard.scene.render.Renderer;
import com.avogine.junkyard.scene.render.load.ModelInfo;

public class Stage implements MemoryManaged {

	//private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	private Window window;

	private Camera camera;
	
	private Renderer renderer;
	
	private Cast cast;
	private StageLighting lighting;
	private Physics physics;
	private Followers followers;
	
	private Model treeModel;
	
	private BackgroundMusic bgm;
		
	public Stage(Window window) {
		setWindow(window);
		
		cast = new Cast();
		physics = new Physics(cast);
		Vector3f ambientLight = new Vector3f(0.3f, 0.3f, 0.3f);
		float specularPower = 1f;
		lighting = new StageLighting(cast, ambientLight, specularPower);
		followers = new Followers(cast);
		
		bgm = new BackgroundMusic("menu");
		//bgm.play();
		
		this.camera = new Camera(window, new Vector3f(0, 100, 50), new Vector3f(), 0f);
		cast.addComponent(Cast.CAMERA_ID, camera);
		
		renderer = new Renderer(window);
		
		physics.getWorld().setDebugDrawer(new AvoDebugDraw(renderer));
		
		int entity = cast.newEntity();
		Body entityBody = new StaticBody(entity, new Vector3f(10, 150, 0));
		cast.addComponent(entity, new AnimatedModel(entity, new ModelInfo("robutt11.fbx")));
		cast.addComponent(entity, entityBody);
		cast.addComponent(entity, new Collider(entity, physics.getWorld(), entityBody));
		/*Audioable entitySound = new NoiseMaker(entity, "bounce");
		cast.addComponent(entity, entitySound);
		entityBody.addListener(entitySound);*/
		
		for(int i = 0; i < 25; i++) {
			entity = cast.newEntity();
			entityBody = new StaticBody(entity, new Vector3f((float) ((Math.random() * 500) - 250), 150, (float) ((Math.random() * 500) - 250)));
			cast.addComponent(entity, new AnimatedModel(entity, new ModelInfo("robutt11.fbx")));
			cast.addComponent(entity, entityBody);
			cast.addComponent(entity, new Collider(entity, physics.getWorld(), entityBody));
		}
		
		//cast.addComponent(Cast.CAMERA_ID, new Follower(Cast.CAMERA_ID, entity));
		
		entity = cast.newEntity();
		treeModel = new StaticModel(entity, new ModelInfo("bigTree.obj"));
		cast.addComponent(entity, treeModel);
		Body treeBody = new StaticBody(entity, new Vector3f(-250, -5, -200));
		treeBody.setScale(new Vector3f(10f));
		cast.addComponent(entity, treeBody);
		
		/* TERRAIN */
//		int ground = cast.newEntity();
//		TerrainModel groundModel = new TerrainModel(ground, "");
//		Body groundBody = new StaticBody(ground, new Vector3f(-512, 0, -512));
//		cast.addComponent(ground, groundModel);
//		cast.addComponent(ground, groundBody);
//		cast.addComponent(ground, new Collider(ground, physics.getWorld(), groundModel.getMeshInterface(), groundBody));

		int ground;
		TerrainModel groundModel;
		Body groundBody;
		int xMax = 2;
		int yMax = 2;
		int halfWidth = (xMax * 792) / 2;
		int halfHeight = (yMax * 792) / 2;
		for(int x = 0; x < xMax; x++) {
			for(int y = 0; y < yMax; y++) {
				ground = cast.newEntity();
				groundModel = new TerrainModel(ground, new ModelInfo(x, y));
				groundBody = new StaticBody(ground, new Vector3f((x * 792) - halfWidth, 0, (y * 792) - halfHeight));
				cast.addComponent(ground, groundModel);
				cast.addComponent(ground, groundBody);
				cast.addComponent(ground, new Collider(ground, physics.getWorld(), groundModel.getMeshInterface(), groundBody));
			}
		}
		
		/* LIGHTING */
		Vector3f lightColor;
		Vector3f lightPosition;
		float lightIntensity = 1f;
		Attenuation att;
		
		// PointLight
		entity = cast.newEntity();
		lightColor = new Vector3f(1f, 1f, 1f);
		PointLight pointLight = new PointLight(entity, lightColor, lightIntensity);
		att = new Attenuation(1.0f, 0.0f, 0.0f);
		pointLight.setAttenuation(att);
		cast.addComponent(entity, pointLight);
		lightPosition = new Vector3f(0f, 2f, 10f);
		cast.addComponent(entity, new StaticBody(entity, lightPosition));
		
		Vector3f coneDir;
		float cutoff;
		
		// SpotLight 1
		entity = cast.newEntity();
		lightColor = new Vector3f(0f, 0f, 1f);
		lightIntensity = 1000f;
		pointLight = new PointLight(entity, lightColor, lightIntensity);
		att = new PointLight.Attenuation(1.0f, 0.0f, 0.0f);
		pointLight.setAttenuation(att);
		coneDir = new Vector3f(0, -0.5f, -0.5f);
		cutoff = (float) Math.cos(Math.toRadians(359));
		SpotLight spotLight = new SpotLight(pointLight, coneDir, cutoff);
		cast.addComponent(entity, spotLight);
		lightPosition = new Vector3f(-10f, 1000f, 100f);
		cast.addComponent(entity, new StaticBody(entity, lightPosition));
		
		// SpotLight 2
		entity = cast.newEntity();
		lightColor = new Vector3f(0f, 1f, 0f);
		pointLight = new PointLight(entity, lightColor, lightIntensity);
		pointLight.setAttenuation(att);
		coneDir = new Vector3f(0, -1, 0);
		spotLight = new SpotLight(pointLight, coneDir, cutoff);
		cast.addComponent(entity, spotLight);
		lightPosition = new Vector3f(0, 1000f, 0f);
		cast.addComponent(entity, new StaticBody(entity, lightPosition));
		
		Vector3f lightDirection;
		
		// DirectionalLight
		entity = cast.newEntity();
		lightColor = new Vector3f(1f, 1f, 1f);
		lightDirection = new Vector3f(0f, -1f, 1f);
		lightIntensity = 1f;
		DirectionalLight directionalLight = new DirectionalLight(entity, lightColor, lightDirection, lightIntensity);
		cast.addComponent(entity, directionalLight);
		
		//Font batangFont = FontCache.getFont("Avocado");
		//batangFont.printFont();
	}
	
	public void render() {
		renderer.render(window, camera, this);
		// TODO I think we batch/instance this here?
		//entities.stream().forEach(e -> render.render(e, camera));
		//render.renderScene(this);
		
		physics.getWorld().debugDrawWorld();
	}
	
	public void update() {
		//treeModel.setRotation(new Vector3f(0, treeModel.getRotation().y + Theater.getDeltaChange(1f), 0));
		
		physics.doPhysics();
		lighting.doLighting();
		followers.followTheLeader();
	}
	
	@Override
	public void cleanUp() {
		renderer.cleanUp();
		physics.cleanUp();
		
		bgm.cleanUp();
	}
	
	public Window getWindow() {
		return window;
	}
	
	public void setWindow(Window window) {
		this.window = window;
	}
	
	public Cast getCast() {
		return cast;
	}
	
	//XXX
	public Physics getPhysics() {
		return physics;
	}
	
	public StageLighting getStageLighting() {
		return lighting;
	}
	
}
