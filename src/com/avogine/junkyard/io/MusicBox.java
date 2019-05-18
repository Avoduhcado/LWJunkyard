package com.avogine.junkyard.io;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.openal.ALUtil;
import org.lwjgl.openal.EnumerateAllExt;

import com.avogine.junkyard.audio.load.AudioCache;
import com.avogine.junkyard.memory.MemoryManaged;

public class MusicBox implements MemoryManaged {

	private long device;
	private ALCCapabilities alcCapabilities;
	private ALCapabilities alCapabilities;
	private long context;
	
	public MusicBox(Window window) {
		printVersionInfo();
		enumerateDevices();
		
		// Use this to attempt to load a pre-selected device
		//device = ALC10.alcOpenDevice(ALC10.alcGetString(0, ALC10.ALC_DEFAULT_DEVICE_SPECIFIER));\
		// Select default device by passing in null
		device = ALC10.alcOpenDevice((ByteBuffer) null);
		if(device != 0) {
			context = ALC10.alcCreateContext(device, (IntBuffer) null);
			ALC10.alcMakeContextCurrent(context);			
		}
		alcCapabilities = ALC.createCapabilities(device);
		alCapabilities = AL.createCapabilities(alcCapabilities);
		if(AL10.alGetError() != AL10.AL_NO_ERROR) {
			System.err.println("Could not set up AL device and context.");
		}
		
		AL10.alDistanceModel(AL11.AL_LINEAR_DISTANCE_CLAMPED);
		
		
		
		
		
		/*String fileName = System.getProperty("user.dir") + "/res/" + ResourceConstants.AUDIO_PATH + "Menu.ogg";
		
		// TODO Clean this up, buffers can't be cleaned up before we load what we need
		//Allocate space to store return information from the function
		//try(MemoryStack stack = MemoryStack.stackPush()) {
		// XXX All of this works but is nasty af
		MemoryStack.stackPush();
		IntBuffer channelsBuffer = MemoryStack.stackMallocInt(1);
		//}
		//try(MemoryStack stack = MemoryStack.stackPush()) {
		MemoryStack.stackPush();
		IntBuffer sampleRateBuffer = MemoryStack.stackMallocInt(1);
		//}

		ShortBuffer rawAudioBuffer = STBVorbis.stb_vorbis_decode_filename(fileName, channelsBuffer, sampleRateBuffer);

		//Retreive the extra information that was stored in the buffers by the function
		int channels = channelsBuffer.get();
		int sampleRate = sampleRateBuffer.get();
		
		MemoryStack.stackPop();
		MemoryStack.stackPop();
		
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
		
		//Find the correct OpenAL format
		int format = -1;
		if(channels == 1) {
		    format = AL10.AL_FORMAT_MONO16;
		} else if(channels == 2) {
		    format = AL10.AL_FORMAT_STEREO16;
		}

		//Request space for the buffer
		int bufferPointer = AL10.alGenBuffers();

		//Send the data to OpenAL
		AL10.alBufferData(bufferPointer, format, rawAudioBuffer, sampleRate);

		//Free the memory allocated by STB
		//free(rawAudioBuffer);
		
		AudioSource source = new AudioSource();
		AL10.alSourcei(source.getId(), AL10.AL_SOURCE_RELATIVE, AL10.AL_TRUE);

		AL10.alSourcei(source.getId(), AL10.AL_BUFFER, bufferPointer);
		AL10.alSourcePlay(source.getId());*/
	}
	
	public void printVersionInfo() {
		System.out.println("ALC Version: " + ALC10.alcGetInteger(0, ALC10.ALC_MAJOR_VERSION) + "." + ALC10.alcGetInteger(0, ALC10.ALC_MINOR_VERSION));
	}
	
	public void printExtensions() {
		System.out.println(ALUtil.getStringList(0, ALC10.ALC_EXTENSIONS));
	}
	
	public void enumerateDevices() {
		if(ALC10.alcIsExtensionPresent(0, "ALC_ENUMERATION_EXT")) {
			System.out.println("all devices: " + ALUtil.getStringList(0, ALC10.ALC_DEVICE_SPECIFIER));
			System.out.println("default device: " + ALC10.alcGetString(0, ALC10.ALC_DEFAULT_DEVICE_SPECIFIER));
		}
		if(ALC10.alcIsExtensionPresent(0, "ALC_ENUMERATE_ALL_EXT")) {
			System.out.println("all devices: " + ALUtil.getStringList(0, EnumerateAllExt.ALC_ALL_DEVICES_SPECIFIER));
			System.out.println("default device: " + ALC10.alcGetString(0, EnumerateAllExt.ALC_DEFAULT_ALL_DEVICES_SPECIFIER));
		}
	}
	
	@Override
	public void cleanUp() {
		// TODO Put this buffer cleanup in AudioCache?
		AudioCache.get().cleanUp();
		
		ALC10.alcMakeContextCurrent(0);
		ALC10.alcDestroyContext(context);
		if(!ALC10.alcCloseDevice(device)) {
			System.err.println("Failed to close ALC device: " + device);
		}
		
		ALC.destroy();
	}
	
}
