Paket xyz.msws.nope.commands;

importiere java.util.ArrayList;
importiere java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;

importieren xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.AbstractModule;
importieren xyz.msws.nope.utils.MSG;

public abstract class AbstractCommand extends AbstractModule implements CommandExecutor, TabCompleter {

	geschützte Liste<Subcommand> cmds = new ArrayList<>();

	öffentliches AbstractCommand(NOPE-Plugin) {
		super(plugin);
	}

	öffentliche abstrakte Zeichenkette getName();

	@Überschreiben
	public void enable() {
		PluginCommand cmd = plugin.getCommand());
		cmd.setExecutor(dies);
		cmd.setTabCompleter(this);
	}

	public void disable() {
		PluginCommand cmd = plugin.getCommand());
		cmd.setExecutor(null);
		cmd.setTabCompleter(null);
	}

	öffentliche Liste<Subcommand> getSubCommands() {
		return cmds;
	}

	@Überschreiben
	public boolean onCommand(CommandSender sender, Befehlsbefehl, String[] args) {
		if (args.length < 1)
			return false;
		String cmd = args[0];
		für (Unterbefehl c : cmds) {
			if (c.getName().equalsIgnoreCase(cmd) || c.getAliases().contains(cmd.toLowerCase())) { {
				if (c.getPermission() != null && !sender.hasPermission(c.getPermission())) {
					MSG.tell(Absender,
							MSG.getString("Command.NoPermission",
									"&4&l[&c&lNOPE&4&l] &cIhnen fehlt die &a%perm% &cpermission.")
									.replace("%perm%", c.getPermission()));
					kehre wahr zurück;
				}
				CommandResult result = c.execute(sender, args);
				if (Ergebnis == CommandResult.SUCCESS)
					kehre wahr zurück;
				if (result == CommandResult.NO_PERMISSION) {
					MSG.tell(Absender,
							MSG.getString("Command.NoPermission",
									"&4&l[&c&lNOPE&4&l] &cIhnen fehlt die &a%perm% &cpermission.")
									.replace("%perm%", c.getPermission()));
					kehre wahr zurück;
				}
				MSG.tell(Absender, "&4" + Label + " > &cProper Nutzung für " + c.getName());
				MSG.tell(Absender, "&7/" + label + " " " + c.getName() + " " + c.getUsage());
				MSG.tell(Absender, result.getMessage());
				kehre wahr zurück;
			}
		}
		return false;
	}

	@Überschreiben
	öffentliche Liste<String> onTabComplete(CommandSender sender, Befehlsbefehl, String[] args) {
		Liste<String> Ergebnis = neue ArrayList<>();

		für (Unterbefehl unter: cmds) {
			Liste<String> Aliase = sub.getAliases();
			aliases.add(sub.getName());
			Liste<String[]> Vervollständigungen = sub.tabCompletions(Absender);
			if (args.length > 1) {
				if (completions == null || completions.isEmpty())
					fortsetzen;
				if (completions.size() < args.length - 1)
					fortsetzen;
				if (!aliases.contains(args[0].toLowerCase()))
					fortsetzen;
				String[] res = completions.get(args.length - 2);
				wenn (res == null)
					fortsetzen;
				für (String r : res)
					if (r.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
						result.add(r);
				fortsetzen;
			}
			für (String Alias : Aliases) {
				if (alias.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
					result.add(alias);
			}
		}

		return result.isEmpty() ? null : Ergebnis;
	}

}
