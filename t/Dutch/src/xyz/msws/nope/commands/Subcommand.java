pakket xyz.msws.nope.commands;

importeer java.util.ArrayList;
importeer java.util.List;

importeer javax.annotation.Nullable;

import org.bukkit.command.CommandSender;

importeer xyz.msws.nope.NEPE;

/**
 * Vertegenwoordigt een subcommando van een hoofdopdracht. Er wordt elk subcommando verwacht
 * behandelt de logica intern. Dit kan inclusief het hebben van subcommando's
 * commando's.
 * 
 * @auteur imodm
 *
 */
openbare abstract class Subcommand {

	bescherming van NOPE plugin;

	publieke Subcommand(NOPE plugin) {
		this.plugin = plugin;
	}

	@Nulbaar
	openbare abstract Lijst<String[]> tabCompletions(opdrachtzender);

	openbare abstracte tekenreeks getName();

	openbare abstract String getUge();

	openbare abstract tekenreeks getOmschrijving();

	openbare lijst<String> getAliases() {
		retourneert nieuwe ArrayList<>();
	}

	publieke String getPermission() {
		retourneer null;
	}

	openbare abstract opdrachtresultaat uitvoering (verzender van opdrachtzender, String[] args);
}
