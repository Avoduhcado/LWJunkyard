package com.avogine.junkyard.audio.data;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.lwjgl.BufferUtils;

import com.avogine.junkyard.util.ResourceConstants;

public class WaveData extends BufferData<ByteBuffer> {

	protected WaveData(int format, ByteBuffer data, int frequency) {
		super(format, data, frequency);
	}

	public static WaveData create(String file) {
		String filePath = ResourceConstants.AUDIO_PATH + file;
		
		try(FileInputStream stream = new FileInputStream(filePath);
				InputStream bufferedInput = new BufferedInputStream(stream)) {
			try (AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedInput)) {
				AudioFormat audioFormat = audioStream.getFormat();
				int format = getOpenAlFormat(audioFormat);
				int frequency = (int) audioFormat.getSampleRate();
				int bytesPerFrame = audioFormat.getFrameSize();
				int totalBytes = (int) (audioStream.getFrameLength() * bytesPerFrame);
				ByteBuffer data = BufferUtils.createByteBuffer(totalBytes);
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
				
				WaveData wavStream = new WaveData(format, data, frequency);
				return wavStream;
			} catch (UnsupportedAudioFileException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e1) {
			System.err.println("Couldn't find file: " + filePath);
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return null;
	}
	
	@Override
	public ByteBuffer getData() {
		return super.getData();
	}

}