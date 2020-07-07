paketet xyz.msws.nope.commands;

import xyz.msws.nope.utils.MSG;

/**
 * Representerar resultatet av ett kommando.
 * 
 * @author imodm
 *
 */
offentligt enum CommandResult {
	/**
	 * Kommandot slutfördes framgångsrikt. Ett anpassat framgångsmeddelande bör vara
	 * skickad.
	 */
	SUCCESS,
	/**
	 * Avsändaren har inte rätt behörighet för kommandot.
	 */
	NO_PERMISSION,
	/**
	 * Ett argument saknas.
	 */
	MISSING_ARGUMENT,
	/**
	 * Ett ogiltigt argument anges.
	 */
	INVALID_ARGUMENT,
	/**
	 * Endast en spelare kan använda kommandot och avsändaren är inte en.
	 */
	ENDAST SPELAR,
	/**
	 * Exekutorn gav inte en spelare, samma som
	 * {@link CommandResult#MISSING_ARGUMENT} men mer specifik
	 */
	SPELER_KRÄVER,
	/**
	 * Ett okänt fel uppstod
	 */
	FEL

	offentlig String getMessage() {
		växla (detta) {
			case INVALID_ARGUMENT:
				returnera MSG.getString("Kommando.InvalidArgument", "&cEtt ogiltigt argument angavs".);
			case MISSING_ARGUMENT:
				returnera MSG.getString("Kommando.MissingArgument", "&cYou saknar ett argument.");
			case NO_PERMISSION:
				returnera MSG.getString("Command.NoPermission",
						"&4&l[&c&lNOPE&4&l] &cYou lack the &a%perm% &cpermission.");
			case PLAYER_ONLY:
				returnera MSG.getString("Command.PlayerOnly",
						"&cYou måste ange en spelare för att köra detta kommando som console.");
			fall SPELER_KRÄVS:
				returnera MSG.getString("Command.SpecifyPlayer", "&cYou måste ange en spelare som ett argument.");
			fall SUCCESS:
				återvänd "";
			standard:
				paus
		}
		returnera "&4Ett fel uppstod vid körning av kommandot.";
	}
}
