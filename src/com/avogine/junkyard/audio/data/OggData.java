package com.avogine.junkyard.audio.data;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import org.lwjgl.stb.STBVorbis;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;

import com.avogine.junkyard.util.ResourceConstants;

public class OggData extends BufferData<ShortBuffer> {
	
	public OggData(int format, ShortBuffer rawAudioBuffer, int frequency) {
		super(format, rawAudioBuffer, frequency);
	}
	
	public static OggData create(String file) {
		String fileName = System.getProperty("user.dir") + "/res/" + ResourceConstants.AUDIO_PATH + file;
		
		ShortBuffer rawAudioBuffer;
		int channels;
		int sampleRate;
		
		try(MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer channelsBuffer = stack.mallocInt(1);
			IntBuffer sampleRateBuffer = stack.mallocInt(1);
			
			// XXX This may trigger the same problem texture loading had and we'll need to load from memory or something?
			rawAudioBuffer = STBVorbis.stb_vorbis_decode_filename(fileName, channelsBuffer, sampleRateBuffer);
			
			channels = channelsBuffer.get();
			sampleRate = sampleRateBuffer.get();
		}
		
		int format = getOpenAlFormat(channels);
		
		OggData oggData = new OggData(format, rawAudioBuffer, sampleRate);
		return oggData;
	}
	
	@Override
	public ShortBuffer getData() {
		return super.getData();
	}

	private static void print(STBVorbisInfo info, long handle) {
		System.out.println("stream length, samples: " + STBVorbis.stb_vorbis_stream_length_in_samples(handle));
		System.out.println("stream length, seconds: " + STBVorbis.stb_vorbis_stream_length_in_seconds(handle));

		System.out.println();

		STBVorbis.stb_vorbis_get_info(handle, info);

		System.out.println("channels = " + info.channels());
		System.out.println("sampleRate = " + info.sample_rate());
		System.out.println("maxFrameSize = " + info.max_frame_size());
		System.out.println("setupMemoryRequired = " + info.setup_memory_required());
		System.out.println("setupTempMemoryRequired() = " + info.setup_temp_memory_required());
		System.out.println("tempMemoryRequired = " + info.temp_memory_required());
	}

}
