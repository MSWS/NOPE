Paket xyz.msws.nope.commands;

importieren xyz.msws.nope.utils.MSG;

/**
 * Repräsentiert das Ergebnis eines Befehls.
 * 
 * @author imodm
 *
 */
public enum CommandResult {
	/**
	 * Der Befehl wurde erfolgreich abgeschlossen. Eine benutzerdefinierte Erfolgsnachricht sollte sein
	 * gesendet.
	 */
	AKTUELLE,
	/**
	 * Der Absender hat nicht die nötigen Berechtigungen für den Befehl.
	 */
	NO_PERMISSION,
	/**
	 * Ein Argument fehlt.
	 */
	FISSING_ARGUMENT,
	/**
	 * Ein ungültiges Argument wird angegeben.
	 */
	Ungültig
	/**
	 * Nur ein Spieler kann den Befehl verwenden und der Absender ist nicht eins.
	 */
	Nur Spieler,
	/**
	 * Der ausführende Benutzer hat keinen Spieler gegeben, genauso wie
	 * {@link CommandResult#MISSING_ARGUMENT} aber spezifischer
	 */
	SPIELER_REQUILE,
	/**
	 * Ein unbekannter Fehler ist aufgetreten
	 */
	FEHLER;

	öffentliche String getMessage() {
		wechseln (dies) {
			Fall INVALID_ARGUMENT:
				return MSG.getString("Command.InvalidArgument", "&cEin ungültiges Argument wurde angegeben.");
			falle MISSING_ARGUMENT:
				return MSG.getString("Command.MissingArgument", "&cIhnen fehlt ein Argument.");
			case NO_PERMISSION:
				return MSG.getString("Command.NoPermission",
						"&4&l[&c&lNOPE&4&l] &cIhnen fehlt die &a%perm% &cpermission.");
			case PLAYER_ONLY:
				gibt MSG.getString("Nur Befehl.Player",
						"&cDu musst einen Spieler angeben, um diesen Befehl als Konsole auszuführen.");
			case PLAYER_REQUIRED:
				return MSG.getString("Command.SpecifyPlayer", "&cDu musst einen Spieler als Argument angeben.");
			fall-ERKLÄRUNGEN:
				zurückgeben "";
			default:
				bruch;
		}
		return "&4Ein Fehler ist beim Ausführen des Befehls aufgetreten.";
	}
}
