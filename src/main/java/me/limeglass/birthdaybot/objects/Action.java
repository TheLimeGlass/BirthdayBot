package me.limeglass.birthdaybot.objects;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.reflections.Reflections;

import java.util.Set;

import com.google.common.reflect.Reflection;

import me.limeglass.birthdaybot.BirthdayBot;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.RequestBuffer;

public abstract class Action {

	protected static Map<Action, String[]> modules = new HashMap<Action, String[]>();

	protected static void registerAction(Action action, String... actions) {
		if (!modules.containsValue(actions)) modules.put(action, actions);
	}

	public static void setup() {
		Reflections reflections = new Reflections("me.limeglass.birthdaybot");  
		Set<Class<? extends Action>> classes = reflections.getSubTypesOf(Action.class);
		Reflection.initialize(classes.toArray(new Class[classes.size()]));
	}

	private static Action getAction(String action) {
		for (Entry<Action, String[]> entry : modules.entrySet()) {
			for (String subAction : entry.getValue()) {
				if (subAction.equals(action.toLowerCase())) {
					return entry.getKey();
				}
			}
		}
		return null;
	}

	@SafeVarargs
	public static void callAction(MessageReceivedEvent event, String command, String... parameters) {
		Action action = getAction(command);
		if (action != null) action.onActionCall(command, event, parameters);
	}

	protected IMessage message(IChannel channel, String content) {
		return new MessageBuilder(BirthdayBot.getClient()).withChannel(channel).appendContent(content).build();
	}

	public void scheduledMessage(IChannel channel, long delay, String content) {
		RequestBuffer.request(() -> {
			IMessage message = new MessageBuilder(BirthdayBot.getClient()).withChannel(channel).appendContent(content).build();
			Executors.newScheduledThreadPool(1).schedule(() -> message.delete(), delay, TimeUnit.SECONDS);
		});
	}

	public abstract void onActionCall(final String action, final MessageReceivedEvent event, final String[] parameters);

}