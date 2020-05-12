package org.mswsplex.anticheat.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.mswsplex.anticheat.msws.NOPE;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import io.netty.util.internal.ThreadLocalRandom;

public class MSG {
	public static NOPE plugin;

	/**
	 * Returns the string with &'s being color codes
	 * 
	 * @param msg the message to replace
	 * @return returns colored msg
	 */
	public static String color(String msg) {
		if (msg == null || msg.isEmpty())
			return null;
		return ChatColor.translateAlternateColorCodes('&', msg);
	}

	/**
	 * Returns string with camel case, and with _'s replaced with spaces
	 * 
	 * @param string hello_how is everyone
	 * @return Hello How Is Everyone
	 */
	public static String camelCase(String string) {
		String prevChar = " ";
		String res = "";
		for (int i = 0; i < string.length(); i++) {
			if (i > 0)
				prevChar = string.charAt(i - 1) + "";
			if (prevChar.matches("[a-zA-Z]")) {
				res = res + ((string.charAt(i) + "").toLowerCase());
			} else {
				res = res + ((string.charAt(i) + "").toUpperCase());
			}
		}
		return res.replace("_", " ");
	}

	/**
	 * Gets a string from lang.yml
	 * 
	 * @param id  key id of the string to get
	 * @param def default string in case lang.yml doesn't have the key
	 * @return
	 */
	public static String getString(String id, String def) {
		return plugin.lang.contains(id) ? plugin.lang.getString(id) : "[" + id + "] " + def;
	}

	/**
	 * Sends the message colored to the sender CommandSender
	 * 
	 * @param sender CommandSender to send message to
	 * @param msg    Message to send
	 */
	public static void tell(CommandSender sender, String msg) {
		if (msg != null && !msg.isEmpty())
			sender.sendMessage(color(msg));
	}

	/**
	 * Sends a message to everyone in a world
	 * 
	 * @param world World to send message to
	 * @param msg   Message to send
	 */
	public static void tell(World world, String msg) {
		if (world != null && msg != null) {
			for (Player target : world.getPlayers()) {
				tell(target, msg);
			}
		}
	}

	/**
	 * Sends a message to all players with a specific permission
	 * 
	 * @param perm Permission to require
	 * @param msg  Message to send
	 */
	public static void tell(String perm, String msg) {
		for (Player target : Bukkit.getOnlinePlayers()) {
			if (target.hasPermission(perm))
				tell(target, msg);
		}
	}

	/**
	 * Announces a message to all players
	 * 
	 * @param msg Message to announce
	 */
	public static void announce(String msg) {
		Bukkit.getOnlinePlayers().forEach((player) -> {
			tell(player, msg);
		});
	}

	/**
	 * Sends a no permission message to the target
	 * 
	 * @param sender CommandSender to send message to
	 */
	public static void noPerm(CommandSender sender, String perm) {
		tell(sender, getString("NoPermission", "Insufficient Permissions").replace("%perm%", perm));
	}

	/**
	 * Logs a message to console
	 * 
	 * @param msg Message to log
	 */
	public static void log(String msg) {
		tell(Bukkit.getConsoleSender(), "[" + plugin.getDescription().getName() + "] " + msg);
	}

	/**
	 * Colored boolean
	 * 
	 * @param bool true/false
	 * @return Green True or Red False
	 */
	public static String TorF(Boolean bool) {
		if (bool) {
			return "&aTrue&r";
		} else {
			return "&cFalse&r";
		}
	}

	/**
	 * Sends a help message with specified pages
	 * 
	 * @param sender  CommandSender to send message to
	 * @param page    Page of help messages
	 * @param command Command to send help to
	 */
	public static void sendHelp(CommandSender sender, int page, String command) {
		if (!plugin.lang.contains("Help." + command.toLowerCase())) {
			tell(sender, getString("UnknownCommand", "There is no help available for this command."));
			return;
		}
		List<String> help = plugin.lang.getStringList("Help." + command.toLowerCase()), list = new ArrayList<String>();
		for (String res : help) {
			if (res.startsWith("perm:")) {
				String perm = "";
				res = res.substring(5, res.length());
				for (char a : res.toCharArray()) {
					if (a == ' ')
						break;
					perm = perm + a;
				}
				if (!sender.hasPermission(perm))
					continue;
				res = res.replace(perm + " ", "");
			}
			list.add(res);
		}
		for (String line : list) {
			tell(sender, line);
		}
		if (command.equals("default")) {
			String bottom = "&l&4[&c&lNOPE&4&l] &ev" + plugin.getDescription().getVersion() + " &7created by &bMSWS";
			if (plugin.getPluginInfo() != null)
				bottom += " &7(Online Version is &e" + plugin.getPluginInfo().getVersion() + "&7)";
			MSG.tell(sender, bottom);
		}
	}

	/**
	 * Returns a text progress bar
	 * 
	 * @param prog   0-total double value of progress
	 * @param total  Max amount that progress bar should represent
	 * @param length Length in chars for progress bar
	 * @return
	 */
	public static String progressBar(double prog, double total, int length) {
		return progressBar("&a\u258D", "&c\u258D", prog, total, length);
	}

