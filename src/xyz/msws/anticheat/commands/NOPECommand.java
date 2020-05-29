package xyz.msws.anticheat.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.commands.sub.BanwaveSubcommand;
import xyz.msws.anticheat.commands.sub.ClearSubcommand;
import xyz.msws.anticheat.commands.sub.EnablechecksSubcommand;
import xyz.msws.anticheat.commands.sub.OnlineSubcommand;
import xyz.msws.anticheat.commands.sub.ReloadSubcommand;
import xyz.msws.anticheat.commands.sub.RemovebanwaveSubcommand;
import xyz.msws.anticheat.commands.sub.ResetSubcommand;
import xyz.msws.anticheat.commands.sub.StatsSubcommand;
import xyz.msws.anticheat.commands.sub.TestAnimationSubcommand;
import xyz.msws.anticheat.commands.sub.TestlagSubcommand;
import xyz.msws.anticheat.commands.sub.TimeSubcommand;
import xyz.msws.anticheat.commands.sub.VLSubcommand;
import xyz.msws.anticheat.commands.sub.WarnSubcommand;
import xyz.msws.anticheat.utils.MSG;

public class NOPECommand implements CommandExecutor, TabCompleter {
	@SuppressWarnings("unused")
	private NOPE plugin;

	private Map<String, Subcommand> subs;

	public NOPECommand(NOPE plugin) {
		this.plugin = plugin;

		subs = new HashMap<>();
		subs.put("testlag", new TestlagSubcommand(plugin));
		subs.put("clear", new ClearSubcommand(plugin));
		subs.put("vl", new VLSubcommand(plugin));
		subs.put("reload", new ReloadSubcommand(plugin));
		subs.put("reset", new ResetSubcommand(plugin));
		subs.put("time", new TimeSubcommand(plugin));
		subs.put("banwave", new BanwaveSubcommand(plugin));
		subs.put("removebanwave", new RemovebanwaveSubcommand(plugin));
		subs.put("stats", new StatsSubcommand(plugin));
		subs.put("enablechecks", new EnablechecksSubcommand(plugin));
		subs.put("online", new OnlineSubcommand(plugin));
		subs.put("testanimation", new TestAnimationSubcommand(plugin));
		subs.put("warn", new WarnSubcommand(plugin));
		plugin.getCommand("nope").setExecutor(this);
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0) {
			MSG.sendHelp(sender, 0, "default");
			return true;
		}

		if (subs.containsKey(args[0].toLowerCase())) {
			Subcommand cmd = subs.get(args[0].toLowerCase());
			CommandResult result = cmd.execute(sender, args);
			if (result == CommandResult.SUCCESS)
				return true;
			MSG.tell(sender, "&4NOPE > &cProper usage for " + cmd.getName());
			MSG.tell(sender, "&7/nope " + cmd.getName() + " " + cmd.getUsage());
			MSG.tell(sender, result.getMessage());
			return true;
		}

		MSG.sendHelp(sender, 0, "default");
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> result = new ArrayList<>();

		for (Subcommand sub : subs.values()) {
			if (args.length > 1) {
				if (sub.tabCompletions() == null || sub.tabCompletions().isEmpty())
					continue;
				if (sub.tabCompletions().size() <= args.length - 1)
					continue;
				if (!sub.getName().equalsIgnoreCase(args[0]))
					continue;
				String[] res = sub.tabCompletions().get(args.length - 1);
				for (String r : res) {
					if (r.startsWith(args[args.length - 1].toLowerCase())) {
						result.add(r);
					}
				}
			} else {
				if (sub.getName().toLowerCase().startsWith(args[args.length - 1]))
					result.add(sub.getName());
			}
		}
//
//		if (args.length <= 1) {
//			for (String res : new String[] { "clear", "vl", "toggle", "reset", "flag", "checks", "banwave",
//					"removebanwave", "time", "stats", "enablechecks", "online", "testanimation", "testlag" }) {
//				if (res.toLowerCase().startsWith(args[0].toLowerCase()) && sender.hasPermission("nope.command." + res))
//					result.add(res);
//			}
//		}
//
//		if (args.length >= 2 && args.length <= 3) {
//			if (args[0].matches("(?i)(clear|removebanwave|banwave|flag)")) {
//				for (Player target : Bukkit.getOnlinePlayers()) {
//					if (target.getName().toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
//						result.add(target.getName());
//				}
//			}
//			if (args[0].equalsIgnoreCase("clear")) {
//				if ("all".startsWith(args[args.length - 1].toLowerCase()))
//					result.add("all");
//				for (Check c : plugin.getChecks().getAllChecks()) {
//					if (c.getCategory().toLowerCase().startsWith(args[args.length - 1])
//							&& !result.contains(c.getCategory()))
//						result.add(c.getCategory());
//				}
//			}
//		}
//
//		if (args.length == 2) {
//			if (args[0].equalsIgnoreCase("toggle")) {
//				for (String res : new String[] { "dev", "debug", "logs", "global", "globalscoreboard", "scoreboard" }) {
//					if (sender.hasPermission("nope.command.toggle." + res)
//							&& res.toLowerCase().startsWith(args[1].toLowerCase()))
//						result.add(res);
//				}
//			}
//			if (args[0].equalsIgnoreCase("testanimation")) {
//				for (String res : new String[] { "gwen", "nope" }) {
//					if (sender.hasPermission("nope.command.toggle." + res)
//							&& res.toLowerCase().startsWith(args[1].toLowerCase()))
//						result.add(res);
//				}
//			}
//		}
		return result;
	}
}
