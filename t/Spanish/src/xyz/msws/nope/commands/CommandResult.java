paquete xyz.msws.nope.commands;

importar xyz.msws.nope.utils.MSG;

/**
 * Representa el resultado de un comando.
 * 
 * imodm @author
 *
 */
public enum CommandResult {
	/**
	 * El comando se ha completado correctamente. Un mensaje de éxito personalizado debe ser
	 * enviado.
	 */
	ESPECIAL,
	/**
	 * El remitente no tiene los permisos adecuados para el comando.
	 */
	No hay perforación
	/**
	 * Falta un argumento.
	 */
	ARGUMENT
	/**
	 * Se da un argumento inválido.
	 */
	ArgUMENT
	/**
	 * Sólo un jugador puede usar el comando y el remitente no lo es.
	 */
	SÓLO JUGADOR
	/**
	 * El ejecutor no le dio a un jugador, igual que
	 * {@link CommandResult#MISSING_ARGUMENT} pero más específico
	 */
	PLAYER_REQUERIDO,
	/**
	 * Se ha producido un error desconocido
	 */
	ERROR;

	public String getMessage() {
		cambiar (este) {
			ARGUMENT del caso:
				return MSG.getString("Command.InvalidArgument", "&cSe proporcionó un argumento no válido.");
			MISSING_ARGUMENT:
				return MSG.getString("Command.MissingArgument", "&cTe falta un argumento.");
			SI_PERMISIÓN:
				return MSG.getString("Command.NoPermission",
						"&4&l[&c&lNOPE&4&l] &cTe falta &a%perm% &cpermission.");
			sólo para minúsculas:
				return MSG.getString("Command.PlayerOnly",
						"&cDebes especificar un jugador para ejecutar este comando como consola.");
			PLAYER_REQUERIDO:
				return MSG.getString("Command.SpecifyPlayer", "&cYou must specify a player as an argument.");
			caso SUCCESS:
				devolver "";
			por defecto:
				romper;
		}
		return "&4Ha ocurrido un error mientras se ejecutaba el comando.";
	}
}
