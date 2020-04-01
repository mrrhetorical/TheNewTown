package com.rhetorical.town.util;

import org.bukkit.Bukkit;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

public class DateTimeConverter {

	public static LocalDateTime convert(String from) {
		try {
			DateFormat format = new SimpleDateFormat("M/d/yyyy h:m a", Locale.ENGLISH);
			Date date = format.parse(from);

			return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
		} catch (Exception e) {
			Bukkit.getServer().getLogger().severe("Could not convert date from string!");
			return null;
		}
	}

	public static String convert(LocalDateTime from) {
		if (from == null)
			return "";
		DateFormat format = new SimpleDateFormat("M/d/yyyy h:m a", Locale.ENGLISH);

		Date date = Date.from(from.atZone(ZoneId.systemDefault()).toInstant());

		return format.format(date);
	}


}
