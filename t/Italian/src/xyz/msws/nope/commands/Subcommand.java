pacchetto xyz.msws.nope.commands;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import org.bukkit.command.CommandSender;

import xyz.msws.nope.NOPE;

/**
 * Rappresenta un sottocomando di un comando principale. È atteso ogni sottocomando
 * gestisce internamente la sua logica. Questo può includere avere sub comandi di sub
 * comandi.
 * 
 * @author imodm
 *
 */
public abstract class Subcommand {

	plugin NOPE protetto;

	public Subcommand(NOPE plugin) {
		this.plugin = plugin;
	}

	@Nullable
	elenco astratto pubblico<String[]> tabCompletions(CommandSender sender);

	public abstract String getName();

	public abstract String getUsage();

	pubblico astratto String getDescription();

	public List<String> getAliases() {
		restituisce la nuova ArrayList<>();
	}

	public String getPermission() {
		return null;
	}

	public abstract CommandResult execute(CommandSender sender, args[] string);
}
