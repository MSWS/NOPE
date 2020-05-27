package xyz.msws.anticheat.modules.checks;

public enum CheckType {
	COMBAT("Hacks related to combat"), MOVEMENT("Hacks that affect server movement"),
	RENDER("Hacks that affect either client or server renders"), PLAYER("Hacks that affect only the player"),
	WORLD("Hacks that affect the world or blocks"), TICK("Hacks related with packets/ticks"),
	EXPLOIT("Hacks that exploit the server"), MISC("Miscellaneous");

	private String description;

	private CheckType(String desc) {
		description = desc;
	}

	public String getDescription() {
		return description;
	}
}
