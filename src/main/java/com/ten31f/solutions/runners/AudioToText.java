package com.ten31f.solutions.runners;

import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognizeRequest;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.protobuf.ByteString;
import com.ten31f.solutions.domain.SpeachBurst;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.MessageChannel;

@AllArgsConstructor
@Slf4j
@Getter
@Setter
public class AudioToText implements Runnable {

	private static final int SAMPLE_RATE_HERTZ = 48000;
	private static final int CHANNEL_COUNT = 2;
	private static final String LANGUAGE_CODE = "en-US";

	private SpeachBurst speachBurst = null;

	private MessageChannel messageChannel = null;

	private boolean showTimes = false;

	public AudioToText(SpeachBurst speachBurst, MessageChannel messageChannel) {
		setSpeachBurst(speachBurst);
		setMessageChannel(messageChannel);
	}

	@Override
	public void run() {

		while (!getSpeachBurst().isClosed()) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException interruptedException) {
				log.error("Speech burst waiting intruppted", interruptedException);
			}
		}

		getSpeachBurst().addEvent(getSpeachBurst().getLastTime(), "last audio captured");
		getSpeachBurst().addEvent("Going out for transcription");

		try (SpeechClient speechClient = SpeechClient.create()) {

			RecognitionConfig config = RecognitionConfig.newBuilder().setLanguageCode(LANGUAGE_CODE)
					.setSampleRateHertz(SAMPLE_RATE_HERTZ).setAudioChannelCount(CHANNEL_COUNT)
					.setEncoding(RecognitionConfig.AudioEncoding.LINEAR16).setEnableAutomaticPunctuation(true).build();

			RecognitionAudio audio = RecognitionAudio.newBuilder()
					.setContent(ByteString.copyFrom(getSpeachBurst().getAudioAsWave())).build();

			RecognizeRequest request = RecognizeRequest.newBuilder().setConfig(config).setAudio(audio).build();
			RecognizeResponse response = speechClient.recognize(request);
			for (SpeechRecognitionResult result : response.getResultsList()) {
				// First alternative is the most probable result
				SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);

				getSpeachBurst().addEvent("Reporting transcription");

				StringBuilder messageStringBuilder = new StringBuilder();

				messageStringBuilder.append(
						String.format("**%s**: %s%n", getSpeachBurst().getUserName(), alternative.getTranscript()));

				if (isShowTimes()) {
					messageStringBuilder.append("```");
					getSpeachBurst().getEvents().stream().forEach(event -> messageStringBuilder
							.append(String.format("%s: %s%n", event.getLocalDateTime(), event.getDescription())));
					messageStringBuilder.append("```");
				}

				getMessageChannel().sendMessage(messageStringBuilder.toString()).queue();

				if (log.isInfoEnabled())
					log.info(messageStringBuilder.toString());
			}

		} catch (Exception exception) {
			log.error(String.format("Failed to create the client due to: %s", exception), exception);
		}
	}

}
