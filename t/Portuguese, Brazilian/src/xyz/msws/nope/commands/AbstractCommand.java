pacote xyz.msws.nope.commands;

importar java.util.ArrayList;
importar java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;

importar xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.AbstractModule;
importar xyz.msws.nope.utils.MSG;

public abstract class AbstractCommand extends AbstractModule implementa CommandExecutor, TabCompleter {

	lista protegida<Subcommand> cmds = new ArrayList<>();

	public AbstractCommand(Nplugin SEP) {
		super(plugin);
	}

	public abstract String getName();

	Ignorar
	void void habilitado() {
		PluginCommand cmd = plugin.getCommand(getName());
		cmd.setExecutor(isto);
		cmd.setTabCompleter(isto);
	}

	public void disable() {
		PluginCommand cmd = plugin.getCommand(getName());
		cmd.setExecutor(null);
		cmd.setTabCompleter(null);
	}

	public List<Subcommand> getSubCommands() {
		retornar cmds;
	}

	Ignorar
	booleano público onCommand(CommandSender sender, comando de comando, nome de string, String[] args) {
		if (args.length < 1)
			retornar falso;
		Texto = args[0];
		for (Subcomando : cmds) {
			if (c.getName().equalsIgnoreCase(cmd) ├c.getAliases().contains(cmd.toLowerCase())) {
				if (c.getPermission() != null && !sender.hasPermission(c.getPermission())) {
					MSG.tell(sender,
							MSG.getString("Command.NoPermission",
									"&4&l[&c&lNOPE&4&l] &cVocê não possui uma &a permissão de%perm%.")
									.replace("%perm%", c.getPermission()));
					retornar verdadeiro;
				}
				Resultado do CommandResultult = c.execute(sender, args);
				if (result == CommandResult.SUCCESS)
					retornar verdadeiro;
				if (result == CommandResult.NO_PERMISSION) {
					MSG.tell(sender,
							MSG.getString("Command.NoPermission",
									"&4&l[&c&lNOPE&4&l] &cVocê não possui uma &a permissão de%perm%.")
									.replace("%perm%", c.getPermission()));
					retornar verdadeiro;
				}
				MSG.tell(sender, "&4" + label + " > &cProper uso para " + c.getName());
				MSG.tell(sender, "&7/" + label + " + c.getName() + " + c.getUsage());
				MSG.tell(sender, resultado.getMessage());
				retornar verdadeiro;
			}
		}
		retornar falso;
	}

	Ignorar
	public List<String> onTabComplete(CommandSender sender, comando de comando, String label, String[] args) {
		Lista<String> resultado = new ArrayList<>();

		for (Subcomando sub : cmds) {
			Listar<String> apelidos = sub.getAliases();
			aliases.add(sub.getName());
			Listar<String[]> complementos = sub.tabCompletions(sender);
			if (args.length > 1) {
				if (completações == null 「completions.isEmpty())
					continuar;
				if (completions.size() < args.length - 1)
					continuar;
				if (!aliases.contains(args[0].toLowerCase()))
					continuar;
				String[] res = completions.get(args.length - 2);
				if (res == null)
					continuar;
				for (String r : res)
					if (r.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
						resultado.add(r);
				continuar;
			}
			for (String alias : aliases) {
				if (alias.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
					resultado.add(alias);
			}
		}

		retornar resultado.isEmpty() ? null : resultado;
	}

}
