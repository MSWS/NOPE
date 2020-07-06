Pakiet xyz.msws.nope.commands;

importuj java.util.ArrayList;
importuj java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;

import xyz.msws.nope.NOPE;
importuj xyz.msws.nope.modules.AbstractModule;
import xyz.msws.nope.utils.MSG;

publiczna klasa abstrakcyjna AbstractCommand rozszerza AbstractModule implements CommandExecutor, TabCompleter {

	chroniona lista<Subcommand> cmds = nowa ArrayList<>();

	publiczna wtyczka AbstractCommand(NOPE) {
		super(wtyczka);
	}

	abstrakcyjny publiczny „String getName();

	@Nadpisz
	włączono ubytek () {
		PluginCommand cmd = plugin.getCommand(getName());
		cmd.setExecutor(this);
		cmd.setTabCompleter(to);
	}

	ubytek wyłączony() {
		PluginCommand cmd = plugin.getCommand(getName());
		cmd.setExecutor(null);
		cmd.setTabCompleter(null);
	}

	publiczna lista<Subcommand> getSubCommands() {
		nachylenie położenia zwrotnego cm;
	}

	@Nadpisz
	Publiczny boolean onCommand(CommandSender sender, Command Command, String label, String[] args) {
		if (args.length < 1)
			fałszywe zwroty;
		Ciągi cmd = args[0];
		for (podpolecenie c : cmds) {
			if (c.getName().equalsIgnoreCase(cmd) || c.getAliases().contains(cmd.toLowerCase())) {
				if (c.getPermission() != null && !sender.hasPermission(c.getPermission())) {
					MSG.tell(płeć),
							MSG.getString("Command.NoPermission",
									"&4&l[&c&lNOPE&4&l] &cBrakuje &%perm% &cpermission.")
									.replace("%perm%", c.getPermission()));
					zwróć prawdę;
				}
				Wynik komendy = c.execute(sender, args);
				if (wynik == CommandResult.SUCCESS)
					zwróć prawdę;
				if (wynik == CommandResult.NO_PERMISSION) {
					MSG.tell(płeć),
							MSG.getString("Command.NoPermission",
									"&4&l[&c&lNOPE&4&l] &cBrakuje &%perm% &cpermission.")
									.replace("%perm%", c.getPermission()));
					zwróć prawdę;
				}
				MSG.tell(sender, "&4" + label + " > &cPrawidłowe użycie dla " + c.getName());
				MSG.tell(sender, "&7/" + label + " " + c.getName() + " " + c.getUsage());
				MSG.tell(sender, result.getMessage());
				zwróć prawdę;
			}
		}
		fałszywe zwroty;
	}

	@Nadpisz
	lista publiczna<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		Wynik listy<String> = nowy ArrayList<>();

		dla (podpolecenie podrzędne: cmds) {
			Lista<String> aliasów = sub.getAliases();
			aliases.add(sub.getName());
			Lista<String[]> uzupełnień = sub.tabCompletions(sender);
			if (args.length > 1) {
				if (uzupełnienia == null || completions.isEmpty())
					kontynuować;
				if (completions.size() < args.length - 1)
					kontynuować;
				if (!aliases.contains(args[0].toLowerCase()))
					kontynuować;
				String[] res = completions.get(args.length - 2);
				if (res == null)
					kontynuować;
				for (String r: res)
					jeżeli (r.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
						wynik.add(r);
				kontynuować;
			}
			for (alias String : aliases) {
				if (alias.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
					wynik.add(alias);
			}
		}

		Zwróć result.isEmpty() ? null : wynik;
	}

}
