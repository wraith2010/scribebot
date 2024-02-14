package com.ten31f.solutions.handlers;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import com.ten31f.solutions.domain.SpeachBurst;
import com.ten31f.solutions.runners.AudioToText;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.UserAudio;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

@Getter
@Setter
public class QueuedTranscriber implements AudioReceiveHandler {

	private Map<String, SpeachBurst> speachMap = null;
	private Deque<SpeachBurst> speachDeque = null;

	private ExecutorService executorService = null;

	private MessageChannel messageChannel = null;

	private boolean showTimes = false;

	public QueuedTranscriber() {
		setSpeachMap(new HashMap<>());
		setSpeachDeque(new LinkedList<>());
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
			getExecutorService().execute(new AudioToText(speachBurst, getMessageChannel(), isShowTimes()));
			getSpeachMap().put(userName, speachBurst);
		}
	}

}
