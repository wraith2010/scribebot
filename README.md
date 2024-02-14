<img align="right" src="https://user-images.githubusercontent.com/17032276/131927356-6df2919e-bfb2-4adc-ad92-a9f646f94eb5.png" height="200" width="200">

# Scribe Bot

Scribe Bot is a discord bot designed to transcribe the audio conversation of discord users in a given channel. The goal is to use the transcribed data to record meetings, DND Games and possible provide a way for deaf players to participate remotely. It is also the goal to read the stream of text and trigger context appropriate events if other bots are present. 

## Tech
* Maven
* JDA (Java Discord API)
* Cloud Speech-to-Text API

## Google API key required
This service uses a google api to translate speech to text.  An api key is required and is looked for as a environmental value.