package xyz.msws.nope.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import io.netty.util.internal.ThreadLocalRandom;
import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.checks.Check;
import xyz.msws.nope.modules.data.CPlayer;
import xyz.msws.nope.utils.Utils.Age;

public class MSG {

	public static ChatColor ALL = ChatColor.WHITE;
	public static ChatColor PLAYER = ChatColor.YELLOW;
	public static ChatColor STAFF = ChatColor.GOLD;

	public static ChatColor ADMIN = ChatColor.RED;

	public static ChatColor DEFAULT = ChatColor.GRAY;

	public static ChatColor FORMATTER = ChatColor.GRAY;
	public static ChatColor FORMAT_INFO = ChatColor.GREEN;
	public static ChatColor FORMAT_SEPARATOR = ChatColor.YELLOW;

	public static ChatColor NUMBER = ChatColor.YELLOW;
	public static ChatColor TIME = ChatColor.GOLD;
	public static ChatColor DATE = ChatColor.DARK_GREEN;
	public static ChatColor MONEY = ChatColor.GREEN;

	public static ChatColor SUBJECT = ChatColor.AQUA;

	public static ChatColor PREFIX = ChatColor.BLUE;

	public static ChatColor ERROR = ChatColor.RED;
	public static ChatColor FAIL = ChatColor.RED;
	public static ChatColor SUCCESS = ChatColor.GREEN;

	public static ChatColor BOLD = ChatColor.BOLD;
	public static ChatColor ITALIC = ChatColor.ITALIC;
	public static ChatColor MAGIC = ChatColor.MAGIC;
	public static ChatColor UNDER = ChatColor.UNDERLINE;
	public static ChatColor STRIKE = ChatColor.STRIKETHROUGH;
	public static ChatColor RESET = ChatColor.RESET;

	public static NOPE plugin;

	public static void log(String message) {
		plugin.getLogger().log(Level.INFO, MSG.color(message));
	}

	public static void warn(String message) {
		plugin.getLogger().log(Level.WARNING, MSG.color(message));
	}

	public static void error(String message) {
		plugin.getLogger().log(Level.SEVERE, MSG.color(message));
	}

	public static void printStackTrace() {
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
		for (int i = 2; i < elements.length; i++)
			System.out.println(elements[i].toString());
	}

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
	 * @deprecated Will likely be replaced in the future
	 */
	public static String getString(String id, String def) {
		return plugin.getLang().contains(id) ? plugin.getLang().getString(id) : "[" + id + "] " + def;
	}

	public static List<String> getStringList(String id) {
		return plugin.getLang().getStringList(id);
	}

	/**
	 * Sends the message colored to the sender CommandSender
	 * 
	 * @param sender CommandSender to send message to
	 * @param msg    Message to send
	 */
	public static void tell(CommandSender sender, String msg) {
		if (msg == null || msg.isEmpty())
			return;
		if (sender instanceof OfflinePlayer)
			sender.sendMessage(color(papi((OfflinePlayer) sender, msg)));
		else
			sender.sendMessage(color(msg));
	}

	public static void tell(CommandSender sender, String module, String message) {
		tell(sender, PREFIX + module + "> " + DEFAULT + message);
	}

	/**
	 * Replaces all PAPI placeholders appropriate, automatically makes sure PAPI is
	 * enabled to avoid {@link ClassNotFoundException}.
	 * 
	 * @param sender
	 * @param msg
	 * @return
	 */
	public static String papi(OfflinePlayer sender, String msg) {
		if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
			return msg;
		}
		return new PAPIHook().setPlaceholders(sender, msg);
	}

	/**
	 * Replaces the standard placeholders related to checks.
	 * 
	 * @param s
	 * @param cp
	 * @param check
	 * @return
	 */
	public static String replaceCheckPlaceholder(String s, CPlayer cp, Check check) {

		s = s.replace("%check%", check.getCategory()).replace("%debug%", check.getDebugName())
				.replace("%vl%", cp.getVL(check.getCategory()) + "").replace("%player%", cp.getPlayer().getName())
				.replace("%world%",
						cp.getPlayer().isOnline() ? cp.getPlayer().getPlayer().getWorld().getName() : "Offline")
				.replace("%uuid%", cp.getPlayer().getUniqueId().toString())
				.replace("%nuuid%", cp.getPlayer().getUniqueId().toString().replace("-", ""))
				.replace("%server%", plugin.getServerName());

		return papi(cp.getPlayer(), s);
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
		if (!plugin.getLang().contains("Help." + command.toLowerCase())) {
			tell(sender, getString("UnknownCommand", "There is no help available for this command."));
			return;
		}
		List<String> help = plugin.getLang().getStringList("Help." + command.toLowerCase()),
				list = new ArrayList<String>();
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
			String bottom = "&l&4[&c&lNOPE&4&l] &e" + plugin.getDescription().getVersion() + " &7created by &bMSWS";
			if (plugin.getPluginInfo() != null)
				bottom += " &7(Online Version is "
						+ (plugin.getPluginInfo().outdated() == Age.OUTDATED_VERSION ? "&a"
								: (plugin.getPluginInfo().outdated() == Age.DEVELOPER_VERSION ? "&c" : "&b"))
						+ plugin.getPluginInfo().getVersion() + "&7)";
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

	public static String getTime(long mils) {
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

	public static void sendMethods(Class<?> cl) {
		MSG.log("Declared methods for " + cl.getName());
		StringBuilder builder = new StringBuilder();

		for (Method m : cl.getDeclaredMethods()) {
			builder.append(m.getReturnType().getName()).append(" ");
			builder.append(m.getName()).append(" (");
			for (Class<?> c : m.getParameterTypes()) {
				builder.append(c.getName()).append(" ");
			}

			builder.append(") ");
		}
		MSG.log("All methods for " + cl.getName());
		builder = new StringBuilder();

		for (Method m : cl.getMethods()) {
			builder.append(m.getReturnType().getName()).append(" ");
			builder.append(m.getName()).append(" (");
			for (Class<?> c : m.getParameterTypes()) {
				builder.append(c.getName()).append(" ");
			}

			builder.append(") ");
		}

		MSG.log(builder.toString());
	}

	public static void sendFields(Class<?> cl) {
		MSG.log("Declared methods for " + cl.getName());
		StringBuilder builder = new StringBuilder();

		for (Field f : cl.getDeclaredFields()) {
			builder.append(f.getType().getName()).append(" ");
			builder.append(f.getName());
			builder.append(", ");
		}
		MSG.log("All fields for " + cl.getName());
		builder = new StringBuilder();

		for (Field f : cl.getFields()) {
			builder.append(f.getType().getName()).append(" ");
			builder.append(f.getName());
			builder.append(", ");
		}

		MSG.log(builder.toString());
	}
}
