pachetul xyz.msws.nope.commands;

import xyz.msws.nope.utils.MSG;

/**
 * Reprezintă rezultatul unei comenzi.
 * 
 * @autor imodm
 *
 */
public enum CommandResult {
	/**
	 * Comanda a fost finalizată cu succes. Un mesaj de succes personalizat ar trebui să fie
	 * trimis.
	 */
	SUCHESTIUNI,
	/**
	 * Expeditorul nu are permisiunile corecte pentru comandă.
	 */
	Nespecificat,
	/**
	 * Lipsește un argument.
	 */
	MISING_ARGUMENT,
	/**
	 * Un argument nevalid este prezentat.
	 */
	INVALID_ARGUMENT,
	/**
	 * Doar un jucător poate folosi comanda și expeditorul nu este unul.
	 */
	Unelt,
	/**
	 * Executorul nu a dat un jucător, la fel ca
	 * {@link CommandResult#MISSING_ARGUMENT} dar mai specific
	 */
	Specifică,
	/**
	 * A apărut o eroare necunoscută
	 */
	EROARE;

	public String getMessage() {
		comutaţi (this) {
			caz INVALID_ARGUMENT:
				return MSG.getString("Command.InvalidArgument", "&cA fost furnizat un argument nevalid.");
			MISIUNE Caz:
				return MSG.getString("Command.MissingArgument", "&cNu aveți un argument.");
			cazul NU_PERMISIUNE:
				return MSG.getString("Command.NoPermission",
						"&4&xiz[&c&lNOPE&4&&] &cNu ai &o%perm% &cpermission.");
			CAZUL JUCĂTORULULU:
				return MSG.getString("Command.PlayerOnly",
						"&cTrebuie sa specifici un jucator pentru a rula aceasta comanda ca consola.");
			cazul JUCĂTORUL_CERER:
				return MSG.getString("Command.SpecifyPlayer", "&cTrebuie să specificați un jucător ca argument.");
			SUCCESUL DE cazuri:
				returnează "";
			implicit:
				pauză;
		}
		returnează "&4A apărut o eroare în timpul executării comenzii.";
	}
}
