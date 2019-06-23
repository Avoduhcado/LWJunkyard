package com.avogine.junkyard.scene.entity.render;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import com.avogine.junkyard.Theater;
import com.avogine.junkyard.scene.entity.Model;
import com.avogine.junkyard.scene.entity.event.EntityEvent;
import com.avogine.junkyard.scene.render.data.AnimatedFrame;
import com.avogine.junkyard.scene.render.data.AnimationData;
import com.avogine.junkyard.scene.render.data.ModelData;
import com.avogine.junkyard.scene.render.load.ModelCache;
import com.avogine.junkyard.scene.render.load.ModelInfo;
import com.avogine.junkyard.scene.render.util.RenderConstants;

// TODO Extract animation stuff into some sort of AnimationController component
public class AnimatedModel extends Model implements Animatable {

	private static final int ANIMATION_FRAME_RATE = 12;
	
	protected Map<String, AnimationData> animations;
	protected String currentAnimation;
	protected AnimatedFrame tweenedFrame;

	protected int currentFrame;
	protected double animationStep;

	public AnimatedModel(int entity, ModelInfo modelInfo) {
		super(entity, modelInfo);
	}

	@Override
	public void fireEvent(EntityEvent event) {
		// TODO Auto-generated method stub
	}

	@Override
	public void loadMeshes(ModelInfo modelInfo) {
		try {
			ModelData modelData = ModelCache.getModel(modelInfo.getModelName());
			meshes = modelData.getMeshes();
			animations = modelData.getAnimations();
			System.out.println(animations.keySet());
			Optional<Entry<String, AnimationData>> entry = animations.entrySet().stream().findFirst();
			currentAnimation = entry.isPresent() ? entry.get().getKey() : null;
			System.out.println(currentAnimation);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void prepare() {
	}

	@Override
	public void animate() {
		// TODO Put this in a time listener
		animationStep += Theater.getDeltaChange(ANIMATION_FRAME_RATE);
		if(animationStep >= animations.get(currentAnimation).getFrameDuration()) {
			nextFrame();
			animationStep = 0;
			
//			if(Math.random() > 0.8) {
//				animationStep += Theater.getDeltaChange(ANIMATION_FRAME_RATE);
//			}
		}
	}

	protected void nextFrame() {
		int nextFrame = currentFrame + 1;
		if (nextFrame > animations.get(currentAnimation).getFrames().size() - 1) {
			currentFrame = 0;
		} else {
			currentFrame = nextFrame;
		}
	}

	public Map<String, AnimationData> getAnimations() {
		return animations;
	}

	protected String getCurrentAnimation() {
		return currentAnimation;
	}

	public void setCurrentAnimation(String currentAnimation) {
		this.currentAnimation = currentAnimation;
		resetAnimation();
	}
	
	protected void resetAnimation() {
		currentFrame = 0;
		animationStep = 0;
	}
	
	@Override
	public AnimatedFrame getCurrentFrame() {
		if(RenderConstants.TWEENING) {
			return getCurrentFrameTweened(); 
		}
		return animations.get(currentAnimation).getFrames().get(currentFrame);
	}

	protected AnimatedFrame getCurrentFrameTweened() {
		if(tweenedFrame == null) {
			tweenedFrame = new AnimatedFrame();
		}
		AnimatedFrame current = animations.get(currentAnimation).getFrames().get(currentFrame);
		AnimatedFrame next = animations.get(currentAnimation).getFrames().get((currentFrame + 1 >= animations.get(currentAnimation).getFrames().size()) ? 0 : currentFrame + 1);

		float mixFactor = (float) (animationStep / animations.get(currentAnimation).getFrameDuration());
		for(int i = 0; i < RenderConstants.MAX_JOINTS; i++) {
			current.getJointMatrices()[i].lerp(next.getJointMatrices()[i], mixFactor, tweenedFrame.getJointMatrices()[i]);
		}

		return tweenedFrame;
	}

}
