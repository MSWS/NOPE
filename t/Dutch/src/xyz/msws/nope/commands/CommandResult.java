pakket xyz.msws.nope.commands;

importeer xyz.msws.nope.utils.MSG;

/**
 * Vertegenwoordigt het resultaat van een commando.
 * 
 * @auteur imodm
 *
 */
publieke opdrachtresultaat {
	/**
	 * De opdracht is succesvol voltooid. Een aangepast succesbericht moet zijn
	 * verzonden.
	 */
	SUCCES,
	/**
	 * De afzender heeft niet de juiste permissies voor de opdracht.
	 */
	NO_PERMISSIES
	/**
	 * Er ontbreekt een argument.
	 */
	OPPONENT_MATCH_DESCRIPTION
	/**
	 * Een ongeldig argument is opgegeven.
	 */
	DAILY_SPIN_DESCRIPTION
	/**
	 * Alleen een speler kan de opdracht gebruiken en de afzender is er niet.
	 */
	PLAYLIST_DEACTIVATE_BTN
	/**
	 * De executeur gaf geen speler, hetzelfde als
	 * {@link CommandResult#MISSING_ARGUMENT} maar meer specifiek
	 */
	LOCAL_NOTIF_SETTINGS_POPUP_TITLE
	/**
	 * Er is een onbekende fout opgetreden
	 */
	FOUT;

	openbare tekenreeks getMessage() {
		switch (deze) {
			case INVALID_ARGUMENT:
				return MSG.getString("Command.InvalidArgument", "&cEen ongeldig argument is opgegeven.");
			case MISSING_ARGUMENT:
				return MSG.getString("Command.MissingArgument", "&cJe mist een argument.");
			case NO_PERMISSIE:
				retourneer MSG.getString("Command.NoPermission",
						"&4&l[&c&lNOPE&4&l] &cU mist de &a%perm% &cpermission.");
			case PLAYER_ONLINE
				retourneer MSG.getString("Command.PlayerOnly",
						"&cYou moet een speler opgeven om deze opdracht uit te voeren als console.");
			case PLAYER_REQUIRED:
				return MSG.getString("Command.SpecifyPlayer", "&cJe moet een speler opgeven als argument.");
			case SUCCES:
				retourneer "";
			standaard:
				pak;
		}
		geef "&4Er is een fout opgetreden tijdens het uitvoeren van de opdracht.";
	}
}
