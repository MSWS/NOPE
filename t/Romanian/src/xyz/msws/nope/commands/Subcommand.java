pachetul xyz.msws.nope.commands;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import org.bukkit.command.CommandSender;

import xyz.msws.nope.NOPE;

/**
 * Reprezintă o subcomandă a unei comenzi principale. Se așteaptă ca fiecare subcomandă să fie
 * se ocupă intern de logica sa. Acest lucru poate include să aibă sub-comenzi
 * comenzi.
 * 
 * @autor imodm
 *
 */
public abstract class Subcommand {

	plugin-ul protejat NOPE;

	public Subcommand(NOPE plugin) {
		this.plugin = plugin;
	}

	@Nulable
	public abstract List<String[]> tabuCompletions(CommandSender sender);

	public abstract String getName();

	Șir public abstract getUsage();

	public abstract String getDescription();

	Listă publică<String> getAliases() {
		returnează noul ArrayList<>();
	}

	public String getPermission() {
		returnarea null;
	}

	public abstract CommandResult execute(CommandSender sender, String[] argini);
}
