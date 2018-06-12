package me.limeglass.birthdaybot.objects;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import me.limeglass.birthdaybot.BirthdayBot;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

public class Command {

	private final MessageReceivedEvent event;
	private final String input;
	private String command;
	private String[] arguments;
	
	public Command(MessageReceivedEvent event, String input) {
		this.event = event;
		this.input = input;
		this.arguments = input.split(" ");
		this.command = arguments[0];
		if (command == null || command.equals("")) {
			command = input;
			arguments = new String[1];
		}
		this.arguments = Arrays.copyOfRange(arguments, 1, arguments.length);
		if (BirthdayBot.inDebug()) {
			EmbedBuilder builder = new EmbedBuilder();
			builder.withAuthorName("Debug log - " + ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.RFC_1123_DATE_TIME));
			builder.withAuthorIcon("https://i.imgur.com/bMV38v8.png");
			builder.appendField("Message user", event.getAuthor().getName(), true);
			builder.appendField("Message command", command, true);
			builder.appendField("Message arguments", Arrays.toString(arguments), true);
			builder.appendField("Message content", "`" + input + "`", false);
			builder.appendField("Message channel", event.getChannel().mention(), true);
			builder.withColor(0, 255, 255);
			builder.appendField("Infomation", "Type: :tada: **help** for a list of all commands.", false);
			//TODO make the user allow to define the debug channel.
			RequestBuffer.request(() -> event.getChannel().sendMessage(builder.build()));
		}
		Action.callAction(event, command, arguments);
	}
	
	public MessageReceivedEvent getEvent() {
		return event;
	}

	public String[] getArguments() {
		return arguments;
	}
	
	public String getCommand() {
		return command;
	}

	public String getInput() {
		return input;
	}
}
