package me.limeglass.birthdaybot.objects;

import me.limeglass.birthdaybot.listeners.EventListener;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;

public class Client {
	
	protected String token;
	private ClientBuilder builder;
	private IDiscordClient client;
	private EventDispatcher dispatcher;
	
	public Client(String token) {
		this.token = token;
		this.builder = new ClientBuilder().withToken(token);
		this.client = builder.login();
		this.dispatcher = client.getDispatcher();
		dispatcher.registerListener(new EventListener());
	}
	
	public Client(IDiscordClient client) {
		this.client = client;
	}
	
	public IDiscordClient getClient() {
		return client;
	}
	
	public EventDispatcher getEventDispatcher() {
		return dispatcher;
	}
}