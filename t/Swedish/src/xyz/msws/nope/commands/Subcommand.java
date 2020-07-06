paketet xyz.msws.nope.commands;

importera java.util.Arraylist
importera java.util.list

importera javax.annotation.Nullable;

import org.bukkit.command.CommandSender;

importera xyz.msws.nope.NOPE;

/**
 * Representerar ett underkommando till ett huvudkommando. Det förväntas varje underkommando
 * hanterar sin logik internt. Detta kan inkludera att ha underkommandon för underleverantörer
 * kommandon.
 * 
 * @author imodm
 *
 */
offentlig abstrakt klass Underkommando {

	skyddad NOPE plugin

	offentligt underkommando (NOPE plugin) {
		this.plugin = plugin;
	}

	@Nullable
	offentlig abstrakt lista<String[]> tabAvslutningar(CommandSender avsändare);

	offentlig abstrakt sträng getName();

	offentlig abstrakt sträng getUsage();

	offentlig abstrakt sträng getDescription();

	offentlig lista<String> getAliases() {
		returnera ny ArrayList<>();
	}

	offentlig sträng getPermission() {
		retur null,
	}

	offentligt abstrakt kommandoResultat exekvera(CommandSender sender, String[] args);
}
