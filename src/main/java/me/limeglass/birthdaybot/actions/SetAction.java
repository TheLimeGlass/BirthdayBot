package me.limeglass.birthdaybot.actions;

import java.util.List;
import java.util.Optional;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.IllegalFieldValueException;
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
import sx.blah.discord.util.RequestBuffer;

public class SetAction extends Action {
	
	static {
		registerAction(new SetAction(), "set", "add");
	}
	
	@Override
	public void onActionCall(String action, MessageReceivedEvent event, String[] parameters) {
		if (parameters == null) return;
		DateTime date = null;
		try {
			date = DateTimeFormat.forPattern("dd/MM/yyyy").parseDateTime(parameters[0]);
		} catch (IllegalFieldValueException error) {
			scheduledMessage(event.getChannel(), 60, "Birthday **" + parameters[0] + "** is invalid, make sure the date format matches dd/mm/yyyy");
			return;
		}
		for (IChannel channel : event.getGuild().getChannels()) {
			if (channel.getTopic() != null && channel.getTopic().contains(BirthdayBot.getClient().getOurUser().mention())) {
				Optional<IMessage> message = channel.getFullMessageHistory().parallelStream()
						.filter(msg -> msg.getAuthor().equals(BirthdayBot.getClient().getOurUser()))
						.filter(msg -> !msg.getEmbeds().isEmpty())
						.findFirst();
				EmbedBuilder builder = new EmbedBuilder();
				builder.withColor(0, 255, 255);
				builder.withAuthorName("Birthdays");
				builder.withAuthorIcon("https://i.imgur.com/bMV38v8.png");
				DateTime next = date.withYear(DateTime.now().getYear());
				if (next.isBeforeNow()) next = next.plusYears(1);
				int days = Days.daysBetween(DateTime.now(), next).getDays() + 1;
				IUser user = event.getAuthor();
				List<IUser> mentions = event.getMessage().getMentions();
				if (!mentions.isEmpty()) {
					List<IRole> roles = event.getGuild().getRolesByName("Birthday Handler");
					if (roles.isEmpty()) return;
					for (IRole role : roles) {
						if (!user.hasRole(role) && !event.getGuild().getOwner().equals(user)) {
							scheduledMessage(event.getChannel(), 5, "**You don't have the correct permissions to set other users birthdays. You need the role `Birthday Handler` :tada:**");
							return;
						}
					}
					user = mentions.get(0);
				}
				builder.appendDescription(user.mention() + " - "
						+ date.toString("MMMMMMMMM dd, yyyy")
						+ " - (Birthday falls on a **" + next.toString("EEEEEEEEE") + "**) (**" + days + " days until**)\n");
				if (!message.isPresent()) RequestBuffer.request(() -> channel.sendMessage(builder.build()));
				else {
					IMessage msg = message.get();
					IEmbed embed = msg.getEmbeds().get(0);
					if (embed.getDescription() == null) {
						msg.edit(builder.build());
						continue;
					}
					for (String line : embed.getDescription().split("\n")) {
						if (!line.contains(user.mention())) {
							builder.appendDescription(line + "\n");
						}
					}
					RequestBuffer.request(() -> msg.edit(builder.build()));
				}
				scheduledMessage(event.getChannel(), 60, "Birthday **" + date.toString("MMMMMMMMM dd, yyyy") + "** was added for " + user.getName());
				break;
			}
		}
	}
}
