package me.limeglass.birthdaybot.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TimerTask;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;

import me.limeglass.birthdaybot.BirthdayBot;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IEmbed;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.EmbedBuilder;

public class UpdateTask extends TimerTask {

	@Override
	public void run() {
		try {
			Thread.sleep(BirthdayBot.time);
			for (IGuild guild : BirthdayBot.getClient().getGuilds()) {
				loop : for (IChannel channel : guild.getChannels()) {
					if (channel.getTopic() != null && channel.getTopic().contains(BirthdayBot.getClient().getOurUser().mention())) {
						Optional<IMessage> message = channel.getFullMessageHistory().parallelStream()
								.filter(msg -> msg.getAuthor().equals(BirthdayBot.getClient().getOurUser()))
								.filter(msg -> !msg.getEmbeds().isEmpty())
								.findFirst();
						if (message.isPresent()) {
							EmbedBuilder builder = new EmbedBuilder();
							builder.withColor(0, 255, 255);
							builder.withAuthorName("Birthdays");
							builder.withAuthorIcon("https://i.imgur.com/bMV38v8.png");
							IMessage msg = message.get();
							IEmbed embed = msg.getEmbeds().get(0);
							if (!embed.getDescription().contains("\n")) continue loop;
							Map<String[], DateTime> dates = new HashMap<String[], DateTime>();
							for (String line : embed.getDescription().split("\n")) {
								String[] values = line.split(" - ");
								DateTime lineDate = DateTimeFormat.forPattern("MMMMMMMMM dd, yyyy").parseDateTime(values[1]);
								DateTime next = lineDate.withYear(DateTime.now().getYear());
								if (next.isBeforeNow()) next = next.plusYears(1);
								dates.put(values, next);
							}
							for (Entry<String[], DateTime> sorted : sortByValue(dates).entrySet()) {
								int userDays = Days.daysBetween(DateTime.now(), sorted.getValue()).getDays() + 1;
								builder.appendDescription(sorted.getKey()[0] + " - " + sorted.getKey()[1] + " - (Next birthday falls on **" + sorted.getValue().toString("EEEEEEEEE") + "**) (**" + userDays + " days until**)\n");
							}
							msg.edit(builder.build());
							break;
						}
					}
				}
			}
		} catch (InterruptedException e) {}
	}
	
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
		List<Entry<K, V>> list = new ArrayList<>(map.entrySet());
		list.sort(Entry.comparingByValue());
		Map<K, V> result = new LinkedHashMap<>();
		for (Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}
}