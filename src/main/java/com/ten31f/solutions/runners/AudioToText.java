package com.ten31f.solutions.runners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognizeRequest;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.protobuf.ByteString;
import com.ten31f.solutions.domain.SpeachBurst;

import net.dv8tion.jda.api.entities.MessageChannel;

public class AudioToText implements Runnable {

	private static final Logger LOGGER = LogManager.getLogger(AudioToText.class);

	private static final int SAMPLE_RATE_HERTZ = 48000;
	private static final int CHANNEL_COUNT = 2;
	private static final String LANGUAGE_CODE = "en-US";

	private SpeachBurst speachBurst = null;

	private MessageChannel messageChannel = null;

	public AudioToText(SpeachBurst speachBurst, MessageChannel messageChannel) {
		setSpeachBurst(speachBurst);
		setMessageChannel(messageChannel);
	}

	@Override
	public void run() {

		while (!getSpeachBurst().isClosed()) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException interruptedException) {

			}
		}

		if (LOGGER.isInfoEnabled())
			LOGGER.info(String.format("Speech burst waited %sms to be sent for recognition",
					System.currentTimeMillis() - getSpeachBurst().getLastTime()));

		try (SpeechClient speechClient = SpeechClient.create()) {

			RecognitionConfig config = RecognitionConfig.newBuilder().setLanguageCode(LANGUAGE_CODE)
					.setSampleRateHertz(SAMPLE_RATE_HERTZ).setAudioChannelCount(CHANNEL_COUNT)
					.setEncoding(RecognitionConfig.AudioEncoding.LINEAR16).build();

			RecognitionAudio audio = RecognitionAudio.newBuilder()
					.setContent(ByteString.copyFrom(getSpeachBurst().getAudioAsWave())).build();

			RecognizeRequest request = RecognizeRequest.newBuilder().setConfig(config).setAudio(audio).build();
			RecognizeResponse response = speechClient.recognize(request);
			for (SpeechRecognitionResult result : response.getResultsList()) {
				// First alternative is the most probable result
				SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);

				getMessageChannel()
						.sendMessage(
								String.format("%s: %s", getSpeachBurst().getUserName(), alternative.getTranscript()))
						.queue();
			}

		} catch (Exception exception) {
			LOGGER.error(String.format("Failed to create the client due to: %s", exception), exception);
		}
	}

	public SpeachBurst getSpeachBurst() {
		return speachBurst;
	}

	public void setSpeachBurst(SpeachBurst speachBurst) {
		this.speachBurst = speachBurst;
	}

	public MessageChannel getMessageChannel() {
		return messageChannel;
	}

	public void setMessageChannel(MessageChannel messageChannel) {
		this.messageChannel = messageChannel;
	}
}
