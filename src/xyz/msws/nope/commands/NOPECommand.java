package xyz.msws.nope.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.commands.sub.BanwaveSubcommand;
import xyz.msws.nope.commands.sub.ChecksSubcommand;
import xyz.msws.nope.commands.sub.ClearSubcommand;
import xyz.msws.nope.commands.sub.EnablechecksSubcommand;
import xyz.msws.nope.commands.sub.OnlineSubcommand;
import xyz.msws.nope.commands.sub.ReloadSubcommand;
import xyz.msws.nope.commands.sub.RemovebanwaveSubcommand;
import xyz.msws.nope.commands.sub.ResetSubcommand;
import xyz.msws.nope.commands.sub.StatsSubcommand;
import xyz.msws.nope.commands.sub.TestAnimationSubcommand;
import xyz.msws.nope.commands.sub.TestlagSubcommand;
import xyz.msws.nope.commands.sub.TimeSubcommand;
import xyz.msws.nope.commands.sub.ToggleSubcommand;
import xyz.msws.nope.commands.sub.VLSubcommand;
import xyz.msws.nope.commands.sub.WarnSubcommand;
import xyz.msws.nope.modules.AbstractModule;
import xyz.msws.nope.utils.MSG;

public class NOPECommand extends AbstractModule implements CommandExecutor, TabCompleter {

	private Map<String, Subcommand> subs;

	public NOPECommand(NOPE plugin) {
		super(plugin);
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
			if (result == CommandResult.NO_PERMISSION) {
				MSG.tell(sender,
						MSG.getString("NoPermission", "&4&l[&c&lNOPE&4&l] &cYou have insufficient permissions."));
				return true;
			}
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
			List<String[]> completions = sub.tabCompletions(sender);
			if (args.length > 1) {
				if (completions == null || completions.isEmpty())
					continue;
				if (completions.size() < args.length - 1)
					continue;
				if (!sub.getName().equalsIgnoreCase(args[0]))
					continue;
				String[] res = completions.get(args.length - 2);
				for (String r : res)
					if (r.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
						result.add(r);

			} else if (sub.getName().toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
				result.add(sub.getName());
		}

		return result.isEmpty() ? null : result;
	}

	@Override
	public void enable() {
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
		subs.put("toggle", new ToggleSubcommand(plugin));
		subs.put("checks", new ChecksSubcommand(plugin));

		plugin.getCommand("nope").setExecutor(this);
	}

	@Override
	public void disable() {
		// TODO Auto-generated method stub

	}
}
