package com.avogine.junkyard.audio.load;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.avogine.junkyard.audio.AudioBuffer;
import com.avogine.junkyard.memory.MemoryManaged;

public class AudioCache implements MemoryManaged {

	private static AudioCache cache = new AudioCache();
	private static Properties audioDirectory = new Properties();
	static {
		try (InputStream input = AudioCache.class.getClassLoader().getResourceAsStream("audio.properties")) {
			audioDirectory.load(input);	
		} catch (IOException e) {
			System.err.println("Failed to load audio properties!");
			e.printStackTrace();
		}
	}
	
	public static AudioCache get() {
		return cache;
	}
	
	private Map<String, AudioBuffer> sounds = new HashMap<>();
	
	// TODO Should probably put all the available sound resources in a properties file and then do a look up from there so I just have to specify the name and it will find the format
	public AudioBuffer getSound(String soundName) {
		if(sounds.containsKey(soundName)) {
			return sounds.get(soundName);
		}
		
		AudioBuffer audio = new AudioBuffer();
		try {
			audio = AudioLoader.loadSound(audioDirectory.getProperty(soundName));
		} catch (Exception e) {
			e.printStackTrace();
		}
		sounds.put(soundName, audio);
		return audio;
	}

	@Override
	public void cleanUp() {
		sounds.values().stream().forEach(MemoryManaged::cleanUp);
	}
	
}
