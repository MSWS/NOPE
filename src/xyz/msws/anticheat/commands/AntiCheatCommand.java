package xyz.msws.anticheat.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.naming.OperationNotSupportedException;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.PluginInfo;
import xyz.msws.anticheat.PluginInfo.Stats;
import xyz.msws.anticheat.events.DevModeToggleEvent;
import xyz.msws.anticheat.events.player.PlayerToggleScoreboardEvent;
import xyz.msws.anticheat.modules.animations.AbstractAnimation;
import xyz.msws.anticheat.modules.animations.AnimationManager;
import xyz.msws.anticheat.modules.animations.GWENAnimation;
import xyz.msws.anticheat.modules.animations.NOPEAnimation;
import xyz.msws.anticheat.modules.checks.Check;
import xyz.msws.anticheat.modules.checks.CheckType;
import xyz.msws.anticheat.modules.data.CPlayer;
import xyz.msws.anticheat.modules.data.PlayerManager;
import xyz.msws.anticheat.utils.MSG;

public class AntiCheatCommand implements CommandExecutor, TabCompleter {
	private NOPE plugin;

	public AntiCheatCommand(NOPE plugin) {
		this.plugin = plugin;
		plugin.getCommand("nope").setExecutor(this);
	}

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0) {
			MSG.sendHelp(sender, 0, "default");
			return true;
		}
		OfflinePlayer off;
		CPlayer cp;

		switch (args[0].toLowerCase()) {
			case "clear":
				if (!sender.hasPermission("nope.command.clear")) {
					MSG.noPerm(sender, "nope.command.clear");
					return true;
				}
				if (args.length < 3) {
					MSG.sendHelp(sender, 0, "default");
					return true;
				}
				String target = "", hack = "";

				if (args[1].equalsIgnoreCase("all")) {
					target = "everyone's";

					for (OfflinePlayer p : plugin.getPlayerManager().getLoadedPlayers()) {
						cp = plugin.getCPlayer(p);
						if (args[2].equalsIgnoreCase("all")) {
							cp.clearVls();
							MSG.sendPluginMessage(null, "clearvl:" + p.getName());
							hack = "all hacks";
						} else {
							boolean found = false;
							for (Check h : plugin.getChecks().getAllChecks()) {
								if (args[2].equalsIgnoreCase(h.getCategory())) {
									cp.setSaveData("vls." + h.getCategory(), 0);
									MSG.sendPluginMessage(null, "setvl:" + p.getName() + " " + h + " 0");
									hack = h.getCategory();
									found = true;
									break;
								}
							}
							if (!found) {
								MSG.tell(sender, "&7Unable to find specified hack: &e" + args[2] + "&7.");
								return true;
							}
						}
					}
				} else {
					cp = plugin.getCPlayer(Bukkit.getOfflinePlayer(args[1]));
					target = cp.getPlayer().getName() + "'"
							+ (cp.getPlayer().getName().toLowerCase().endsWith("s") ? "" : "s");

					if (args[2].equalsIgnoreCase("all")) {
						cp.clearVls();
						MSG.sendPluginMessage(null, "clearvl:" + cp.getPlayer().getName());
						hack = "all hacks";
					} else {
						boolean found = false;
						for (Check h : plugin.getChecks().getAllChecks()) {
							if (args[2].equalsIgnoreCase(h.getCategory())) {
								cp.setSaveData("vls." + h.getCategory(), 0);
								MSG.sendPluginMessage(null, "setvl:" + cp.getPlayer().getName() + " " + h + " 0");
								hack = h.getCategory();
								found = true;
								break;
							}
						}
						if (!found) {
							MSG.tell(sender, "&7Unable to find specified hack: &e" + args[2] + "&7.");
							return true;
						}
					}
				}

				MSG.tell(sender, "&7You cleared &e" + target + "&7 VLs for &c" + hack);
				break;
			case "vl":
				if (!sender.hasPermission("nope.command.vl")) {
					MSG.noPerm(sender, "nope.command.vl");
					return true;
				}
				if (args.length == 1) {
					boolean shown = false;
					for (OfflinePlayer p : plugin.getPlayerManager().getLoadedPlayers()) {
						cp = plugin.getCPlayer(p);
						String vls = formatVls(p);
						if (vls.isEmpty())
							continue;
						shown = true;
						MSG.tell(sender, "&5[&d" + cp.getTotalVL() + "&5] &e" + p.getName() + "&7: " + formatVls(p));
					}
					if (!shown)
						MSG.tell(sender, "&5[&dVLS&5] &cThere are no VLs.");
					return true;
				}

				OfflinePlayer t = Bukkit.getOfflinePlayer(args[1]);

				cp = plugin.getCPlayer(t);

				if (formatVls(t).isEmpty()) {
					MSG.tell(sender, "&5[&d" + cp.getTotalVL() + "&5] &e" + t.getName() + " &chas no VLs.");
					return true;
				}

				MSG.tell(sender, "&5[&d" + cp.getTotalVL() + "&5] &e" + t.getName() + "&7: " + formatVls(t));
				break;
			case "reload":
				if (!sender.hasPermission("nope.command.reload")) {
					MSG.noPerm(sender, "nope.command.reload");
					return true;
				}
				plugin.setConfig(YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "config.yml")));
				plugin.setLang(YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "lang.yml")));
				plugin.getActionManager().loadActions();
				MSG.tell(sender, MSG.getString("Reloaded", "Successfully reloaded."));
				break;
			case "toggle":
				if (!sender.hasPermission("nope.command.toggle")) {
					MSG.noPerm(sender, "nope.command.toggle");
					return true;
				}
				if (args.length < 2) {
					MSG.sendHelp(sender, 0, "default");
					return true;
				}
				switch (args[1].toLowerCase()) {
					case "dev":
						if (!sender.hasPermission("nope.command.toggle.dev")) {
							MSG.noPerm(sender, "nope.command.toggle.dev");
							return true;
						}
						plugin.getConfig().set("DevMode", !plugin.devMode());
						MSG.tell(sender,
								MSG.getString("Toggle", "you %status% %name%")
										.replace("%status%", enabledDisable(plugin.devMode()))
										.replace("%name%", "Developer Mode"));
						plugin.saveConfig();
						Bukkit.getPluginManager().callEvent(new DevModeToggleEvent(plugin.devMode()));
						break;
					case "debug":
						if (!sender.hasPermission("nope.command.toggle.debug")) {
							MSG.noPerm(sender, "nope.command.toggle.debug");
							return true;
						}
						plugin.getConfig().set("DebugMode", !plugin.debugMode());
						MSG.tell(sender,
								MSG.getString("Toggle", "you %status% %name%")
										.replace("%status%", enabledDisable(plugin.debugMode()))
										.replace("%name%", "Debug Mode"));
						plugin.saveConfig();
						break;
					case "lagback":
					case "setback":
					case "cancel":
						if (!sender.hasPermission("nope.command.toggle.cancel")) {
							MSG.noPerm(sender, "nope.command.toggle.cancel");
							return true;
						}
						plugin.getConfig().set("SetBack", !plugin.getConfig().getBoolean("SetBack"));
						MSG.tell(sender,
								MSG.getString("Toggle", "you %status% %name%")
										.replace("%status%", enabledDisable(plugin.getConfig().getBoolean("SetBack")))
										.replace("%name%", "Setbacks"));
						plugin.saveConfig();
						break;
					case "log":
					case "logs":
						if (!sender.hasPermission("nope.command.toggle.logs")) {
							MSG.noPerm(sender, "nope.command.toggle.logs");
							return true;
						}
						String nextValue = "";
						String current = plugin.getConfig().getString("Log");
						if (current.equalsIgnoreCase("none")) {
							nextValue = "file";
						} else if (current.equalsIgnoreCase("file")) {
							nextValue = "hastebin";
						} else {
							nextValue = "NONE";
						}

						plugin.getConfig().set("Log", nextValue);
//						MSG.tell(sender,
//								MSG.getString("Toggle", "you %status% %name%")
//										.replace("%status%", enabledDisable(plugin.getConfig().getBoolean("Log")))
//										.replace("%name%", "Logs"));
						MSG.tell(sender,
								MSG.getString("LogToggle", "You set logs to %status%").replace("%status%", nextValue));
						plugin.saveConfig();
						break;
					case "global":
						if (!sender.hasPermission("nope.command.toggle.global")) {
							MSG.noPerm(sender, "nope.command.toggle.global");
							return true;
						}
						plugin.getConfig().set("Global", !plugin.getConfig().getBoolean("Global"));
						MSG.tell(sender,
								MSG.getString("Toggle", "you %status% %name%")
										.replace("%status%", enabledDisable(plugin.getConfig().getBoolean("Global")))
										.replace("%name%", "Global"));
						plugin.saveConfig();
						break;
					case "globalscoreboard":
						if (!sender.hasPermission("nope.command.toggle.globalscoreboard")) {
							MSG.noPerm(sender, "nope.command.toggle.globalscoreboard");
							return true;
						}
						plugin.getConfig().set("Scoreboard", !plugin.getConfig().getBoolean("Scoreboard"));
						MSG.tell(sender,
								MSG.getString("Toggle", "you %status% %name%")
										.replace("%status%",
												enabledDisable(plugin.getConfig().getBoolean("Scoreboard")))
										.replace("%name%", "Global Scoreboard"));
						plugin.saveConfig();
						break;
					case "scoreboard":
						if (!sender.hasPermission("nope.command.toggle.scoreboard")) {
							MSG.noPerm(sender, "nope.command.toggle.scoreboard");
							return true;
						}
						if (!(sender instanceof Player)) {
							MSG.tell(sender, "You must be a player to have a scoreboard.");
							return true;
						}
						cp = plugin.getCPlayer(((Player) sender));
						cp.setSaveData("scoreboard",
								cp.hasSaveData("scoreboard") ? !cp.getSaveData("scoreboard", Boolean.class) : true);
						MSG.tell(sender,
								MSG.getString("Toggle", "you %status% %name%")
										.replace("%status%",
												enabledDisable(cp.getSaveData("scoreboard", Boolean.class)))
										.replace("%name%", "your Scoreboard"));
//						((Player) sender).setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
						Bukkit.getPluginManager().callEvent(new PlayerToggleScoreboardEvent((Player) sender,
								cp.getSaveData("scoreboard", Boolean.class)));
						break;
				}
				break;
			case "reset":
				if (!sender.hasPermission("nope.command.reset")) {
					MSG.noPerm(sender, "nope.command.reset");
					return true;
				}
				plugin.saveResource("config.yml", true);
				plugin.saveResource("lang.yml", true);
				plugin.setConfig(YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "config.yml")));
				plugin.setLang(YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "lang.yml")));
				MSG.tell(sender, "Succesfully reset.");
				break;
			case "time":
				if (!sender.hasPermission("nope.command.time")) {
					MSG.noPerm(sender, "nope.command.time");
					return true;
				}
				MSG.tell(sender,
						"&4&l[&c&lNOPE&4&l] &7Next banwave: &e" + MSG.getTime(plugin.getBanwave().timeToNextBanwave()));
				break;
			case "banwave":
				if (!sender.hasPermission("nope.command.banwave")) {
					MSG.noPerm(sender, "nope.command.banwave");
					return true;
				}
				if (args.length > 1) {
					if (!sender.hasPermission("nope.command.banwave.addplayer")) {
						MSG.noPerm(sender, "nope.command.banwave.addplayer");
						return true;
					}
					off = Bukkit.getOfflinePlayer(args[1]);
					cp = plugin.getCPlayer(off);
					cp.setSaveData("isBanwaved", "Manual [" + sender.getName() + "]");
					MSG.sendPluginMessage(null, "banwave:" + off.getName() + " Manual");
					MSG.tell(sender, "Added " + off.getName() + " to the banwave.");
					return true;
				}
				MSG.sendPluginMessage(null, "banwave");
				plugin.getBanwave().runBanwave(true).run();
				MSG.tell(sender, "&cSuccessfully initiated banwave.");
				break;
			case "removebanwave":
				if (!sender.hasPermission("nope.command.removebanwave")) {
					MSG.noPerm(sender, "nope.command.removebanwave");
					return true;
				}
				if (args.length < 2) {
					MSG.tell(sender, "You must specify a player");
					return true;
				}
				off = Bukkit.getOfflinePlayer(args[1]);

				cp = plugin.getCPlayer(off);
				if (!cp.hasSaveData("isBanwaved")) {
					MSG.tell(sender, off.getName() + " is not banwaved.");
					return true;
				}
				MSG.sendPluginMessage(null, "removebanwave:" + off.getName());
				cp.removeSaveData("isBanwaved");
				MSG.tell(sender, "Removed " + off.getName() + " from the banwave.");
				break;
			case "warn":
			case "flag":
				if (!sender.hasPermission("nope.command.warn")) {
					MSG.noPerm(sender, "nope.command.warn");
					return true;
				}
				if (args.length < 4) {
					MSG.sendHelp(sender, 0, "default");
					return true;
				}
				t = Bukkit.getOfflinePlayer(args[1]);
				cp = plugin.getCPlayer(t);

				String hackName = "", stringVl = "", current = "";
				for (int i = 2; i < args.length; i++) {
					String arg = args[i];
					if (arg.startsWith("h:")) {
						current = "hack";
						hackName += arg.substring(2);
						continue;
					} else if (arg.startsWith("v:")) {
						current = "vl";
						stringVl += arg.substring(2);
						continue;
					}

					if (current.equals("hack")) {
						hackName += " " + arg;
					} else if (current.equals("vl")) {
						stringVl += " " + arg;
					}
				}

				final String hackNameFinal = hackName;

				cp.flagHack(new Check() {
					@Override
					public boolean lagBack() {
						return true;
					}

					@Override
					public CheckType getType() {
						return CheckType.MISC;
					}

					@Override
					public String getDebugName() {
						return "ManuallyIssued";
					}

					@Override
					public String getCategory() {
						return hackNameFinal;
					}

					@Override
					public void register(NOPE plugin) {
					}

				}, Integer.parseInt(stringVl));

				MSG.tell(sender, "Warned " + t.getName() + " for " + hackName + " (vl: " + stringVl + ")");
				break;
			case "checks":
				if (!sender.hasPermission("nope.command.checks")) {
					MSG.noPerm(sender, "nope.command.checks");
					return true;
				}
				for (CheckType type : plugin.getChecks().getCheckTypes()) {
					HashMap<String, Integer> checks = new HashMap<>();
					for (Check check : plugin.getChecks().getChecksWithType(type))
						checks.put(check.getCategory(),
								checks.containsKey(check.getCategory()) ? checks.get(check.getCategory()) + 1 : 1);

					if (checks.isEmpty())
						continue;
					MSG.tell(sender, " ");

					StringBuilder builder = new StringBuilder();

					String[] colors = { "&b", "&a" };

					for (int i = 0; i < checks.keySet().size(); i++) {
						builder.append(colors[i % colors.length] + checks.keySet().toArray()[i] + " "
								+ checks.values().toArray()[i] + " ");
					}

					MSG.tell(sender, "&6&l" + MSG.camelCase(type.toString()) + " &7(&e&l"
							+ plugin.getChecks().getChecksWithType(type).size() + "&7) " + type.getDescription());
					MSG.tell(sender, builder.toString());
				}

				MSG.tell(sender, "&c&lTotal Checks: &4" + plugin.getChecks().getAllChecks().size());
				break;
			case "stats":
				if (!(sender instanceof Player)) {
					MSG.tell(sender, "You must be a player.");
					return true;
				}
				if (!sender.hasPermission("nope.command.stats")) {
					MSG.noPerm(sender, "nope.command.stats");
					return true;
				}

				Player player = (Player) sender;
				cp = plugin.getCPlayer(player);
				player.openInventory(plugin.getStats().getInventory());
				player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1, 1);
