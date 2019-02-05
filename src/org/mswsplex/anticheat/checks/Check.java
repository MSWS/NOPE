package org.mswsplex.anticheat.checks;

import org.mswsplex.anticheat.msws.AntiCheat;

public interface Check {
	public CheckType getType();

	public void register(AntiCheat plugin);
	
	public String getCategory();
	
	public String getDebugName();
}