package com.ten31f.solutions.listners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.dv8tion.jda.api.events.GatewayPingEvent;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.EventListener;

public class EventLogger implements EventListener {

	private static final Logger LOGGER = LogManager.getLogger(EventLogger.class);

	@Override
	public void onEvent(GenericEvent event) {

		if (event instanceof GatewayPingEvent)
			return;
		else if (event instanceof ReadyEvent)
			LOGGER.info("API is ready!");
		else
			LOGGER.info(event.getClass());

	}

}
