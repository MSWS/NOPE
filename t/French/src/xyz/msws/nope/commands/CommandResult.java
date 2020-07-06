paquet xyz.msws.nope.commands;

Importer xyz.msws.nope.utils.MSG;

/**
 * Représente le résultat d'une commande.
 * 
 * @author imodm
 *
 */
numéro public CommandResult {
	/**
	 * La commande a été complétée avec succès. Un message personnalisé de succès devrait être
	 * envoyé.
	 */
	SUCCÈS,
	/**
	 * L'expéditeur n'a pas les permissions appropriées pour la commande.
	 */
	Vous n'avez pas la permission requise
	/**
	 * Un argument est manquant.
	 */
	MISSING_ARGUMENT,
	/**
	 * Un argument invalide est donné.
	 */
	INVALID_ARGUMENT
	/**
	 * Seul un joueur peut utiliser la commande et l'expéditeur ne l'est pas.
	 */
	PLAYER_SEULEMENT
	/**
	 * L'exécuteur n'a pas donné de joueur, identique à
	 * {@link CommandResult#MISSING_ARGUMENT} mais plus spécifique
	 */
	PLAYER_RESULTANT
	/**
	 * Une erreur inconnue est survenue
	 */
	Erreur;

	chaîne publique getMessage() {
		basculer (ceci) {
			cas INVALID_ARGUMENT :
				return MSG.getString("Command.InvalidArgument", "&cUn argument invalide a été fourni.");
			format@@0 case MISSING_ARGUMENT :
				return MSG.getString("Command.MissingArgument", "&cIl vous manque un argument.");
			format@@0 case NO_PERMISSION :
				retourner MSG.getString("Command.NoPermission",
						"&4&l[&c&lNOPE&4&l] &cIl vous manque &a%perm% &cpermission.");
			cas PLAYER_UNIQUEMENT :
				retourner MSG.getString("Command.PlayerOnly",
						"&cVous devez spécifier un joueur pour exécuter cette commande en tant que console.");
			format@@0 case PLAYER_REQUIRED :
				retourner MSG.getString("Command.SpecifyPlayer", "&cVous devez spécifier un joueur comme argument.");
			cas SUCCÈS :
				retour "";
			par défaut:
				casse ;
		}
		retourne "&4Une erreur est survenue lors de l'exécution de la commande.";
	}
}
