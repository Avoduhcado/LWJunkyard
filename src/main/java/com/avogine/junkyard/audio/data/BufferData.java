package com.avogine.junkyard.audio.data;

import java.nio.Buffer;

import javax.sound.sampled.AudioFormat;

import org.lwjgl.openal.AL10;

public abstract class BufferData<T extends Buffer> {

	protected final int format;
	protected final T data;
	protected final int frequency;
	
	protected BufferData(int format, T data, int frequency) {
		this.format = format;
		this.data = data;
		this.frequency = frequency;
	}
	
	/*protected BufferData(AudioInputStream stream) {
		AudioFormat audioFormat = stream.getFormat();
		format = getOpenAlFormat(audioFormat);
		this.frequency = (int) audioFormat.getSampleRate();
		int bytesPerFrame = audioFormat.getFrameSize();
		int totalBytes = (int) (stream.getFrameLength() * bytesPerFrame);
		this.data = BufferUtils.createByteBuffer(totalBytes);
		byte[] dataArray = new byte[totalBytes];
		try {
			int bytesRead = stream.read(dataArray, 0, totalBytes);
			data.clear();
			data.put(dataArray, 0, bytesRead);
			data.flip();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Couldn't read bytes from audio stream!");
		}
	}*/

	public int getFormat() {
		return format;
	}
	
	public T getData() {
		return data;
	}
	
	public int getFrequency() {
		return frequency;
	}
	
	protected static int getOpenAlFormat(AudioFormat format) {
		if (format.getChannels() == 1) {
			return format.getSampleSizeInBits() == 8 ? AL10.AL_FORMAT_MONO8 : AL10.AL_FORMAT_MONO16;
		} else {
			return format.getSampleSizeInBits() == 8 ? AL10.AL_FORMAT_STEREO8 : AL10.AL_FORMAT_STEREO16;
		}
	}
	
	protected static int getOpenAlFormat(int channels) {
		if (channels == 1) {
			return AL10.AL_FORMAT_MONO16;
		} else {
			return AL10.AL_FORMAT_STEREO16;
		}
	}
	
}
