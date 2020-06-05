package xyz.msws.nope.commands;

public enum CommandResult {
	SUCCESS, NO_PERMISSION, MISSING_ARGUMENT, INVALID_ARGUMENT, PLAYER_ONLY, PLAYER_REQUIRED, ERROR;

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
