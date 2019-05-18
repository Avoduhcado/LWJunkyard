package com.avogine.junkyard.util;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.lwjgl.BufferUtils;
import org.lwjgl.assimp.AIFile;
import org.lwjgl.assimp.AIFileIO;
import org.lwjgl.system.MemoryUtil;

public class JarFile extends AIFile {
	
	private static AtomicInteger nextId = new AtomicInteger(1);
	private static Map<String, JarFile> fileMap = new HashMap<>();
	
	private String id;
	private String path;
	private ByteBuffer buf;
	private OutputStream out;
	
	public JarFile(String path) {
		super(BufferUtils.createByteBuffer(SIZEOF));
		
		this.path = path;
		
		ReadProc(JarFile::read);
		WriteProc(JarFile::write);
		TellProc(JarFile::tell);
		FileSizeProc(JarFile::fileSize);
		SeekProc(JarFile::seek);
		FlushProc(JarFile::flush);
		
		id = Integer.toString(nextId.getAndIncrement());
		fileMap.put(id, this);
		
		UserData(MemoryUtil.memAddress(MemoryUtil.memUTF8(id, true)));
		
		try {
			byte[] bytes = ClassLoader.getSystemResourceAsStream(this.path).readAllBytes();
			buf = MemoryUtil.memAlloc(bytes.length).put(bytes).flip();
		} catch (Exception ex) {
			throw new RuntimeException("Error trying to read " + path, ex);
		}
	}
	
	@Override
	public String toString() {
		return String.format("id: %s, path: %s", id, path);
	}
	
	@Override
	public void close() {
		if (out != null) {
			try { 
				out.close(); out = null; 
			} catch (IOException ex) {
				ex.printStackTrace(); 
			}
		}
		fileMap.remove(id);
		MemoryUtil.memFree(buf);
		super.close();
	}
	
	private static JarFile lookup(long pFile) {
		AIFile aiFile = AIFile.create(pFile);
		String idStr = MemoryUtil.memUTF8(aiFile.UserData());
		return fileMap.get(idStr);
	}
	
	private static long read(long pFile, long pBuffer, long size, long count) {
		JarFile jarFile = lookup(pFile);
		System.out.printf("READ { size: %d, count: %d }\n", size, count);
		
		try {
			long numBytesToRead = size * count;
			int i = 0;
			ByteBuffer buf = jarFile.buf;
			while (i < numBytesToRead && buf.hasRemaining()) {
				MemoryUtil.memPutByte(pBuffer + i, buf.get());
				i++;
			}
			System.out.printf("%d bytes read\n", i);
			return i;
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}
	
	private static long tell(long pFile) {
		JarFile jarFile = lookup(pFile);
		System.out.printf("TELL { curPos: %d }\n", jarFile.buf.position());
		// Apparently "tell" means report the current byte position
		return jarFile.buf.position();
	}
	
	private static long fileSize(long pFile) {
		JarFile jarFile = lookup(pFile);
		System.out.printf("SIZE { size: %d }\n", jarFile.buf.limit());
		return jarFile.buf.limit();
	}
	
	private static int seek(long pFile, long offset, int origin) {
		JarFile jarFile = lookup(pFile);
		System.out.printf("SEEK { offset: %d, origin: %d }\n", offset, origin);
		jarFile.buf.position((int)offset);
		return 0; // 0 means success, -1 means failure
	}
	
	// Write operations are not supported for jar files so these are noops
	
	private static long write(long pFile, long pBuffer, long memB, long count) {
		System.out.printf("WRITE { memB: %d, count %d }\n", memB, count);
		// Return the count to pretend like we wrote the bytes
		return count;
	}
	
	private static void flush(long pFile) {
		System.out.println("FLUSH");
	}
	
	private static class JarFileIO extends AIFileIO {
		public JarFileIO() {
			super(BufferUtils.createByteBuffer(SIZEOF));
			
			OpenProc(JarFileIO::open);
			CloseProc(JarFileIO::close);
		}
		
		private static long open(long pFileIO, long fileName, long openMode) {
			String name = MemoryUtil.memUTF8(fileName);
			String mode = MemoryUtil.memUTF8(openMode);
			
			JarFile jarFile = new JarFile(name);
			
			System.out.printf("OPEN(%s) { mode: %s }\n", jarFile, mode);
			
			return jarFile.address();
		}
		
		private static void close(long pFileIO, long pFile) {
			JarFile jarFile = lookup(pFile);
			System.out.printf("CLOSE(%s)\n", jarFile);
			jarFile.close();
		}
	}
	
	public static AIFileIO createFileIO() {
		return new JarFileIO();
	}

}
