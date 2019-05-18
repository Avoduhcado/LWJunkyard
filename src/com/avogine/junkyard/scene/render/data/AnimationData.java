package com.avogine.junkyard.scene.render.data;

import java.util.List;

public class AnimationData {

    private String name;
    private List<AnimatedFrame> frames;
	private double duration;
	
	public AnimationData(String name, List<AnimatedFrame> frames, double duration) {
        this.name = name;
        this.frames = frames;
        this.duration = duration;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<AnimatedFrame> getFrames() {
		return frames;
	}

	public void setFrames(List<AnimatedFrame> frames) {
		this.frames = frames;
	}

	public double getDuration() {
		return duration;
	}

	public void setDuration(double duration) {
		this.duration = duration;
	}
	
	public double getFrameDuration() {
		return frames.size() / duration;
	}
	
    @Override
    public String toString() {
    	return getName() + "[" + frames.size() + ", " + duration + "(" + getFrameDuration() + ")]";
    }
	
}
