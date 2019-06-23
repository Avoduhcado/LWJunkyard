package com.avogine.junkyard.scene;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.ode4j.ode.DContactBuffer;
import org.ode4j.ode.DJoint;
import org.ode4j.ode.DJointGroup;
import org.ode4j.ode.DSpace;
import org.ode4j.ode.DTriMesh;
import org.ode4j.ode.DWorld;
import org.ode4j.ode.OdeConstants;
import org.ode4j.ode.OdeHelper;

import com.avogine.junkyard.memory.MemoryManaged;
import com.avogine.junkyard.scene.audio.BackgroundMusic;
import com.avogine.junkyard.scene.entity.Body;
import com.avogine.junkyard.scene.entity.Model;
import com.avogine.junkyard.scene.entity.body.Movable;
import com.avogine.junkyard.scene.entity.body.StaticBody;
import com.avogine.junkyard.scene.entity.collision.Collider;
import com.avogine.junkyard.scene.entity.collision.KineticCollider;
import com.avogine.junkyard.scene.entity.collision.TerrainCollider;
import com.avogine.junkyard.scene.entity.light.DirectionalLight;
import com.avogine.junkyard.scene.entity.light.PointLight;
import com.avogine.junkyard.scene.entity.light.PointLight.Attenuation;
import com.avogine.junkyard.scene.entity.light.SpotLight;
import com.avogine.junkyard.scene.entity.render.Animatable;
import com.avogine.junkyard.scene.entity.render.AnimatedModel;
import com.avogine.junkyard.scene.entity.render.StaticModel;
import com.avogine.junkyard.scene.entity.render.TerrainModel;
import com.avogine.junkyard.scene.light.StageLighting;
import com.avogine.junkyard.scene.render.Renderer;
import com.avogine.junkyard.scene.render.load.ModelInfo;
import com.avogine.junkyard.window.Window;

public class Stage extends Scene implements MemoryManaged {

	private Window window;

	private Camera camera;
	
	private Renderer renderer;
	
	private Cast cast;
	private StageLighting lighting;
	private Followers followers;
	
	private DWorld world;
	private DSpace space;
	private DJointGroup contactGroup;
		
	private BackgroundMusic bgm;
		
	public Stage(Window window) {
		setWindow(window);
		
		cast = new Cast();
		Vector3f ambientLight = new Vector3f(0.3f, 0.3f, 0.3f);
		float specularPower = 1f;
		lighting = new StageLighting(cast, ambientLight, specularPower);
		followers = new Followers(cast);

		OdeHelper.initODE();
		world = OdeHelper.createWorld();
		world.setGravity(0, -9.81, 0);
		world.setLinearDamping(0.01);
		
		space = OdeHelper.createHashSpace();
		contactGroup = OdeHelper.createJointGroup();
		
		//ground = OdeHelper.createPlane(space, 0, 1, 0, 0);
		
		bgm = new BackgroundMusic("menu");
		//bgm.play();
		
		this.camera = new Camera(window, new Vector3f(0, 100, 50), new Quaternionf(), 0f);
		cast.addComponent(Cast.CAMERA_ID, camera);
		
		renderer = new Renderer(window);
				
		int entity = cast.newEntity();
		Body entityBody = new StaticBody(entity, new Vector3f(10, 150, 0));
		cast.addComponent(entity, new AnimatedModel(entity, new ModelInfo("robutt11.fbx")));
		cast.addComponent(entity, entityBody);
		cast.addComponent(entity, new KineticCollider(entity, world, space, entityBody));
		/*Audioable entitySound = new NoiseMaker(entity, "bounce");
		cast.addComponent(entity, entitySound);
		entityBody.addListener(entitySound);*/
		
		for(int i = 0; i < 25; i++) {
			entity = cast.newEntity();
			entityBody = new StaticBody(entity, new Vector3f((float) ((Math.random() * 500) - 250), 150, (float) ((Math.random() * 500) - 250)));
			cast.addComponent(entity, new AnimatedModel(entity, new ModelInfo("robutt11.fbx")));
			cast.addComponent(entity, entityBody);
			cast.addComponent(entity, new KineticCollider(entity, world, space, entityBody));
		}
		
		//cast.addComponent(Cast.CAMERA_ID, new Follower(Cast.CAMERA_ID, entity));
		
		entity = cast.newEntity();
		Model treeModel = new StaticModel(entity, new ModelInfo("bigTree.obj"));
		cast.addComponent(entity, treeModel);
		Body treeBody = new StaticBody(entity, new Vector3f(-250, -5, -200));
		treeBody.setScale(new Vector3f(10f));
		cast.addComponent(entity, treeBody);
		
		/* TERRAIN */
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
				cast.addComponent(ground, new TerrainCollider(entity, world, space, groundBody, groundModel));
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
	
	@Override
	public void render() {
		renderer.render(window, camera, this);
	}
	
	@Override
	public void update() {
		// TODO Define some of these operations elsewhere? A stage post processor??
		lighting.doLighting();
		followers.followTheLeader();
		
		space.collide(null, (d, c1, c2) -> {
			int N = 10;
			DContactBuffer contactBuffer = new DContactBuffer(N);

			//boolean isGround = ((c1 == ground) || (c2 == ground));
			boolean isGround = (c1 instanceof DTriMesh || c2 instanceof DTriMesh);

			int n = OdeHelper.collide(c1, c2, N, contactBuffer.getGeomBuffer());

			if(isGround) {
				for(int i = 0; i < n; i++) {
					contactBuffer.get(i).surface.mode = OdeConstants.dContactBounce;
					contactBuffer.get(i).surface.mu = OdeConstants.dInfinity;
					contactBuffer.get(i).surface.bounce = 0.5; 		// (0.0~1.0) restitution parameter
					contactBuffer.get(i).surface.bounce_vel = 0.0; 	// minimum incoming velocity for bounce
					
					DJoint joint = OdeHelper.createContactJoint(world, contactGroup, contactBuffer.get(i));
					joint.attach(contactBuffer.get(i).geom.g1.getBody(), contactBuffer.get(i).geom.g2.getBody());
				}
			}
		});
		world.step(1.0 / 60.0);
		contactGroup.empty();
		
		cast.getEntitiesWithComponent(Body.class).stream()
			.map(b -> b.getAs(Body.class))
			.filter(Movable.class::isInstance)
			.map(Movable.class::cast)
			.filter(Movable::isAwake)
			.forEach(Movable::move);
		
		for(ComponentMap entity : cast.getEntitiesWithComponents(Body.class, Collider.class)) {
			Body body = entity.getAs(Body.class);
			Collider collider = entity.getAs(Collider.class);
			
			body.setPosition(collider.getBodyPosition());
		}
		
		cast.getEntitiesWithComponent(Model.class).stream()
			.map(m -> m.getAs(Model.class))
			.filter(Animatable.class::isInstance)
			.map(Animatable.class::cast)
			.forEach(Animatable::animate);
	}
	
	@Override
	public void cleanUp() {
		renderer.cleanUp();
		space.destroy();
		world.destroy();
		contactGroup.destroy();
		
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
	
	public StageLighting getStageLighting() {
		return lighting;
	}
	
}
