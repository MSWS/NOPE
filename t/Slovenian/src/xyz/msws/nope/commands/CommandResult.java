package xyz.msws.nope.commands;

import xyz.msws.nope.utils.MSG;

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
				return MSG.getString("Command.InvalidArgument", "&cAn invalid argument was provided.");
			case MISSING_ARGUMENT:
				return MSG.getString("Command.MissingArgument", "&cYou are missing an argument.");
			case NO_PERMISSION:
				return MSG.getString("Command.NoPermission",
						"&4&l[&c&lNOPE&4&l] &cYou lack the &a%perm% &cpermission.");
			case PLAYER_ONLY:
				return MSG.getString("Command.PlayerOnly",
						"&cYou must specify a player to run this command as console.");
			case PLAYER_REQUIRED:
				return MSG.getString("Command.SpecifyPlayer", "&cYou must specify a player as an argument.");
			case SUCCESS:
				return "";
			default:
				break;
		}
		return "&4An error occured whilst executing the command.";
	}
}
