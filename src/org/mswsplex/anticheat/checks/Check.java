package org.mswsplex.anticheat.checks;

import javax.naming.OperationNotSupportedException;

import org.mswsplex.anticheat.msws.NOPE;

public interface Check {
	public CheckType getType();

	public void register(NOPE plugin) throws OperationNotSupportedException;

	public String getCategory();

	public String getDebugName();

	public boolean lagBack();
}