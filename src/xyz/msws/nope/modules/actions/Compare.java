package xyz.msws.nope.modules.actions;

/**
 * Used for easy comparisons used by {@link AbstractConditionalAction}
 * 
 * @author imodm
 *
 */
public enum Compare {
	LESS_THAN("<"), LESS_THAN_EQUALS("<="), EQUALS("="), NOT_EQUALS("!="), GREATER_THAN(">"), GREATER_THAN_EQUALS(">=");

	private String symbol;

	Compare(String symbol) {
		this.symbol = symbol;
	}

	public static Compare fromString(String s) {
		for (Compare t : Compare.values())
			if (t.getSymbol().equals(s))
				return t;
		return NOT_EQUALS;
	}

	public String getSymbol() {
		return this.symbol;
	}

	public boolean check(int a, int b) {
		switch (this) {
			case LESS_THAN:
				return a < b;
			case LESS_THAN_EQUALS:
				return a <= b;
			case EQUALS:
				return a == b;
			case NOT_EQUALS:
				return a != b;
			case GREATER_THAN:
				return a > b;
			case GREATER_THAN_EQUALS:
				return a >= b;
		}
		return false;
	}

}
