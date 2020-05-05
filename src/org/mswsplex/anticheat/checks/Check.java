package org.mswsplex.anticheat.checks;

import org.mswsplex.anticheat.msws.NOPE;

public interface Check {
	public CheckType getType();

	public void register(NOPE plugin);

	public String getCategory();

	public String getDebugName();

	public boolean lagBack();
}