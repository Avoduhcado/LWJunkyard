package com.avogine.junkyard.scene.entity.audio;

import com.avogine.junkyard.audio.AudioSource;
import com.avogine.junkyard.audio.load.AudioCache;
import com.avogine.junkyard.io.event.TimeEvent;
import com.avogine.junkyard.io.event.TimeListener;
import com.avogine.junkyard.memory.MemoryManaged;
import com.avogine.junkyard.scene.entity.Audioable;
import com.avogine.junkyard.scene.entity.event.EntityEvent;
import com.avogine.junkyard.scene.entity.event.MovementEvent;
import com.avogine.junkyard.system.TimeWizard;

public class NoiseMaker extends Audioable implements TimeListener, MemoryManaged {

	private AudioSource source;
	private String soundName;
	
	private double playDelay;
	private double timePassed;
	
	public NoiseMaker(int entity, String soundName) {
		super(entity);
		TimeWizard.addListener(this);
		this.soundName = soundName;
		source = new AudioSource();
		source.setLooping(true);
		playDelay = (Math.random() * 5) + 2;
		System.out.println(ID + " delay: " + playDelay);
		play();
	}

	@Override
	public void play() {
		System.out.println("Playing " + this.ID);
		source.play(AudioCache.get().getSound(soundName));
	}
	
	@Override
	public void fireEvent(EntityEvent event) {
		if(event instanceof MovementEvent) {
			switch(event.getType()) {
			case MovementEvent.IMMEDIATE:
				source.setPosition(((MovementEvent) event).getMovement());
				break;
			case MovementEvent.RELATIVE:
				source.addPosition(((MovementEvent) event).getMovement());
				break;
			}
		}
		// TODO Velocity/Gain/... events
	}

	@Override
	public void timePassed(TimeEvent e) {
		timePassed += e.getDelta();
		if(timePassed >= playDelay) {
			timePassed = 0;
			System.out.println("Playing " + this.ID);
			//source.play(AudioCache.get().getSound(soundName));
		}
	}

	@Override
	public void cleanUp() {
		source.cleanUp();
	}

}
