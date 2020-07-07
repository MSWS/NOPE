paketet xyz.msws.nope.commands;

importera java.util.Arraylist
importera java.util.list

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;

importera xyz.msws.nope.NOPE;
importera xyz.msws.nope.modules.AbstractModule;
import xyz.msws.nope.utils.MSG;

offentlig abstrakt klass AbstractCommand förlänger AbstractModule implementerar CommandExecutor, TabCompleter {

	skyddad lista<Subcommand> cmds = ny ArrayLista<>();

	offentlig AbstractCommand(NOPE plugin) {
		super(plugin)
	}

	offentlig abstrakt sträng getName();

	@Åsidosätt
	public void enable() {
		PluginKommando cmd = plugin.getCommand(getName());
		cmd.setExecutor(this);
		cmd.setTabCompleter(detta);
	}

	public void disable() {
		PluginKommando cmd = plugin.getCommand(getName());
		cmd.setExecutor(null);
		cmd.setTabCompleter(null);
	}

	offentlig lista<Subcommand> getSubCommands() {
		returnera cmds
	}

	@Åsidosätt
	offentlig boolean onCommand(CommandSender sender, Kommandokommando, Sträng etikett, String[] args) {
		if (args.length < 1)
			returnera falskt
		Sträng cmd = args[0]
		för (Subcommand c : cmds) {
			om (c.getName().equalsIgnoreCase(cmd) <unk> <unk> c.getAliases().innehåller(cmd.toLowerCase())) {
				om (c.getPermission() != null && !sender.hasPermission(c.getPermission())) {
					MSG.tell (avsändare,
							MSG.getString("Command.NoPermission",
									"&4&l[&c&lNOPE&4&l] &cYou lack the &a%perm% &cpermission.")
									.replace("%perm%", c.getPermission());
					returnera sant
				}
				KommandoResultat resultat = c.execute(avsändare, args);
				om (resultat == KommandoResultat.SUCCESS)
					returnera sant
				om (resultat == CommandResult.NO_PERMISSION) {
					MSG.tell (avsändare,
							MSG.getString("Command.NoPermission",
									"&4&l[&c&lNOPE&4&l] &cYou lack the &a%perm% &cpermission.")
									.replace("%perm%", c.getPermission());
					returnera sant
				}
				MSG.tell (avsändare, "&4" + etikett + " > &cKorrekt användning för " + c.getName());
				MSG.tell (avsändare, "&7/" + etikett + " " + c.getName() + " " + c.getUsage());
				MSG.tell (avsändare, result.getMessage());
				returnera sant
			}
		}
		returnera falskt
	}

	@Åsidosätt
	offentlig lista<String> onTabComplete(CommandSender sender, Kommando kommando, Sträng etikett, String[] args) {
		Lista<String> resultat = ny ArrayList<>();

		för (Subcommand sub : cmds) {
			Lista<String> alias = sub.getAlias();
			aliases.add(sub.getName());
			Lista<String[]> kompletteringar = sub.tabAvslutningar(avsändare);
			if (args.length > 1) {
				om (kompletteringar == null <unk> <unk> complettions.isEmpty())
					fortsätt
				om (complettions.size() < args.length - 1)
					fortsätt
				om (!aliases.contains(argar[0].toLowerCase()))
					fortsätt
				Sträng[] res = complettions.get(args.length - 2);
				om (res == null)
					fortsätt
				för (String r : res)
					om (r.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
						result.add(r)
				fortsätt
			}
			för (Strängalias : alias) {
				om (alias.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
					result.add(alias)
			}
		}

		returnera result.isEmpty() ? noll : resultat
	}

}
