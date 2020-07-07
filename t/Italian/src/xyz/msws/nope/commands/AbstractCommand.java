pacchetto xyz.msws.nope.commands;

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
		cmd.setExecutor(questo);
		cmd.setTabCompleter(questo);
	}

	public void disable() {
		PluginCommand cmd = plugin.getCommand(getName());
		cmd.setExecutor(null);
		cmd.setTabCompleter(null);
	}

	public List<Subcommand> getSubCommands() {
		cmd di ritorno;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length < 1)
			restituire falso;
		Stringa cmd = args[0];
		for (Subcommand c : cmds) {
			if (c.getName().equalsIgnoreCase(cmd) <unk> <unk> c.getAliases().contains(cmd.toLowerCase())) {
				if (c.getPermission() != null && !sender.hasPermission(c.getPermission())) {
					MSG.tell(sender,
							MSG.getString("Command.NoPermission",
									"&4&l[&c&lNOPE&4&l] &cTi manca &a%perm% &cpermission.")
									.replace("%perm%", c.getPermission()));
					return true;
				}
				Risultato del comandoRisultato = c.execute(sender, args);
				if (result == CommandResult.SUCCESS)
					return true;
				if (result == CommandResult.NO_PERMISSION) {
					MSG.tell(sender,
							MSG.getString("Command.NoPermission",
									"&4&l[&c&lNOPE&4&l] &cTi manca &a%perm% &cpermission.")
									.replace("%perm%", c.getPermission()));
					return true;
				}
				MSG.tell(sender, "&4" + label + " > &cUso corretto per " + c.getName());
				MSG.tell(sender, "&7/" + label + " " + c.getName() + " + " + c.getUsage());
				MSG.tell(sender, result.getMessage());
				return true;
			}
		}
		restituire falso;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		Elenco<String> risultato = nuovo ArrayList<>();

		for (Subcommand sub : cmds) {
			Elenco<String> alias = sub.getAliases();
			aliases.add(sub.getName());
			Elenco<String[]> completions = sub.tabCompletions(sender);
			if (args.length > 1) {
				if (completions == null <unk> <unk> completions.isEmpty())
					continuare;
				if (completions.size() < args.length - 1)
					continuare;
				if (!aliases.contains(args[0].toLowerCase()))
					continuare;
				Stringa[] res = completions.get(args.length - 2);
				if (res == null)
					continuare;
				for (Stringa r : res)
					if (r.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
						result.add(r);
				continuare;
			}
			for (String alias : aliases) {
				if (alias.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
					result.add(alias);
			}
		}

		return result.isEmpty() ? null : risultato;
	}

}
