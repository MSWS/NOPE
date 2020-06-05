package xyz.msws.nope.commands;

/**
 * Represents the result of a command.
 * 
 * @author imodm
 *
 */
public enum CommandResult {
	/**
	 * The command was completed successfully. A custom success message should be
	 * sent.
	 */
	SUCCESS,
	/**
	 * The sender does not have the proper permissions for the command.
	 */
	NO_PERMISSION,
	/**
	 * An argument is missing.
	 */
	MISSING_ARGUMENT,
	/**
	 * An invalid argument is given.
	 */
	INVALID_ARGUMENT,
	/**
	 * Only a player can use the command and the sender is not one.
	 */
	PLAYER_ONLY,
	/**
	 * The executor did not give a player, same as
	 * {@link CommandResult#MISSING_ARGUMENT} but more specific
	 */
	PLAYER_REQUIRED,
	/**
	 * An unknown error occured
	 */
	ERROR;

	public String getMessage() {
		switch (this) {
			case INVALID_ARGUMENT:
				return "&cAn invalid argument was provided.";
			case MISSING_ARGUMENT:
				return "&cYou are missing a parameter.";
			case NO_PERMISSION:
				return "&cYou do not have the sufficient permissions required to run that command.";
			case PLAYER_ONLY:
				return "&cYou must be a player to run this command.";
			case PLAYER_REQUIRED:
				return "&cYou must specify a player.";
			case SUCCESS:
				return "";
			default:
				break;
		}
		return "&4An error occured whilst executing the command.";
	}
}
