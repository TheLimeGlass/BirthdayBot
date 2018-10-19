package me.limeglass.birthdaybot.tasks;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TimeZone;
import java.util.TimerTask;

import org.apache.commons.configuration2.Configuration;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Seconds;
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
		for (IGuild guild : BirthdayBot.getClient().getGuilds()) {
			for (IChannel channel : guild.getChannels()) {
				if (channel.getTopic() != null && channel.getTopic().contains(BirthdayBot.getClient().getOurUser().mention())) {
					//Grab existing embed
					Optional<IMessage> message = channel.getMessageHistory(1000).parallelStream()
							.filter(msg -> msg.getAuthor().equals(BirthdayBot.getClient().getOurUser()))
							.filter(msg -> !msg.getEmbeds().isEmpty())
							.findFirst();
					if (message.isPresent()) {
						//Setup the embed
						EmbedBuilder builder = new EmbedBuilder();
						builder.withColor(0, 255, 255);
						builder.withAuthorName("Birthdays");
						builder.withAuthorIcon("https://i.imgur.com/bMV38v8.png");
						IMessage msg = message.get();
						IEmbed embed = msg.getEmbeds().get(0);
						if (embed.getDescription() == null) {
							msg.delete();
							continue;
						}
						//Birthday getter and formatter
						Map<String[], DateTime> dates = new HashMap<String[], DateTime>();
						String[] lines = embed.getDescription().split("\n");
						if (lines.length > 1) {
							for (String line : lines) {
								String[] values = line.split(" - ");
								if (values.length == 3) {
									DateTime lineDate = DateTimeFormat.forPattern("MMMMMMMMM dd, yyyy").parseDateTime(values[1]);
									DateTime next = lineDate.withYear(DateTime.now().getYear());
									if (next.isBeforeNow()) next = next.plusYears(1);
									dates.put(values, next);
								}
							}
						}
						//Loop sorted birthdays
						for (Entry<String[], DateTime> sorted : sortByValue(dates).entrySet()) {
							DateTime date = sorted.getValue();
							//Birthday updater
							int days = Days.daysBetween(DateTime.now(), date).getDays() + 1;
							builder.appendDescription(sorted.getKey()[0] + " - " + sorted.getKey()[1] + " - (Birthday falls on a **" + sorted.getValue().toString("EEEEEEEEE") + "**) (**" + days + " days until**)\n");
							//Birthday broadcaster
							Seconds seconds = Seconds.secondsBetween(DateTime.now(), date);
							if (seconds.getSeconds() <= 1) {
								Configuration configuration = BirthdayBot.getConfiguration();
								Object object = configuration.getProperties(guild.getStringID());
								if (object == null) {
									//flawed
									configuration.setProperty(guild.getStringID(), seconds.getSeconds() + ":" + sorted.getKey()[0]);
								} else {
									String input = (String) object;
									String[] data = input.split(":", 1);
									if (data.length <= 1) {
										configuration.setProperty(guild.getStringID(), null);
										continue;
									}
									long milliseconds = Long.parseLong(data[0]);
									//flawed
									if (System.currentTimeMillis() - milliseconds >= 0) {
										
									}
								}
							}
						}
						SimpleDateFormat formatter = new SimpleDateFormat("MMMMM d, yyyy '**at**' hh:mm aaa");
						formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
						builder.appendDescription("\n*Last updated: **" + formatter.format(Date.from(embed.getTimestamp())) + "** UTC*\n");
						builder.appendDescription("\n**Source code/Report issues**: https://github.com/TheLimeGlass/BirthdayBot");
						msg.edit(builder.build());
						break;
					}
				}
			}
		}
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