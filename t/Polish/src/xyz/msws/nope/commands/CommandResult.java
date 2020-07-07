Pakiet xyz.msws.nope.commands;

import xyz.msws.nope.utils.MSG;

/**
 * Reprezentuje wynik polecenia.
 * 
 * @author imodm
 *
 */
publiczna komenda wyników {
	/**
	 * Polecenie zostało zakończone pomyślnie. Niestandardowa wiadomość o sukcesie powinna być
	 * wysłano.
	 */
	SĄDY,
	/**
	 * Nadawca nie ma odpowiednich uprawnień dla polecenia.
	 */
	NO_PERMISSIONE,
	/**
	 * Brakuje argumentu.
	 */
	ARGENT_MISSING,
	/**
	 * Podano nieprawidłowy argument.
	 */
	Niepoprawna wartość,
	/**
	 * Tylko gracz może używać polecenia, a nadawca nie jest żadnym użytkownikiem.
	 */
	TYLKO
	/**
	 * Wykonawca nie podał gracza, tak samo jak
	 * {@link CommandResult#MISSING_ARGUMENT} ale bardziej szczegółowe
	 */
	PLAYER
	/**
	 * Wystąpił nieznany błąd
	 */
	BŁĄD;

	publiczny ciąg getMessage() {
		przełącznik (to) {
			Niepoprawna_ARGUMENT:
				Zwróć MSG.getString("Command.InvalidArgument", "&cAn invalid argument został dostarczony");
			MISSING_ARGUMENT:
				Zwróć MSG.getString("Command.MissingArgument", "&cBrakuje argumentu");
			Sprawa NO_PERMISSION:
				zwróć MSG.getString("Command.NoPermission",
						"&4&l[&c&lNOPE&4&l] &cBrakuje &%perm% &cpermission.");
			SETT_PLAYLIST_LABEL
				zwróć MSG.getString("Command.Only",
						"&cMusisz określić gracza, aby uruchomić tę komendę jako konsolę.");
			PLAYER_REQUIRED:
				Zwróć MSG.getString("Command.SpecifyPlayer", "&cMusisz określić gracza jako argument.");
			przypadek SUCCES:
				wróć "";
			domyślnie:
				przerwa;
		}
		wróć "&4Wystąpił błąd podczas wykonywania polecenia.”;
	}
}