	/**
	 * Returns a text progress bar with specified chars
	 * 
	 * @param progChar   Progress string to represent progress
	 * @param incomplete Incomplete string to represent amount left
	 * @param prog       0-total double value of progress
	 * @param total      Max amount that progress bar should represent
	 * @param length     Length in chars for progress bar
	 * @return
	 */
	public static String progressBar(String progChar, String incomplete, double prog, double total, int length) {
		String disp = "";
		double progress = Math.abs(prog / total);
		int len = length;
		for (double i = 0; i < len; i++) {
			if (i / len < progress) {
				disp = disp + progChar;
			} else {
				disp = disp + incomplete;
			}
		}
		return color(disp);
	}

	/**
	 * Returns a string for shortened decimal
	 * 
	 * @param decimal Decimal to shorten
	 * @param length  Amount of characters after the ., will add on 0's to meet
	 *                minimum
	 * @return Input: "5978.154123" (Length of 3) Output: "5978.154"
	 */
	public static String parseDecimal(String decimal, int length) {
		if (decimal.contains(".")) {
			if (decimal.split("\\.").length == 1)
				decimal += "0";
			if (decimal.split("\\.")[1].length() > 2) {
				decimal = decimal.split("\\.")[0] + "."
						+ decimal.split("\\.")[1].substring(0, Math.min(decimal.split("\\.")[1].length(), length));
			}
		} else {
			decimal += ".0";
		}
		while (decimal.split("\\.")[1].length() < length)
			decimal += "0";
		return decimal;
	}

	/**
	 * Returns a string for shortened decimal
	 * 
	 * @param decimal Decimal to shorten
	 * @param length  Amount of characters after the .
	 * @return Input: 5978.154123 (Length of 3) Output: "5978.154"
	 */
	public static String parseDecimal(double decimal, int length) {
		return parseDecimal(decimal + "", length);
	}

	public static String getTime(Double mils) {
		boolean isNegative = mils < 0;
		double mil = Math.abs(mils);
		String names[] = { "milliseconds", "seconds", "minutes", "hours", "days", "weeks", "months", "years", "decades",
				"centuries" };
		String sNames[] = { "millisecond", "second", "minute", "hour", "day", "week", "month", "year", "decade",
				"century" };
		Double length[] = { 1.0, 1000.0, 60000.0, 3.6e+6, 8.64e+7, 6.048e+8, 2.628e+9, 3.154e+10, 3.154e+11,
				3.154e+12 };
		String suff = "";
		for (int i = length.length - 1; i >= 0; i--) {
			if (mil >= length[i]) {
				if (suff.equals(""))
					suff = names[i];
				mil = mil / length[i];
				if (mil == 1) {
					suff = sNames[i];
					// suff = suff.substring(0, suff.length() - 1);
				}
				break;
			}
		}
		String name = mil + "";
		if (Math.round(mil) == mil) {
			name = (int) Math.round(mil) + "";
		}
		if (name.contains(".")) {
			if (name.split("\\.")[1].length() > 2)
				name = parseDecimal(name, 2);
		}
		if (isNegative)
			name = "-" + name;
		return name + " " + suff;
	}

	public static double getMills(String msg) {
		String val = "";
		double mills = -1;
		for (char c : msg.toCharArray()) {
			if ((c + "").matches("[0-9\\.-]")) {
				val = val + c;
			} else {
				break;
			}
		}
		try {
			mills = Double.valueOf(val) * 1000;
		} catch (Exception e) {
			return 0.0;
		}

		Double amo[] = { 60.0, 3600.0, 86400.0, 604800.0, 2.628e+6, 3.154e+7, 3.154e+8, 3.154e+9 };
		String[] names = { "m", "h", "d", "w", "mo", "y", "de", "c" };
		for (int i = amo.length - 1; i >= 0; i--) {
			if (msg.toLowerCase().contains(names[i])) {
				mills = mills * amo[i];
				break;
			}
		}
		return mills;
	}

	public static String getVlColor(int vl) {
		int maxVl = plugin.config.getInt("VlForBanwave");
		vl = Math.min(vl, maxVl);
		List<String> colors = plugin.config.getStringList("VlColors");
		int index = (int) Math.floor(((double) vl / (double) maxVl) * (colors.size()));
		String color = colors.get(Math.min(index, colors.size() - 1));

		return color;
	}

	public static String genUUID(int length) {
		String[] keys = new String[100];
		int pos = 0;
		for (int i = 0; i < 26; i++) {
			keys[i + pos] = ((char) (i + 65)) + "";
		}
		pos += 26;
		for (int i = 0; i < 10; i++) {
			keys[i + pos] = i + "";
		}
		pos += 10;
		String res = "";
		for (int i = 0; i < length; i++)
			res = res + keys[(int) Math.floor(ThreadLocalRandom.current().nextDouble(pos))];
		return res;
	}

	public static void sendPluginMessage(Player player, String message) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Forward");
		out.writeUTF("ALL");
		out.writeUTF("NOPE");
		out.writeUTF(message);
		if (player == null)
			player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
		if (player == null)
			return;
		player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
	}
}
