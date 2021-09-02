package com.ten31f.solutions.handlers;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import com.ten31f.solutions.domain.SpeachBurst;
import com.ten31f.solutions.runners.AudioToText;

import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.UserAudio;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class Transcriber implements AudioReceiveHandler {

	private Map<String, SpeachBurst> speachMap = null;

	private ExecutorService executorService = null;

	private MessageChannel messageChannel = null;

	public Transcriber(ExecutorService executorService) {
		setExecutorService(executorService);
		setSpeachMap(new HashMap<>());
	}

	@Override
	public boolean canReceiveUser() {
		return true;
	}

	@Override
	public void handleUserAudio(UserAudio userAudio) {

		User user = userAudio.getUser();
		String userName = user.getName();

		SpeachBurst speachBurst = getSpeachMap().get(userName);

		if (speachBurst != null && !speachBurst.isClosed()) {
			speachBurst.queue(userAudio.getAudioData(1.0f));
		} else if (speachBurst != null && speachBurst.isClosed()) {
			getSpeachMap().remove(userName);
			speachBurst = null;
		}

		if (speachBurst == null) {
			speachBurst = new SpeachBurst(userName, userAudio.getAudioData(1.0f));
			getExecutorService().execute(new AudioToText(speachBurst, getMessageChannel()));
			getSpeachMap().put(userName, speachBurst);
		}
	}

	private void setSpeachMap(Map<String, SpeachBurst> speachMap) {
		this.speachMap = speachMap;
	}

	private Map<String, SpeachBurst> getSpeachMap() {
		return speachMap;
	}

	private void setExecutorService(ExecutorService executorService) {
		this.executorService = executorService;
	}

	private ExecutorService getExecutorService() {
		return executorService;
	}

	public MessageChannel getMessageChannel() {
		return messageChannel;
	}

	public void setMessageChannel(MessageChannel messageChannel) {
		this.messageChannel = messageChannel;
	}
}
