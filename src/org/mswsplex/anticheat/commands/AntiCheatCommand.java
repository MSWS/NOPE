package org.mswsplex.anticheat.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.mswsplex.anticheat.checks.Check;
import org.mswsplex.anticheat.data.CPlayer;
import org.mswsplex.anticheat.msws.AntiCheat;
import org.mswsplex.anticheat.utils.MSG;

public class AntiCheatCommand implements CommandExecutor, TabCompleter {
	private AntiCheat plugin;

	public AntiCheatCommand(AntiCheat plugin) {
		this.plugin = plugin;
		plugin.getCommand("anticheat").setExecutor(this);
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0) {
			MSG.sendHelp(sender, 0, "default");
			return true;
		}
		switch (args[0].toLowerCase()) {
		case "clear":
			if (args.length < 3) {
				MSG.tell(sender, "/anticheat clear [player/all] [hack/all]");
				return true;
			}

			String target = "", hack = "";

			if (args[1].equalsIgnoreCase("all")) {
				target = "everyone's";

				for (Player p : Bukkit.getOnlinePlayers()) {
					CPlayer cp = plugin.getCPlayer(p);
					if (args[2].equalsIgnoreCase("all")) {
						for (Check c : plugin.getChecks().getActiveChecks())
							cp.setSaveData("vls." + c.getCategory().toLowerCase(), 0);
						hack = "all hacks";
					} else {
						cp.setSaveData("vls." + args[2].toLowerCase(), 0);
						hack = MSG.camelCase(args[2]);
					}
				}
			} else {
				if (Bukkit.getPlayer(args[1]) == null) {
					MSG.tell(sender, "&cUnknown Player.");
					return true;
				}
				CPlayer cp = plugin.getCPlayer(Bukkit.getPlayer(args[1]));

				target = Bukkit.getPlayer(args[1]).getName() + "'"
						+ (Bukkit.getPlayer(args[1]).getName().toLowerCase().endsWith("s") ? "" : "s");

				if (args[2].equalsIgnoreCase("all")) {
					for (Check c : plugin.getChecks().getActiveChecks())
						cp.setSaveData("vls." + c.getCategory().toLowerCase(), 0);
					hack = "all hacks";
				} else {
					cp.setSaveData("vls." + args[2].toLowerCase(), 0);
					hack = MSG.camelCase(args[2]);
				}
			}

			MSG.tell(sender, "&7You cleared &e" + target + "&7 VLs for &c" + hack);
			break;
		case "vl":
			if (args.length == 1) {
				boolean shown = false;
				for (Player p : Bukkit.getOnlinePlayers()) {
					CPlayer cp = plugin.getCPlayer(p);
					String vls = formatVls(p);
					if (vls.isEmpty())
						continue;
					shown = true;
					MSG.tell(sender, "&5[&d" + cp.getTotalVL() + "&5] &e" + p.getName() + "&7: " + formatVls(p));
				}
				if (!shown) {
					MSG.tell(sender, "&5[&dVLS&5] &cThere are no VLs.");
				}
				return true;
			}

			Player t = Bukkit.getPlayer(args[1]);

			if (t == null) {
				MSG.tell(sender, "&cUnknown Player");
				return true;
			}

			CPlayer cp = plugin.getCPlayer(t);

			if (formatVls(t).isEmpty()) {
				MSG.tell(sender, "&5[&d" + cp.getTotalVL() + "&5] &e" + t.getName() + " &chas no VLs.");
				return true;
			}

			MSG.tell(sender, "&5[&d" + cp.getTotalVL() + "&5] &e" + t.getName() + "&7: " + formatVls(t));
			break;
		case "reload":
			plugin.configYml = new File(plugin.getDataFolder(), "config.yml");
			plugin.config = YamlConfiguration.loadConfiguration(plugin.configYml);
			plugin.langYml = new File(plugin.getDataFolder(), "lang.yml");
			plugin.lang = YamlConfiguration.loadConfiguration(plugin.langYml);
			plugin.guiYml = new File(plugin.getDataFolder(), "guis.yml");
			plugin.gui = YamlConfiguration.loadConfiguration(plugin.guiYml);
			MSG.tell(sender, MSG.getString("Reloaded", "Successfully reloaded."));
			break;
		case "dev":
			plugin.config.set("DevMode", !plugin.devMode());
			MSG.tell(sender, "dev: " + MSG.TorF(plugin.devMode()));
			plugin.saveConfig();
			break;
		case "lagback":
			plugin.config.set("LagBack", !plugin.config.getBoolean("LagBack"));
			MSG.tell(sender, "lagback: " + MSG.TorF(plugin.config.getBoolean("LagBack")));
			plugin.saveConfig();
			break;
		case "reset":
			plugin.saveResource("config.yml", true);
			plugin.saveResource("lang.yml", true);
			plugin.saveResource("guis.yml", true);
			plugin.configYml = new File(plugin.getDataFolder(), "config.yml");
			plugin.langYml = new File(plugin.getDataFolder(), "lang.yml");
			plugin.config = YamlConfiguration.loadConfiguration(plugin.configYml);
			plugin.lang = YamlConfiguration.loadConfiguration(plugin.langYml);
			plugin.guiYml = new File(plugin.getDataFolder(), "guis.yml");
			plugin.gui = YamlConfiguration.loadConfiguration(plugin.guiYml);
			MSG.tell(sender, "Succesfully reset.");
			break;
		default:
			return false;
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> result = new ArrayList<>();
		if (args.length <= 1) {
			for (String res : new String[] { "clear", "vl", "lagback", "reset" }) {
				if (res.toLowerCase().startsWith(args[0].toLowerCase()))
					result.add(res);
			}
		}

		if (args.length >= 2 && args.length <= 3) {
			if (args[0].equalsIgnoreCase("clear")) {
				if ("all".startsWith(args[args.length - 1].toLowerCase())) {
					result.add("all");
				}
				for (Check c : plugin.getChecks().getActiveChecks()) {
					if (c.getCategory().toLowerCase().startsWith(args[args.length - 1])
							&& !result.contains(c.getCategory()))
						result.add(c.getCategory());
				}

				for (Player target : Bukkit.getOnlinePlayers()) {
					if (target.getName().toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
						result.add(target.getName());
				}
			}
		}

		return result;
	}

	private String formatVls(OfflinePlayer player) {
		CPlayer cp = plugin.getPlayerManager().getPlayer(player);
		HashMap<String, Integer> vls = new HashMap<>();
		ConfigurationSection vlSection = cp.getDataFile().getConfigurationSection("vls");
		if (vlSection == null)
			return "";

		for (String hack : vlSection.getKeys(false)) {
			if (vlSection.getInt(hack) > 0) {
				vls.put(MSG.camelCase(hack), vlSection.getInt(hack));
			}
		}

		String result = "";
		for (String hack : vls.keySet()) {
			result += MSG.getVlColor(vls.get(hack)) + hack + " " + vls.get(hack) + "&7, ";
		}
		result = result.substring(0, Math.max(result.length() - 2, 0));
		return result;
	}
}
