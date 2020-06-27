pakke xyz.msws.nope.commands;

Importer java.util.ArrayList;
Importer java.util.List;

importer javax.annotation.Nullverdi;

import org.bukkit.command.CommandSender;

importer xyz.msws.nope.NOPE;

/**
 * Representerer en underkommando i en hovedkommando. Det forventes hver underkommando
 * håndterer logikken innvendig. Dette kan innbefatte underkommandoer av sub
 * kommandoer.
 * 
 * @forfatter imodm
 *
 */
offentlig abstract klasse Subcommand {

	beskyttet NOPE- utvidelse;

	offentlig underkommando (NOPE plugin) {
		this.plugin = plugin;
	}

	@stillbar
	public abstract List<String[]> tabletions(CommandSender avsender);

	public abstract string getName();

	offentlig abstract string getUsage();

	offentlig abstract string getDescription();

	offentlig liste<String> getAliases() {
		returnere ny arrayList<>();
	}

	public String getPermission() {
		nullstilt retur;
	}

	public abstract Commandult execute(CommandSender sender, String[] args);
}
