package me.limeglass.birthdaybot.actions;

import java.util.List;
import java.util.Optional;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import me.limeglass.birthdaybot.BirthdayBot;
import me.limeglass.birthdaybot.objects.Action;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IEmbed;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class RemoveAction extends Action {
	
	static {
		registerAction(new RemoveAction(), "remove", "delete");
	}
	
	@Override
	public void onActionCall(String action, MessageReceivedEvent event, String[] parameters) {
		if (parameters == null) return;
		for (IChannel channel : event.getGuild().getChannels()) {
			if (channel.getTopic() != null && channel.getTopic().contains(BirthdayBot.getClient().getOurUser().mention())) {
				Optional<IMessage> message = channel.getFullMessageHistory().parallelStream()
						.filter(msg -> msg.getAuthor().equals(BirthdayBot.getClient().getOurUser()))
						.filter(msg -> !msg.getEmbeds().isEmpty())
						.findFirst();
				IUser user = event.getAuthor();
				List<IUser> mentions = event.getMessage().getMentions();
				if (!mentions.isEmpty()) {
					//TODO move to a util finder
					List<IRole> roles = event.getGuild().getRolesByName("Birthday Handler");
					if (roles.isEmpty()) return;
					for (IRole role : roles) {
						if (!user.hasRole(role) && !event.getGuild().getOwner().equals(user)) {
							scheduledMessage(event.getChannel(), 5, "**You don't have the correct permissions to remove other users birthdays. You need the role `Birthday Handler` :tada:**");
							return;
						}
					}
					user = mentions.get(0);
				}
				EmbedBuilder builder = new EmbedBuilder();
				builder.withColor(0, 255, 255);
				builder.withAuthorName("Birthdays");
				builder.withAuthorIcon("https://i.imgur.com/bMV38v8.png");
				if (message.isPresent()) {
					IMessage msg = message.get();
					IEmbed embed = msg.getEmbeds().get(0);
					for (String line : embed.getDescription().split("\n")) {
						if (!line.contains(user.mention())) {
							String[] values = line.split(" - ");
							DateTime lineDate = DateTimeFormat.forPattern("MMMMMMMMM dd, yyyy").parseDateTime(values[1]);
							DateTime next = lineDate.withYear(DateTime.now().getYear());
							if (next.isBeforeNow()) next = next.plusYears(1);
							int userDays = Days.daysBetween(DateTime.now(), next).getDays() + 1;
							builder.appendDescription(values[0] + " - " + values[1] + " - (Next birthday falls on a **" + lineDate.toString("EEEEEEEEE") + "**) (**" + userDays + " days until**)\n");
						}
					}
					msg.edit(builder.build());
				}
				scheduledMessage(event.getChannel(), 60, "Birthday for user **" + user.getName() + "** was removed.");
				break;
			}
		}
	}
}
