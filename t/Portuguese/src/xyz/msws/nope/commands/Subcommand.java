pacote xyz.msws.nope.commands;

importar java.util.ArrayList;
importar java.util.List;

importar javax.annotation.Nullable;

import org.bukkit.command.CommandSender;

importar xyz.msws.nope.NOPE;

/**
 * Representa um subcomando de um comando principal. É esperado cada subcomando
 * lida com sua lógica internamente. Isso pode incluir ter sub comandos do sub
 * comandos.
 * 
 * Iodm @autor
 *
 */
subcomando público de classe abstrata {

	plugin NOPE protegido;

	subcomando público(NÃO plugin) {
		this.plugin = plugin;
	}

	@Nulável
	public abstraact List<String[]> tabCompletions(CommandSender sender);

	public abstract String getName();

	public abstract String getUsage();

	public abstract String getDescription();

	public List<String> getAliases() {
		retorne novo ArrayList<>();
	}

	public String getPermission() {
		retornar null;
	}

	public abstract CommandResultult execute(CommandSender sender, String[] argumentos);
}
