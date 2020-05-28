package xyz.msws.anticheat.commands;

public enum CommandResult {
	SUCCESS, NO_PERMISSION, MISSING_ARGUMENT, INVALID_ARGUMENT, ERROR;

	public String getMessage() {
		switch (this) {
			case INVALID_ARGUMENT:
				return "An invalid argument was provided.";
			case MISSING_ARGUMENT:
				return "You are missing a parameter";
			case NO_PERMISSION:
				return "You do not have the sufficient permissions required to run that command.";
			case SUCCESS:
				return "";
			default:
				break;
		}
		return "An error occured whilst executing the command.";
	}
}
