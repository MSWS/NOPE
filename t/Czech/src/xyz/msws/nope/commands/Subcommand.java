balíček xyz.msws.nope.commands;

importovat java.util.ArrayList;
importovat java.util.List;

importovat javax.annotation.Nullable;

import org.bukkit.command.CommandSender;

importovat xyz.msws.nope.NOPE;

/**
 * Představuje podpříkaz hlavního příkazu. Očekává se, že každý subpříkaz
 * interně zpracovává svou logiku. To může zahrnovat podpříkazy subb
 * příkazy.
 * 
 * @autor imodm
 *
 */
veřejný abstraktní třída Subcommand {

	chráněný zásuvný modul NOPE;

	public Subcommand(NOPE plugin) {
		this.plugin = plugin;
	}

	@Neullable
	veřejný abstraktní seznam<String[]> tabCompletions(CommandSender sender);

	veřejný abstract String getName();

	veřejný abstract String getUsage();

	veřejný abstract String getDescription();

	veřejný seznam<String> getAliases() {
		vrátit nový ArrayList<>();
	}

	public String getPermission() {
		nulová hodnota;
	}

	abstraktní execute(CommandSender sender, String[] náklady);
}