//				cp.setTempData(Stat.OPEN_INVENTORY, "stats");
				cp.setInventory("stats");
				break;
			case "enablechecks":
				if (!sender.hasPermission("nope.command.enablechecks")) {
					MSG.noPerm(sender, "nope.command.enablechecks");
					return true;
				}
				plugin.getConfig().set("Checks", null);
				for (Check check : plugin.getChecks().getAllChecks()) {
					plugin.getConfig().set("Checks." + MSG.camelCase(check.getType() + "") + ".Enabled", true);
					plugin.getConfig().set(
							"Checks." + MSG.camelCase(check.getType() + "") + "." + check.getCategory() + ".Enabled",
							true);
					plugin.getConfig().set("Checks." + MSG.camelCase(check.getType() + "") + "." + check.getCategory()
							+ "." + check.getDebugName() + ".Enabled", true);
				}
				plugin.saveConfig();
				MSG.tell(sender, MSG.getString("AllChecksEnabled", "&aSuccessfully enabled all checks."));
				break;
			case "online":
				if (!sender.hasPermission("nope.command.online")) {
					MSG.noPerm(sender, "nope.command.online");
					return true;
				}
				if (plugin.getPluginInfo() == null) {
					MSG.tell(sender, "&4[NOPE] &7Unable to grab plugin info at this time.");
					return true;
				}
				PluginInfo info = plugin.getPluginInfo();
				Stats stats = info.getStats();
				MSG.tell(sender, String.format(
						"&4[NOPE] &7NOPE has reached &b%d&7 downloads, &a%d&7 reviews, and is averaging about &e%.2f&7.",
						stats.getDownloads(), stats.getReviews(), stats.getRating()));
				break;
			case "testanimation":
				AnimationManager anim = plugin.getModule(AnimationManager.class);

				if (args.length < 2) {
					MSG.tell(sender, "Please specify an animation.");
					return true;
				}

				AbstractAnimation a = null;

				Check check = new Check() {

					@Override
					public void register(NOPE plugin) throws OperationNotSupportedException {
					}

					@Override
					public boolean lagBack() {
						return false;
					}

					@Override
					public CheckType getType() {
						return CheckType.MISC;
					}

					@Override
					public String getDebugName() {
						return "AnimationTest#1";
					}

					@Override
					public String getCategory() {
						return "AnimationTest";
					}
				};

				switch (args[1].toLowerCase()) {
					case "gwen":
						a = new GWENAnimation(plugin, (Player) sender, check);
						break;
					case "nope":
						a = new NOPEAnimation(plugin, (Player) sender, check);
						break;
					default:
						MSG.tell(sender, "Unknown animation.");
						return true;
				}

				anim.startAnimation((Player) sender, a);
				break;
			default:
				MSG.sendHelp(sender, 0, "default");
				return true;
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> result = new ArrayList<>();
		if (args.length <= 1) {
			for (String res : new String[] { "clear", "vl", "toggle", "reset", "flag", "checks", "banwave",
					"removebanwave", "time", "stats", "enablechecks", "online", "testanimation" }) {
				if (res.toLowerCase().startsWith(args[0].toLowerCase()) && sender.hasPermission("nope.command." + res))
					result.add(res);
			}
		}

		if (args.length >= 2 && args.length <= 3) {
			if (args[0].matches("(?i)(clear|removebanwave|banwave|flag)")) {
				for (Player target : Bukkit.getOnlinePlayers()) {
					if (target.getName().toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
						result.add(target.getName());
				}
			}
			if (args[0].equalsIgnoreCase("clear")) {
				if ("all".startsWith(args[args.length - 1].toLowerCase()))
					result.add("all");
				for (Check c : plugin.getChecks().getAllChecks()) {
					if (c.getCategory().toLowerCase().startsWith(args[args.length - 1])
							&& !result.contains(c.getCategory()))
						result.add(c.getCategory());
				}
			}
		}

		if (args.length == 2) {
			if (args[0].equalsIgnoreCase("toggle")) {
				for (String res : new String[] { "cancel", "dev", "debug", "logs", "global", "globalscoreboard",
						"scoreboard" }) {
					if (sender.hasPermission("nope.command.toggle." + res)
							&& res.toLowerCase().startsWith(args[1].toLowerCase()))
						result.add(res);
				}
			}
			if (args[0].equalsIgnoreCase("testanimation")) {
				for (String res : new String[] { "gwen", "nope" }) {
					if (sender.hasPermission("nope.command.toggle." + res)
							&& res.toLowerCase().startsWith(args[1].toLowerCase()))
						result.add(res);
				}
			}
		}
		return result;
	}

	private String formatVls(OfflinePlayer player) {
		CPlayer cp = plugin.getModule(PlayerManager.class).getPlayer(player);
		HashMap<String, Integer> vls = new HashMap<>();
		ConfigurationSection vlSection = cp.getDataFile().getConfigurationSection("vls");
		if (vlSection == null)
			return "";

		for (String hack : vlSection.getKeys(false)) {
			if (vlSection.getInt(hack) > 0)
				vls.put(MSG.camelCase(hack), vlSection.getInt(hack));
		}

		String result = "";
		for (String hack : vls.keySet()) {
			result += hack + " " + vls.get(hack) + "&7, ";
		}
		result = result.substring(0, Math.max(result.length() - 2, 0));
		return result;
	}

	private String enabledDisable(boolean toggle) {
		return toggle ? "&aenabled" : "&cdisabled";
	}
}
