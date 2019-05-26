package com.avogine.junkyard.audio.load;

import com.avogine.junkyard.audio.AudioBuffer;
import com.avogine.junkyard.audio.data.OggData;
import com.avogine.junkyard.audio.data.WaveData;

public class AudioLoader {

	public static final String WAV = "wav";
	public static final String OGG = "ogg";
	
	public static AudioBuffer loadSound(String audioFile) throws IllegalArgumentException {
		AudioBuffer buffer = new AudioBuffer();
		String format = audioFile.substring(audioFile.indexOf(".") + 1, audioFile.length());
		
		switch(format) {
		case WAV:
			WaveData waveFile = WaveData.create(audioFile);
			buffer.loadToBuffer(waveFile);
			break;
		case OGG:
			OggData oggFile = OggData.create(audioFile);
			buffer.loadToBuffer(oggFile);
			break;
		default:
			throw new IllegalArgumentException("Format: " + format + " is not a supported audio file format.");
		}
		
		return buffer;
	}
	
}
