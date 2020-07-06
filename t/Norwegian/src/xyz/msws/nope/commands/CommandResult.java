pakke xyz.msws.nope.commands;

Importer xyz.msws.nope.utils.MSG;

/**
 * Representerer resultatet av en kommando.
 * 
 * @forfatter imodm
 *
 */
allmenheten CommandResult {
	/**
	 * Kommandoen ble fullført uten feil. En egendefinert suksessmelding skal være
	 * sendt.
	 */
	ABONNER
	/**
	 * Avsenderen har ikke de riktige tillatelsene for kommandoen.
	 */
	NO_PERMISON,
	/**
	 * Et argument mangler.
	 */
	OVERSING_ARGUMENT,
	/**
	 * Et ugyldig argument er gitt.
	 */
	INVALID_ARGUMENT,
	/**
	 * Bare en spiller kan bruke kommandoen og avsenderen er ikke en.
	 */
	Kun SPILLER_KUN
	/**
	 * Utføreren ga ikke en spiller, samme som
	 * {@link CommandResultat#MISSING_ARGUMENT} men mer spesifikk
	 */
	SPILLER_KRAV
	/**
	 * En ukjent feil oppstod
	 */
	FEIL;

	public String getMessage() {
		Bytt (denne) {
			case INVALID_ARGUMENT:
				return MSG.getString("Command.InvalidArgument", "&cAn invalid argument was provided.");
			sak MISSING_ARGUMENT:
				return MSG.getString("Command.MissingArgument", "&cYou mangler et argument.");
			case NO_PERMISON:
				returner MSG.getString("Command.NoPermission",
						"&4&l[&c&lNOPE&4&l] &cDu mangler &en%perm% cpermission.");
			case PLAYER_KUN
				returner MSG.getString("Command.PlayerOnly",
						"&cDu må spesifisere en spiller for å kjøre denne kommandoen som konsoll.");
			kasus SPILLER_NÅR:
				returner MSG.getString("Command.SpecifyPlayer", "&cYou må angi en spiller som et argument.");
			case SUCCESS:
				retur"";
			standard:
				brudd;
		}
		returnere "&4En feil oppstod mens kommandoen ble utført.";
	}
}
