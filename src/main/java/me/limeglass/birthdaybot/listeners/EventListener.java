package me.limeglass.birthdaybot.listeners;

import java.util.List;

import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.EmojiParser;

import me.limeglass.birthdaybot.BirthdayBot;
import me.limeglass.birthdaybot.objects.Command;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;
import sx.blah.discord.handle.obj.ActivityType;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.StatusType;

public class EventListener {

	@EventSubscriber
	public void onStartup(ReadyEvent event) {
		BirthdayBot.getClient().changePresence(StatusType.ONLINE, ActivityType.WATCHING, EmojiParser.parseToUnicode(BirthdayBot.getPrefix() + " help"));
	}
	
	@EventSubscriber
	public void onMessageReceivedEvent(MessageReceivedEvent event) {
		IMessage message = event.getMessage();
		String content = message.getContent();
		String prefix = content.split(" ")[0];
		IUser bot = BirthdayBot.getClient().getOurUser();
		List<IUser> mentions = event.getMessage().getMentions();
		String parsed = EmojiParser.parseToUnicode(BirthdayBot.getPrefix());
		if (EmojiManager.isEmoji(prefix) && parsed.equals(prefix)) {
			content = content.replaceFirst(parsed + " ", "");
			new Command(event, content);
		} else if (mentions != null && !mentions.isEmpty() && mentions.get(0).equals(bot)) {
			content = event.getMessage().getFormattedContent();
			content = content.replaceFirst("@" + bot.getName() + " ", "");
			new Command(event, content);
		}
	}
	
	@EventSubscriber
	public void onReactionAddEvent(ReactionAddEvent event) {
		return;
		/*if (event.getAuthor() == BirthdayBot.getClient().getOurUser()) {
			System.out.println("test reaction0 " + event.getReaction().getEmoji().getName());
			System.out.println("test reaction1 " + event.getReaction().getEmoji().getLongID());
			if (event.getReaction().getEmoji().getName().equals(":arrow_right:")) {
				System.out.println("test reaction2");
				List<IEmbed> embeds = event.getMessage().getEmbeds();
				if (embeds != null && embeds.size() > 0) {
					IEmbed embed = embeds.get(0);
					System.out.println("test reaction3");
					if (embed != null && embed.getAuthor().getName().equals("Birthday Bot Help Menu")) {
						System.out.println("test reaction4");
						event.getMessage().removeReaction(event.getUser(), event.getReaction().getEmoji());
					}
				}
			}
		}
		*/
	}

}