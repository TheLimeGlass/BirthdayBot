package me.limeglass.birthdaybot.actions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import me.limeglass.birthdaybot.BirthdayBot;
import me.limeglass.birthdaybot.objects.Action;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.PermissionUtils;

public class SetupAction extends Action {

	private Map<IUser, Integer> storage = new HashMap<IUser, Integer>();
	
	static {
		registerAction(new SetupAction(), "setup");
	}
	
	@Override
	public void onActionCall(String action, MessageReceivedEvent event, String[] parameters) {
		if (parameters == null || !PermissionUtils.hasPermissions(event.getChannel(), BirthdayBot.getClient().getOurUser(), Permissions.SEND_MESSAGES))
			return;
		List<IRole> roles = event.getGuild().getRolesByName("Birthday Handler");
		if (roles.isEmpty()) {
			message(event.getChannel(), "There was no role found named `Birthday Handler`. You will need to create this role and grant it to yourself (unless owner) for this command to work.");
			return;
		}
		IUser author = event.getAuthor();
		IMessage message = event.getMessage();
		if (!message.getChannelMentions().isEmpty()) {
			IChannel channel = event.getMessage().getChannelMentions().get(0);
			if (!PermissionUtils.hasPermissions(channel, BirthdayBot.getClient().getOurUser(), Permissions.MANAGE_CHANNELS, Permissions.MANAGE_CHANNEL)) {
				message(event.getChannel(), "BirthdayBot doesn't have permission to edit the topic of channel " + channel.mention() + ".");
				return;
			}
			if (!PermissionUtils.hasPermissions(event.getGuild(), BirthdayBot.getClient().getOurUser(), Permissions.EMBED_LINKS)) {
				message(event.getChannel(), "BirthdayBot doesn't have permission to create embed links, aborting setup.");
				return;
			}
		}
		for (IRole role : roles) {
			if (author.hasRole(role) || event.getGuild().getOwner().equals(author)) {
				switch (update(author)) {
					case 1:
						scheduledMessage(event.getChannel(), 60, "Setup the channel that you would like the Birthdays to be stored in (BirthdayBot makes a list of everyones birthday). Better to be in it's own channel. Type **:tada: setup #channel-mention**");
						return;
					case 2:
						if (!message.getChannelMentions().isEmpty()) {
							IChannel channel = event.getMessage().getChannelMentions().get(0);
							String topic = channel.getTopic();
							if (topic == null)
								topic = "";
							channel.changeTopic(topic + "\n\nBirthdays channel.\nYou can view everyones birthdays from this channel.\n\n\n\nPlease do not modify this topic or else " + BirthdayBot.getClient().getOurUser().mention() + " will not be able to function properly.");
							message(event.getChannel(), "The main birthday channel has been set to " + channel.mention() + ".");
							storage.remove(author);
						}
						return;
				}
			}
		}
		scheduledMessage(event.getChannel(), 5, "**You don't have the correct permissions to use this command. :tada:**");
	}
	
	private int update(IUser author) {
		if (!storage.containsKey(author)) storage.put(author, 1);
		else {
			int spot = storage.get(author);
			storage.put(author, spot + 1);
		}
		int spot = storage.get(author);
		Executors.newScheduledThreadPool(1).schedule(() -> {
			if (storage.containsKey(author) && storage.get(author) == spot) storage.remove(author);
		}, 2, TimeUnit.MINUTES);
		return spot;
	}
}
