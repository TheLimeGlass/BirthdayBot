package me.limeglass.birthdaybot.actions;

import me.limeglass.birthdaybot.BirthdayBot;
import me.limeglass.birthdaybot.objects.Action;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

public class HelpAction extends Action {

	static {
		registerAction(new HelpAction(), "help");
	}
	
	@Override
	public void onActionCall(String action, MessageReceivedEvent event, String[] parameters) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.withAuthorName("Birthday Bot Help Menu");
		builder.withAuthorIcon("https://i.imgur.com/bMV38v8.png");
		builder.appendField("Bot Information", "BirthdayBot is a bot that allows users to input their birthday and grant other users the ability to recognize when their birthday is. If you setup BirthdayBot, you should have a dedicated channel for viewing birthdays and when a birthday happens it will broadcast that birthday in the main channel defined in the servers' settings."
				+ "\n**Links:** [Bot Invite](https://discordapp.com/api/oauth2/authorize?client_id=454773255824211968&permissions=1010035792&scope=bot) and [Source code](https://github.com/TheLimeGlass/BirthdayBot)"
				+ "\n**Prefixes:** " + BirthdayBot.getClient().getOurUser().mention() + " and :tada:"
				+ "               [] = optional arguments.", false);
		builder.appendField(":tada: help", "Messages the help menu.", false);
		builder.appendField(":tada: setup", "Setup the channels that " + BirthdayBot.getClient().getOurUser().mention() + " should be allocated too. Requires the role: `Birthday Handler`", false);
		builder.appendField(":tada: set dd/mm/yyyy [user]",
				"The main command users use to set their birthday, the user argument is optional and requires the role: `Birthday Handler` to set a mentioned users birthday."
				+ "\n➤ *Example:* :tada: `set 13/3/1995 @BirthdayBot`", false);
		builder.appendField(":tada: remove [user]",
				"Command for users to remove their birthday, the user argument is optional and requires the role: `Birthday Handler` to remove a mentioned users birthday."
				+ "\n➤ *Example:* :tada: `remove @BirthdayBot`", false);
		builder.withColor(0, 255, 255);
		RequestBuffer.request(() -> event.getChannel().sendMessage(builder.build()));
	}
}
