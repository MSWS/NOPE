paquete xyz.msws.nope.commands;

importar java.util.ArrayList;
importar java.util.List;

importar javax.annotation.Nullable;

import org.bukkit.command.CommandSender;

importar xyz.msws.nope.NOPE;

/**
 * Representa un subcomando de un comando principal. Se espera cada subcomando
 * maneja su lógica internamente. Esto puede incluir tener subcomandos de subcomandos
 * comandos.
 * 
 * imodm @author
 *
 */
public abstract class Subcommand {

	protegido plugin NOPE;

	public Subcommand(NOPE plugin) {
		this.plugin = plugin;
	}

	@Nullable
	Lista abstracta pública<String[]> tabletas (remitente del commandSender);

	public abstract String getName();

	public abstract String getUsage();

	public abstract String getDescription();

	public List<String> getAliases() {
		devolver nueva ArrayList<>();
	}

	public String getPermission() {
		devolver nulo;
	}

	public abstract CommandResult execute(CommandSender sender, String[] args);
}
