package me.limeglass.birthdaybot.actions;

import me.limeglass.birthdaybot.BirthdayBot;
import me.limeglass.birthdaybot.objects.Action;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

public class AdminAction extends Action {

	static {
		registerAction(new AdminAction(), "stats", "statistics");
	}
	
	@Override
	public void onActionCall(String action, MessageReceivedEvent event, String[] parameters) {
		if (event.getAuthor().getStringID().equals("91356983042539520")) {
			EmbedBuilder builder = new EmbedBuilder();
			builder.withAuthorName("Birthday Bot Admin Menu");
			builder.withAuthorIcon("https://i.imgur.com/bMV38v8.png");
			builder.appendField("Servers", BirthdayBot.getClient().getGuilds().size() + "", true);
			builder.appendField("Shards", BirthdayBot.getClient().getShardCount() + "", true);
			builder.appendField("Users", BirthdayBot.getClient().getUsers().size() + "", true);
			builder.appendField("Voice channels", BirthdayBot.getClient().getVoiceChannels().size() + "", true);
			builder.appendField("Text channels", BirthdayBot.getClient().getChannels().size() + "", true);
			builder.appendField("Roles", BirthdayBot.getClient().getRoles().size() + "", true);
			builder.appendField("Regions", BirthdayBot.getClient().getRegions().size() + "", true);
			builder.appendField("Categories", BirthdayBot.getClient().getCategories().size() + "", true);
			builder.appendField("My Master", BirthdayBot.getClient().getApplicationOwner().mention(), true);
			builder.withColor(0, 255, 255);
			RequestBuffer.request(() -> event.getChannel().sendMessage(builder.build()));
		}
	}
}
