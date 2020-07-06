balíček xyz.msws.nope.commands;

importovat java.util.ArrayList;
importovat java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;

importovat xyz.msws.nope.NOPE;
importovat xyz.msws.nope.modules.AbstractModule;
importovat xyz.msws.nope.utils.MSG;

public abstract class AbstractCommand rozšiřuje AbstractModule imimplementuje CommandExecutor, TabCompleter {

	chráněný seznam<Subcommand> cmds = nový ArrayList<>();

	public AbstractCommand(NOPE plugin) {
		super(plugin);
	}

	veřejný abstract String getName();

	@override
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

	veřejný seznam<Subcommand> getSubCommands() {
		zpáteční cm;
	}

	@override
	veřejné boolean onCommand(CommandSender sender, velitelský příkaz, String label, String[] args) {
		if (args.length < 1)
			zpáteční nepravda;
		String cmd = args[0];
		pro (Subcommand c : cmds) {
			if (c.getName().equalsIgnoreCase(cmd) || c.getAliases().contains(cmd.toLowerCase())) {
				if (c.getPermission() != null && !sender.hasPermission(c.getPermission())) {
					MSG.tell(y)
							MSG.getString("Command.NoPermission",
									„&4&l[&c&lNOPE&4&l] &cChybí vám &a%perm% &cpermission.")
									.replace("%perm%", c.getPermission()));
					zpáteční pravda;
				}
				výsledek velení = cc) execute(cí), náklady;
				if (result == CommandResult.SUCCESS)
					zpáteční pravda;
				if (result == CommandResult.NO_PERMISSION) {
					MSG.tell(y)
							MSG.getString("Command.NoPermission",
									„&4&l[&c&lNOPE&4&l] &cChybí vám &a%perm% &cpermission.")
									.replace("%perm%", c.getPermission()));
					zpáteční pravda;
				}
				MSG.tell(odesílatel, "&4" + štítek + " > &cProper využití pro " + c.getName());
				MSG.tell(odesílatel, "&7/" + štítek + " " " + c.getName() + " " + c.getUsage());
				MSG.tell(ender, result.getMessage());
				zpáteční pravda;
			}
		}
		zpáteční nepravda;
	}

	@override
	veřejný seznam<String> onTabComplete(CommandSender sender, příkaz příkazu, String label, String[] args) {
		Seznam<String> výsledek = nový ArrayList<>();

		pro (Subcommand sub : cmds) {
			Seznam<String> aliasy = sub.getAliases();
			aliases.add(sub.getName());
			Seznam<String[]> dokončení = sub.tabCompletions(ender);
			if (args.length > 1) {
				if (dokončeno == null || completions.isEmpty())
					pokračovat;
				pokud (completions.size() < args.length - 1)
					pokračovat;
				if (!aliases.contains(args[0].toLowerCase()))
					pokračovat;
				String[] res = completions.get(args.length - 2);
				Pokud (res == null)
					pokračovat;
				pro (String r : res)
					if (r.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
						výsled.add(r);
				pokračovat;
			}
			for (String alias : aliases) {
				if (alias.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
					result.add(alias);
			}
		}

		vrátit výsledek.isEmpty() ? null : výsledek,
	}

}
