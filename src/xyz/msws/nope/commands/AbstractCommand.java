package xyz.msws.nope.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.AbstractModule;
import xyz.msws.nope.utils.MSG;

public abstract class AbstractCommand extends AbstractModule implements CommandExecutor, TabCompleter {

	protected List<Subcommand> cmds = new ArrayList<>();

	public AbstractCommand(NOPE plugin) {
		super(plugin);
	}

	public abstract String getName();

	@Override
	public void enable() {
		PluginCommand cmd = plugin.getCommand(getName());
		cmd.setExecutor(this);
		cmd.setTabCompleter(this);
	}

	public void disable() {
		PluginCommand cmd = plugin.getCommand(getName());
		cmd.setExecutor(null);
		cmd.setTabCompleter(null);
	}

	public List<Subcommand> getSubCommands() {
		return cmds;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length < 1)
			return false;
		String cmd = args[0];
		for (Subcommand c : cmds) {
			if (c.getName().equalsIgnoreCase(cmd) || c.getAliases().contains(cmd.toLowerCase())) {
				if (c.getPermission() != null && !sender.hasPermission(c.getPermission())) {
					MSG.tell(sender,
							MSG.getString("Command.NoPermission",
									"&4&l[&c&lNOPE&4&l] &cYou lack the &a%perm% &cpermission.")
									.replace("%perm%", c.getPermission()));
					return true;
				}
				CommandResult result = c.execute(sender, args);
				if (result == CommandResult.SUCCESS)
					return true;
				if (result == CommandResult.NO_PERMISSION) {
					MSG.tell(sender,
							MSG.getString("Command.NoPermission",
									"&4&l[&c&lNOPE&4&l] &cYou lack the &a%perm% &cpermission.")
									.replace("%perm%", c.getPermission()));
					return true;
				}
				MSG.tell(sender, "&4" + label + " > &cProper usage for " + c.getName());
				MSG.tell(sender, "&7/" + label + " " + c.getName() + " " + c.getUsage());
				MSG.tell(sender, result.getMessage());
				return true;
			}
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> result = new ArrayList<>();

		for (Subcommand sub : cmds) {
			List<String> aliases = sub.getAliases();
			aliases.add(sub.getName());
			List<String[]> completions = sub.tabCompletions(sender, args);
			if (args.length > 1) {
				if (completions == null || completions.isEmpty())
					continue;
				if (completions.size() < args.length - 1)
					continue;
				if (!aliases.contains(args[0].toLowerCase()))
					continue;
				String[] res = completions.get(args.length - 2);
				if (res == null)
					continue;
				for (String r : res)
					if (r.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
						result.add(r);
				continue;
			}
			for (String alias : aliases) {
				if (alias.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
					result.add(alias);
			}
		}

		return result.isEmpty() ? null : result;
	}

}
