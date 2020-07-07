pakket xyz.msws.nope.commands;

importeer java.util.ArrayList;
importeer java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;

importeer xyz.msws.nope.NEPE;
importeer xyz.msws.nope.modules.AbstractModule;
importeer xyz.msws.nope.utils.MSG;

publieke abstract class AbstractCommand breidt AbstractModule de CommandExecutor, TabCompleter {

	beschermde lijst<Subcommand> cmds = nieuwe ArrayList<>();

	publieke AbstractCommand(NOPE plugin) {
		super(plugin);
	}

	openbare abstracte tekenreeks getName();

	@Overschrijven
	publieke ongeldig enable() {
		PluginCommand cmd = plugin.getCommand(getName());
		cmd.setExecutor(deze);
		cmd.setTabCompleter(deze);
	}

	publieke void disable() {
		PluginCommand cmd = plugin.getCommand(getName());
		cmd.setExecutor(null);
		cmd.setTabCompleter(null);
	}

	openbare lijst<Subcommand> getSubCommands() {
		return cmds;
	}

	@Overschrijven
	publieke boolean onCommand(CommandSender afzender, Opdracht commando, String label, String[] args) {
		if (args.length < 1)
			geef onwaar;
		String cmd = argumenten[0];
		for (Subcommand c : cmds) {
			if (c.getName().equalsIgnoreCase(cmd) ‧ c.getAliases().contains(cmd.toLowerCase())) {
				if (c.getPermission() != null && !sender.hasPermission(c.getPermission())) {
					MSG.tell(afzender,
							MSG.getString("Command.NoPermission",
									"&4&l[&c&lNOPE&4&l] &cU mist de &a%perm% &cpermission.")
									.replace("%perm%", c.getPermission()));
					keer waar;
				}
				Resultaat = c.execute(afzender, args);
				if (resultaat == CommandResult.SUCCESS)
					keer waar;
				if (resultaat == CommandResult.NO_PERMISSION) {
					MSG.tell(afzender,
							MSG.getString("Command.NoPermission",
									"&4&l[&c&lNOPE&4&l] &cU mist de &a%perm% &cpermission.")
									.replace("%perm%", c.getPermission()));
					keer waar;
				}
				MSG.tell(afzender, "&4" + label + " > &cProper gebruik voor " + c.getName());
				MSG.tell(afzender, "&7/" + label + " " " + c.getName() + " + c.getUsage());
				MSG.tell(afzender, resultaat.getMessage());
				keer waar;
			}
		}
		geef onwaar;
	}

	@Overschrijven
	openbare lijst<String> onTabComplete(CommandSender afzender, Commando command, String label, String[] args) {
		Lijst<String> resultaat = nieuwe ArrayList<>();

		for (Subcommand sub : cmds) {
			Lijst<String> aliassen = sub.getAliases();
			aliases.add(sub.getName());
			Lijst<String[]> voltooiingen = sub.tabCompletions(afzender);
			if (args.length > 1) {
				if (completies == null +unnamed@@0 completions.isEmpty())
					doorgaan;
				if (completions.size() < args.length - 1)
					doorgaan;
				als (!aliases.contains(args[0].toLowerCase()))
					doorgaan;
				String[] res = completions.get(args.length - 2);
				if (res == null)
					doorgaan;
				for (String r : res)
					if (r.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
						resultaat.add(r);
				doorgaan;
			}
			voor (String alias : aliases) {
				if (alias.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
					resultaat.add(alias);
			}
		}

		retourneer resultaat.isEmpty() ? nul : resultaat;
	}

}
