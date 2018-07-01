package me.limeglass.birthdaybot;

import java.io.File;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

import me.limeglass.birthdaybot.objects.Action;
import me.limeglass.birthdaybot.objects.Client;
import me.limeglass.birthdaybot.tasks.UpdateTask;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.util.MessageBuilder;

public class BirthdayBot {
	
	private final static String prefix = ":tada:";
	public final static int time = 6000;
	private static Boolean debug = false;
	private static Configuration config;
	private static Client client;
	
	public static void main(String[] arguments) {
		Action.setup();
		new Thread(new Runnable() {
			public void run() {
				@SuppressWarnings("resource")
				Scanner scanner = new Scanner(System.in);
				while (scanner.hasNextLine()) {
					String value = scanner.nextLine();
					if (value.contains(":")) {
						long channelID = Long.parseLong(value.split(":")[0]);
						String message = value.substring(value.indexOf(":") + 1, value.length());
						new MessageBuilder(getClient()).withChannel(channelID).withContent(message).build();
					}
				}
			}
		}).start();
		Configurations configurations = new Configurations();
		try {
			config = configurations.properties(new File("config.properties"));
		}
		catch (ConfigurationException exception) {
			exception.printStackTrace();
		}
		client = new Client(config.getString("client.token"));
        Timer timer = new Timer(true);
        TimerTask task = new UpdateTask();
        timer.scheduleAtFixedRate(task, 0, time + 1);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
            	timer.cancel();
            }
        });
	}
	
	public static IDiscordClient getClient() {
		return client.getClient();
	}
	
	public static Client getClientObject() {
		return client;
	}
	
	public static Configuration getConfiguration() {
		return config;
	}

	public static String getPrefix() {
		return prefix;
	}

	public static Boolean inDebug() {
		return debug;
	}

	public static void setDebug(Boolean debug) {
		BirthdayBot.debug = debug;
	}
}
