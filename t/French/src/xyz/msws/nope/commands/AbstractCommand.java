paquet xyz.msws.nope.commands;

Importer java.util.ArrayList;
Importer java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;

Importer xyz.msws.nope.NOPE ;
Importer xyz.msws.nope.modules.AbstractModule ;
Importer xyz.msws.nope.utils.MSG;

public abstract class AbstractCommand extends AbstractModule implémente CommandExecutor, TabCompleter {

	liste protégée<Subcommand> cmds = nouvelle liste de tableaux<>();

	AbstractCommand(plugin NOPE) public {
		super(plugin);
	}

	public abstract String getName();

	@Remplacer
	public void enable() {
		cmd PluginCommand = plugin.getCommand(getName());
		cmd.setExecutor(ceci);
		cmd.setTabCompleter(ceci);
	}

	public void disable() {
		cmd PluginCommand = plugin.getCommand(getName());
		cmd.setExecutor(null);
		cmd.setTabCompleter(null);
	}

	liste publique<Subcommand> getSubCommands() {
		retourner des cmds;
	}

	@Remplacer
	public boolean onCommand(CommandSender sender, Commande de commande, Label de chaîne, String[] args) {
		if (args.length < 1)
			retourner faux;
		Chaîne cmd = args[0];
		pour (Sous-commande c : cmds) {
			if (c.getName().equalsIgnoreCase(cmd) || c.getAliases().contains(cmd.toLowerCase())) {
				if (c.getPermission() != null && !sender.hasPermission(c.getPermission())) {
					MSG.tell(sender,
							MSG.getString("Command.NoPermission",
									"&4&l[&c&lNOPE&4&l] &cIl vous manque &a%perm% &cpermission.")
									.replace("%perm%", c.getPermission()));
					retourner vrai;
				}
				CommandResult result = c.execute(sender, args);
				if (résultat == CommandResult.SUCCESS)
					retourner vrai;
				if (résultat == CommandResult.NO_PERMISSION) {
					MSG.tell(sender,
							MSG.getString("Command.NoPermission",
									"&4&l[&c&lNOPE&4&l] &cIl vous manque &a%perm% &cpermission.")
									.replace("%perm%", c.getPermission()));
					retourner vrai;
				}
				MSG.tell(sender, "&4" + label + " > &cUtilisation appropriée pour " + c.getName());
				MSG.tell(sender, "&7/" + label + " " + c.getName() + " " + c.getUsage());
				MSG.tell(sender, result.getMessage());
				retourner vrai;
			}
		}
		retourner faux;
	}

	@Remplacer
	liste publique<String> onTabComplete(CommandSender sender, Commande de commande, Label de chaîne, String[] args) {
		Liste<String> résultat = nouvelle liste de tableaux<>();

		pour (sous-commande sub : cmds) {
			Liste<String> alias = sub.getAliases();
			aliases.add(sub.getName());
			Liste<String[]> completions = sub.tabCompletions(sender);
			if (args.length > 1) {
				if (completions == null || completions.isEmpty())
					continuez;
				if (completions.size() < args.length - 1)
					continuez;
				if (!aliases.contains(args[0].toLowerCase()))
					continuez;
				String[] res = completions.get(args.length - 2);
				if (res == null)
					continuez;
				pour (String r : s)
					if (r.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
						result.add(r);
				continuez;
			}
			pour (String alias : aliases) {
				if (alias.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
					result.add(alias);
			}
		}

		retourner result.isEmpty() ? null : résultat ;
	}

}
