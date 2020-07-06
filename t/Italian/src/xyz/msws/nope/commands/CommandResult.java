pacchetto xyz.msws.nope.commands;

import xyz.msws.nope.utils.MSG;

/**
 * Rappresenta il risultato di un comando.
 * 
 * @author imodm
 *
 */
public enum CommandResult {
	/**
	 * Il comando è stato completato con successo. Un messaggio di successo personalizzato dovrebbe essere
	 * inviato.
	 */
	SUCCESSO,
	/**
	 * Il mittente non ha i permessi adeguati per il comando.
	 */
	NO_PERMISSION,
	/**
	 * Manca un argomento.
	 */
	MISSING_ARGUMENT,
	/**
	 * Viene fornito un argomento non valido.
	 */
	INVALID_ARGUMENT,
	/**
	 * Solo un giocatore può utilizzare il comando e il mittente non è uno.
	 */
	SOLO
	/**
	 * L'esecutore non ha dato un giocatore, come lo stesso
	 * {@link CommandResult#MISSING_ARGUMENT} ma più specifico
	 */
	RICHIESTO,
	/**
	 * Si è verificato un errore sconosciuto
	 */
	ERRORE;

	public String getMessage() {
		switch (this) {
			case INVALID_ARGUMENT:
				restituisce MSG.getString("Command.InvalidArgument", "&cÈ stato fornito un argomento non valido.");
			caso MISSING_ARGUMENT:
				return MSG.getString("Command.MissingArgument", "&cTi manca un argomento.");
			caso NO_PERMISSIONE:
				restituisce MSG.getString("Command.NoPermission",
						"&4&l[&c&lNOPE&4&l] &cTi manca &a%perm% &cpermission.");
			caso PLAYER_ONLY:
				restituisce MSG.getString("Command.PlayerOnly",
						"&cDevi specificare un giocatore per eseguire questo comando come console.");
			caso PLAYER_REQUIRED:
				restituisci MSG.getString("Command.SpecifyPlayer", "&cDevi specificare un giocatore come argomento.");
			caso SUCCESSO:
				ritorna "";
			predefinito:
				rompere;
		}
		return "&4Si è verificato un errore durante l'esecuzione del comando.";
	}
}
