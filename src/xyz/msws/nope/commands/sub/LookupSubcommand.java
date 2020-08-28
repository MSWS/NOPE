package xyz.msws.nope.commands.sub;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;
import net.md_5.bungee.api.chat.HoverEvent;
import xyz.msws.nope.NOPE;
import xyz.msws.nope.commands.CommandResult;
import xyz.msws.nope.commands.Subcommand;
import xyz.msws.nope.events.actions.ActionExecuteEvent;
import xyz.msws.nope.modules.actions.actions.BanAction;
import xyz.msws.nope.utils.MSG;

public class LookupSubcommand extends Subcommand implements Listener {
	private List<String> tokens = new ArrayList<>();

	private File logs;

	public LookupSubcommand(NOPE plugin) {
		super(plugin);

		logs = new File(plugin.getDataFolder(), "logs");
		if (logs == null || !logs.exists() || logs.list() == null)
			return;
		for (String f : logs.list()) {
			tokens.add(f.substring(0, f.length() - 4));
		}

		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@Override
	public List<String[]> tabCompletions(CommandSender sender, String[] args) {
		List<String[]> result = new ArrayList<>();
		result.add(tokens.toArray(new String[0]));
		return result;
	}

	@Override
	public String getName() {
		return "lookup";
	}

	@Override
	public String getUsage() {
		return "[token/player]";
	}

	@Override
	public CommandResult execute(CommandSender sender, String[] args) {
		if (args.length < 2)
			return CommandResult.MISSING_ARGUMENT;

		String key = args[1];
		MSG.tell(sender, MSG.getString("Command.Lookup.Querying", "Grabbing ban details..."));

		new BukkitRunnable() {
			@Override
			public void run() {
				if (key.length() == 16) {
					File logs = new File(plugin.getDataFolder(), "logs");
					File log = new File(logs, key + ".log");
					if (!log.exists()) {
						MSG.tell(sender, CommandResult.INVALID_ARGUMENT.getMessage());
						return;
					}

					List<String> result = new ArrayList<>();

					try {
						FileReader fread = new FileReader(log);
						BufferedReader reader = new BufferedReader(new FileReader(log));

						String line;
						while ((line = reader.readLine()) != null)
							result.add(line.trim());

						reader.close();
						fread.close();
					} catch (IOException e) {
						e.printStackTrace();
					}

					List<String> res = parseLog(result, key);
					if (res == null) {
						MSG.tell(sender, CommandResult.INVALID_ARGUMENT.getMessage());
						return;
					}
					res.forEach(s -> MSG.tell(sender, s));
					return;
				}

				List<String> result = new ArrayList<>();
				try {
					URL url = new URL("https://hastebin.com/raw/" + key);
					URLConnection conn = url.openConnection();
					conn.setRequestProperty("User-Agent", "NOPE/MC-" + plugin.getDescription().getVersion());
					InputStreamReader iread = new InputStreamReader(conn.getInputStream());
					BufferedReader reader = new BufferedReader(iread);
					String line;

					while ((line = reader.readLine()) != null)
						result.add(line.trim());

					reader.close();
					iread.close();
				} catch (IOException e) {
					e.printStackTrace();
					MSG.tell(sender, CommandResult.INVALID_ARGUMENT.getMessage());
				}

				List<String> res = parseLog(result, key);
				if (res == null) {
					MSG.tell(sender, CommandResult.INVALID_ARGUMENT.getMessage());
					return;
				}
				res.forEach(s -> MSG.tell(sender, s));

				if (sender instanceof Player) {
					ComponentBuilder comp = new ComponentBuilder(MSG.color("&4NOPE > &cYou can view the full log "));
					ComponentBuilder here = new ComponentBuilder("here").color(ChatColor.YELLOW);
					here.event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://hastebin.com/" + key));
					here.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
							new ComponentBuilder(MSG.color("&7Goto &ehttps://hastebin.com" + key)).create()));
					comp.append(here.create(), FormatRetention.NONE);
					comp.append(".", FormatRetention.NONE).color(ChatColor.GRAY);
					((Player) sender).spigot().sendMessage(comp.create());
				}
			}
		}.runTaskAsynchronously(plugin);

		return CommandResult.SUCCESS;
	}

	public List<String> parseLog(List<String> log, String key) {
		if (!log.get(0).startsWith("Starting new log for "))
			return null;
		List<String> result = new ArrayList<>();
		String player = log.get(0).substring("Starting new log for ".length(), log.get(0).indexOf(" ("));
		String uuid = log.get(0).substring(log.get(0).indexOf("(") + 1, log.get(0).indexOf(")"));

		result.add(String.format("&e%s &7came back with a log for &c%s &7(&3%s&7)", key, player, uuid));

		String hack = "UNKNOWN", vl = "N/A";
		Map<String, Integer> vls = new HashMap<>();

		for (String line : log) {
			if (!line.startsWith(player + " was ultimately banned for "))
				continue;
			hack = line.substring((player + " was ultimately banned for ").length(), line.indexOf(" at a VL of "));
			vl = line.substring(line.indexOf(" at a VL of ") + " at a VL of ".length());
			break;
		}

		List<String> vlLines = log.subList(log.indexOf(player + "'s flags:") + 1, log.size());
		for (String line : vlLines) {
			if (line.isEmpty())
				break;
			vls.put(line.split(":")[0], Integer.parseInt(line.split(":")[1].trim()));
		}

		result.add(String.format("&c%s &7was banned for &3%s&7, totalling a VL of &4%s&7.", player, hack, vl));

		result.add(" ");

		result.add("&7Total VLs:");

		Map<String, Integer> ranked = vls.entrySet().stream()
				.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

		for (Entry<String, Integer> entry : ranked.entrySet()) {
			result.add("&e" + entry.getKey() + ": &b" + entry.getValue());
		}

		return result;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onAction(ActionExecuteEvent event) {
		if (!event.getAction().getClass().equals(BanAction.class))
			return;
		if (logs == null || !logs.exists() || logs.list() == null)
			return;
		new BukkitRunnable() {
			@Override
			public void run() {

				tokens.clear();
				for (String f : logs.list()) {
					tokens.add(f.substring(0, f.length() - 4));
				}
			}
		}.runTaskLaterAsynchronously(plugin, 20);
	}

	@Override
	public String getPermission() {
		return "nope.command." + getName();
	}

	@Override
	public String getDescription() {
		return "Lookup a ban log";
	}
}
