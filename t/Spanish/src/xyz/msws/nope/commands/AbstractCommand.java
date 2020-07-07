paquete xyz.msws.nope.commands;

importar java.util.ArrayList;
importar java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;

importar xyz.msws.nope.NOPE;
importar xyz.msws.nope.modules.AbstractModule;
importar xyz.msws.nope.utils.MSG;

public abstract class AbstractCommand extends AbstractModule implementa CommandExecutor, TabCompleter {

	protected List<Subcommand> cmds = new ArrayList<>();

	public AbstractCommand(NOPE plugin) {
		super(plugin);
	}

	public abstract String getName();

	@Sobreescribir
	public void enable() {
		PluginCommand cmd = plugin.getCommand(getName());
		cmd.setExecutor(esto);
		cmd.setTabCompleter(esto);
	}

	public void disable() {
		PluginCommand cmd = plugin.getCommand(getName());
		cmd.setExecutor(null);
		cmd.setTabCompleter(null);
	}

	public List<Subcommand> getSubCommands() {
		devolver cmds;
	}

	@Sobreescribir
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length < 1)
			retorno falso;
		Cadena cmd = argumentos[0];
		for (Subcommand c : cmds) {
			if (c.getName().equalsIgnoreCase(cmd) || c.getAliases().contains(cmd.toLowerCase())) {
				if (c.getPermission() != null && !sender.hasPermission(c.getPermission())) {
					MSG.tell(remitente,
							MSG.getString("Command.Nopermission",
									"&4&l[&c&lNOPE&4&l] &cTe falta &a%perm% &cpermisión.")
									.replace("%perm%", c.getPermission()));
					retorno verdadero;
				}
				Resultado de Comandos = c.execute(sender, args);
				si (resultado == CommandResult.SUCCESS)
					retorno verdadero;
				if (result == CommandResult.NO_PERMISSION) {
					MSG.tell(remitente,
							MSG.getString("Command.Nopermission",
									"&4&l[&c&lNOPE&4&l] &cTe falta &a%perm% &cpermisión.")
									.replace("%perm%", c.getPermission()));
					retorno verdadero;
				}
				MSG.tell(sender, "&4" + label + " > &cProper usage for " + c.getName());
				MSG.tell(sender, "&7/" + etiqueta + " " + c.getName() + " " + c.getUsage());
				MSG.tell(sender, result.getMessage());
				retorno verdadero;
			}
		}
		retorno falso;
	}

	@Sobreescribir
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		Lista<String> resultado = new ArrayList<>();

		for (Subcommand sub : cmds) {
			Lista<String> aliases = sub.getAliases();
			aliases.add(sub.getName());
			Lista<String[]> compleciones = sub.tabCompletions(sender);
			if (args.length > 1) {
				if (completions == null || completions.isEmpty())
					continuar;
				if (completions.size() < args.length - 1)
					continuar;
				if (!aliases.contains(args[0].toLowerCase()))
					continuar;
				Cadena[] res = completions.get(args.length - 2);
				if (res == nulo)
					continuar;
				para (String r : res)
					if (r.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
						result.add(r);
				continuar;
			}
			for (String alias : aliases) {
				if (alias.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
					result.add(alias);
			}
		}

		return result.isEmpty() ? null : resultado;
	}

}
