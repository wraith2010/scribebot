package com.ten31f.solutions.listners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class PingPong extends ListenerAdapter {

	private static final Logger LOGGER = LogManager.getLogger(PingPong.class);

	private static final String PING = "!ping";

	@Override
	public void onMessageReceived(MessageReceivedEvent messageReceivedEvent) {

		Message message = messageReceivedEvent.getMessage();
		if (message.getContentRaw().startsWith(PING)) {
			ping(messageReceivedEvent);
		}
	}

	private void ping(MessageReceivedEvent messageReceivedEvent) {

		if (LOGGER.isInfoEnabled()) {
			LOGGER.info(String.format("[%s][%s] %s: %s%n", messageReceivedEvent.getGuild().getName(),
					messageReceivedEvent.getTextChannel().getName(),
					messageReceivedEvent.getMember().getEffectiveName(),
					messageReceivedEvent.getMessage().getContentDisplay()));
		}

		MessageChannel messageChannel = messageReceivedEvent.getChannel();

		MessageBuilder messageBuilder = new MessageBuilder();
		messageBuilder.append("Pong!");
		Message message = messageBuilder.build();

		long time = System.currentTimeMillis();
		messageChannel.sendMessage(message) /* => RestAction<Message> */
				.queue(response /* => Message */ -> {
					response.editMessageFormat("Pong: %d ms", System.currentTimeMillis() - time).queue();
				});

	}

}
