pachetul xyz.msws.nope.commands;

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

public abstract class AbstractCommand extinde AbstractModule implementări CommandExecutor, TabCompleter {

	Lista<Subcommand> cmds protejate = new ArrayList<>();

	public AbstractCommand(NOPE plugin) {
		super(plugin);
	}

	public abstract String getName();

	@Suprascriere
	evitați public enable() {
		PluginCommand cmd = plugin.getCommand(getName());
		cmd.setExecutor(asta);
		cmd.setTabCompleter(acest);
	}

	anulați dezactivarea publică() {
		PluginCommand cmd = plugin.getCommand(getName());
		cmd.setExecutor(null);
		cmd.setTabComplet(null);
	}

	Listă publică<Subcommand> getSubCommands() {
		cmd-uri returnate;
	}

	@Suprascriere
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length < 1)
			returnare falsă;
		Șir cmd = încarcă[0];
		pentru (Subcomandă c : cmds) {
			dacă (c.getName().equalsIgnoreCase(cmd) <unk> c.getAliases().contains(cmd.toLowerCase())) {
				dacă (c.getPermission() != null && !sender.hasPermission(c.getPermission())) {
					MSG.tell(expeditor),
							MSG.getString("Command.NoPermission",
									"&4&l[&c&lNOPE&4&&&cNu ai &o permisiune%perm%.")
									.replace("%perm%", c.getPermission());
					returnarea este reală;
				}
				Rezultat de comandă= c.execute(expeditor, argeri);
				dacă (rezultat == CommandResult.SUCCESS)
					returnarea este reală;
				dacă (rezultat == CommandResult.NO_PERMISSION) {
					MSG.tell(expeditor),
							MSG.getString("Command.NoPermission",
									"&4&l[&c&lNOPE&4&&&cNu ai &o permisiune%perm%.")
									.replace("%perm%", c.getPermission());
					returnarea este reală;
				}
				MSG.tell(expeditor, "&4" + eticheta + " > &cProper use pentru " + c.getName());
				MSG.tell(expeditor, "&7/" + label + " " + c.getName() + " " + c.getUsage());
				MSG.tell(sender, result.getMessage());
				returnarea este reală;
			}
		}
		returnare falsă;
	}

	@Suprascriere
	Listă publică<String> onTabComplete(CommandSender expeditor, Comandă, Eticheta String, String[] args) {
		Listă<String> rezultat = noul ArrayList<>();

		pentru (Subcomandă sub : cmds) {
			Lista<String> aliasuri = sub.getAliases();
			aliases.add(sub.getName());
			Listă<String[]> completări = sub.tabetions(expeditor);
			if (args.length > 1) {
				dacă (completări == null <unk> completions.isEmpty())
					continuare;
				dacă (completares.size() < args.length - 1)
					continuare;
				if (!aliases.contains(args[0].toLowerCase()))
					continuare;
				String[] res = completares.get(args.length - 2);
				dacă (res == null)
					continuare;
				pentru (şir : res)
					if (r.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
						rezultat.add(r);
				continuare;
			}
			pentru (String alias : aliass) {
				if (alias.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
					rezultat.add(alias);
			}
		}

		return result.isEmpty() ? nul : rezultat;
	}

}
