pakke xyz.msws.nope.commands;

Importer java.util.ArrayList;
Importer java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;

importer xyz.msws.nope.NOPE;
Importer xyz.msws.nope.modules.AbstractModule;
Importer xyz.msws.nope.utils.MSG;

offentlig abstract class AbstractCommand utvider AbstractModule implementerer CommandExecutor, TabCompleter {

	beskyttet liste<Subcommand> cmds = ny liste <>();

	public AbstractCommand(NOPE plugin) {
		super(plugin);
	}

	public abstract string getName();

	@Overstyring
	offentlig aktivering() {
		PluginCommand cmd = plugin.getCommand(getName());
		cmd.setExecutor(dette);
		cmd.setTabCompleter(dette);
	}

	public void disable() {
		PluginCommand cmd = plugin.getCommand(getName());
		cmd.setExecutor(null);
		cmd.setTabKompletter(null);
	}

	offentlig liste<Subcommand> getSubCommands() {
		returcmds;
	}

	@Overstyring
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length < 1)
			returfeil;
		Streng cmd = mål[0];
		for (Subcommandc : cmds) {
			hvis (c.getName().equalsIgnoreCase(cmd) butic.getAliases().contains(cmd.toLowerCase())) {
				hvis (c.getPermission() != null && !sender.hasPermission(c.getPermission())) {
					MSG.tell(sender,
							MSG.getString("Command.NoPermission",
									"&4&l[&c&lNOPE&4&l] &cDu mangler &en%perm% &cpermission.")
									.replace("%perm%", c.getPermission());
					retursann;
				}
				Kommandoresultat = c.execute(sender, argers);
				hvis (resultat == CommandResult.SUCCESS)
					retursann;
				hvis (resultat == Kommandoresultat.NO_PERMISSION) {
					MSG.tell(sender,
							MSG.getString("Command.NoPermission",
									"&4&l[&c&lNOPE&4&l] &cDu mangler &en%perm% &cpermission.")
									.replace("%perm%", c.getPermission());
					retursann;
				}
				MSG.tell(sender, "&4" + etikett + " > &cProper bruk for " + c.getName());
				MSG.tell(sender, "&7/" + etikett + " " " + c.getName() + " " + c.getUsage());
				MSG.tell(sender, resultat.getMessage());
				retursann;
			}
		}
		returfeil;
	}

	@Overstyring
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		Liste<String> resultat = ny liste<>();

		for (Subcommand sub : cmds) {
			Liste<String> aliaser = under.getAliaser();
			aliases.add(sub.getName());
			Liste<String[]> Ferdigheter = sub.tabkompletions(avsender);
			if (args.length > 1) {
				hvis (fullføring== null finish fullført.isfylt())
					fortsett;
				hvis (fullføress.size() < args.length - 1)
					fortsett;
				hvis !aliases.container(varer[0].toLowerCase()))
					fortsett;
				String[] res = ferdigheter.get(args.lengde - 2);
				hvis (res == null)
					fortsett;
				for (String r: res)
					hvis (r.toLowerCase).startsWith(args[args.length - 1].toLowerCase()))
						resultat.add(r);
				fortsett;
			}
			for (Tring alias : aliaser) {
				hvis (alias.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
					resultat.add(alias);
			}
		}

		returnere resultat.is(6 m?? null : resultat;
	}

}
