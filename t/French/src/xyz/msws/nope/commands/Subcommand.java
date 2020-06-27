paquet xyz.msws.nope.commands;

Importer java.util.ArrayList;
Importer java.util.List;

Importer javax.annotation.Nullable;

import org.bukkit.command.CommandSender;

Importer xyz.msws.nope.NOPE ;

/**
 * Représente une sous-commande d'une commande principale. On s'attend à chaque sous-commande
 * gère sa logique interne. Cela peut inclure d'avoir des sous-commandes de sous-commandes
 * commandes.
 * 
 * @author imodm
 *
 */
sous-commande de classe abstraite publique {

	plugin NOPE protégé ;

	sous-commande publique (plugin NOPE) {
		ce.plugin = plugin;
	}

	@Nul
	liste abstraite publique<String[]> tabCompletions(CommandSender expéditeur);

	public abstract String getName();

	public abstract String getUsage();

	public abstract String getDescription();

	liste publique<String> getAliases() {
		renvoie une nouvelle liste de tableaux<>();
	}

	public String getPermission() {
		retourner null ;
	}

	public abstrait CommandResult execute(CommandSender sender, String[] args);
}
