package com.ten31f.solutions.domain;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

public class SpeachBurst {

	// how long before burst closes
	private static final long OPEN_TIME = 1000l;

	private String userName = null;

	private long lastTime = -1;

	private Queue<byte[]> queue = null;

	public SpeachBurst(String userName, byte[] audioSnippet) {
		setUserName(userName);
		setQueue(new ConcurrentLinkedQueue<>());

		getQueue().add(audioSnippet);
		setLastTime(System.currentTimeMillis());
	}

	public boolean isClosed() {
		if (getQueue() == null)
			return false;

		return System.currentTimeMillis() - getLastTime() > OPEN_TIME;
	}

	public void queue(byte[] audioSnippet) {
		getQueue().add(audioSnippet);
		setLastTime(System.currentTimeMillis());
	}

	public byte[] poll() {
		return getQueue().poll();
	}

	public byte[] getAudioAsWave() throws IOException {

		ByteArrayOutputStream collectorByteArrayOutputStream = new ByteArrayOutputStream();

		for (byte[] littleByte : getQueue()) {
			collectorByteArrayOutputStream.write(littleByte);
		}

		ByteArrayOutputStream waveByteArrayOutputStream = new ByteArrayOutputStream();

		byte[] decodedData = collectorByteArrayOutputStream.toByteArray();

		AudioFormat format = new AudioFormat(48000.0f, 16, 2, true, true);
		AudioSystem.write(new AudioInputStream(new ByteArrayInputStream(decodedData), format, decodedData.length),
				AudioFileFormat.Type.WAVE, waveByteArrayOutputStream);
		return waveByteArrayOutputStream.toByteArray();
	}

	private void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserName() {
		return userName;
	}

	public long getLastTime() {
		return lastTime;
	}

	private void setLastTime(long lastTime) {
		this.lastTime = lastTime;
	}

	public Queue<byte[]> getQueue() {
		return queue;
	}

	private void setQueue(Queue<byte[]> queue) {
		this.queue = queue;
	}

}
