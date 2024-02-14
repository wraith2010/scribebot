package com.ten31f.solutions.listners;

import com.ten31f.solutions.handlers.Transcriber;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

@Slf4j
@Getter
@Setter
public class TranscriberCommand extends ListenerAdapter {

	private static final String COMMAND_SCRIBE = "!scribe";
	private static final String COMMAND_SCRIBE_DEBUG = "!scribe-debug";
	private static final String COMMAND_IGNORE = "!ignore";
	private static final String COMMAND_PING = "!ping";

	private Transcriber transcriber = null;

	public TranscriberCommand(Transcriber transcriber) {
		setTranscriber(transcriber);
	}

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		Message message = event.getMessage();
		User author = message.getAuthor();
		String content = message.getContentRaw();

		// Ignore message if bot
		if (author.isBot())
			return;

		switch (content.trim()) {
		case COMMAND_SCRIBE:
		case COMMAND_SCRIBE_DEBUG:
			onScribeCommand(event, content.trim());
			break;
		case COMMAND_IGNORE:
			onIgnoreCommand(event);
			break;
		case COMMAND_PING:
			onPingCommand(event);
			break;
		default:
		}

	}

	private void onIgnoreCommand(GuildMessageReceivedEvent guildMessageReceivedEvent) {
		Member member = guildMessageReceivedEvent.getMember();
		GuildVoiceState voiceState = member.getVoiceState();
		VoiceChannel channel = voiceState.getChannel();
		if (channel != null) {
			disconnectFrom(channel);
		}
	}

	/**
	 * Handle command without arguments.
	 *
	 * @param event The event for this command
	 */
	private void onScribeCommand(GuildMessageReceivedEvent event, String content) {
		//
		Member member = event.getMember();
		GuildVoiceState voiceState = member.getVoiceState();
		VoiceChannel channel = voiceState.getChannel();
		if (channel != null) {
			connectTo(channel); // Join the channel of the user
			onConnecting(channel, event.getChannel()); // Tell the user about our success
			getTranscriber().setMessageChannel(event.getChannel());
			getTranscriber().setShowTimes(COMMAND_SCRIBE_DEBUG.equals(content));
		} else {
			onUnknownChannel(event.getChannel(), "your voice channel"); // Tell the user about our failure
		}
	}

	private void onPingCommand(GuildMessageReceivedEvent event) {

		if (log.isInfoEnabled()) {
			log.info(String.format("[%s][%s] %s: %s%n", event.getGuild().getName(), event.getChannel().getName(),
					event.getMember().getEffectiveName(), event.getMessage().getContentDisplay()));
		}

		MessageChannel messageChannel = event.getChannel();

		MessageBuilder messageBuilder = new MessageBuilder();
		messageBuilder.append("Pong!");
		Message message = messageBuilder.build();

		long time = System.currentTimeMillis();
		messageChannel.sendMessage(message).queue(
				response -> response.editMessageFormat("Pong: %d ms", System.currentTimeMillis() - time).queue());
	}

	/**
	 * Inform user about successful connection.
	 *
	 * @param channel     The voice channel we connected to
	 * @param textChannel The text channel to send the message in
	 */
	private void onConnecting(VoiceChannel channel, TextChannel textChannel) {
		textChannel.sendMessage("Connecting to " + channel.getName()).queue(); // never forget to queue()!
	}

	/**
	 * The channel to connect to is not known to us.
	 *
	 * @param channel The message channel (text channel abstraction) to send failure
	 *                information to
	 * @param comment The information of this channel
	 */
	private void onUnknownChannel(MessageChannel channel, String comment) {
		channel.sendMessage("Unable to connect to ``" + comment + "``, no such channel!").queue(); // never forget to
																									// queue()!
	}

	/**
	 * Connect to requested channel and start echo handler
	 *
	 * @param channel The channel to connect to
	 */
	private void connectTo(VoiceChannel channel) {
		Guild guild = channel.getGuild();
		// Get an audio manager for this guild, this will be created upon first use for
		// each guild
		AudioManager audioManager = guild.getAudioManager();
		// Create our Send/Receive handler for the audio connection

		// The order of the following instructions does not matter!

		// Set the receiving handler to the same echo system, otherwise we can't echo
		// anything
		audioManager.setReceivingHandler(getTranscriber());
		// Connect to the voice channel
		audioManager.openAudioConnection(channel);
	}

	private void disconnectFrom(VoiceChannel voiceChannel) {
		Guild guild = voiceChannel.getGuild();
		// Get an audio manager for this guild, this will be created upon first use for
		// each guild
		AudioManager audioManager = guild.getAudioManager();
		// Create our Send/Receive handler for the audio connection
		audioManager.closeAudioConnection();
	}
}
