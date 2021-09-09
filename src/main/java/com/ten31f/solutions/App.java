package com.ten31f.solutions;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.security.auth.login.LoginException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ten31f.solutions.handlers.Transcriber;
import com.ten31f.solutions.listners.PingPong;
import com.ten31f.solutions.listners.TranscriberCommand;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class App extends ListenerAdapter {

	private static final Logger LOGGER = LogManager.getLogger(App.class);

	public static void main(String[] args) throws LoginException, InterruptedException {

		ExecutorService executorService = Executors.newCachedThreadPool();

		Transcriber transcriber = new Transcriber(executorService);

		validate(args);

		JDABuilder jdaBuilder = JDABuilder.createDefault(args[0], GatewayIntent.GUILD_MESSAGES,
				GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_VOICE_STATES);

		jdaBuilder.setActivity(Activity.listening("To the Conversation"));

		jdaBuilder.addEventListeners(new PingPong(), new TranscriberCommand(transcriber));

		JDA jda = jdaBuilder.build();

		jda.awaitReady();

	}

	public static void validate(String[] args) {

		if (args.length < 1) {
			LOGGER.error("Unable to start without discord token and google key!");
			System.exit(1);
		}

	}

}
