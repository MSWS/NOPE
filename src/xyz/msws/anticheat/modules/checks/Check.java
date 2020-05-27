package xyz.msws.anticheat.modules.checks;

import javax.naming.OperationNotSupportedException;

import xyz.msws.anticheat.NOPE;

public interface Check {
	public CheckType getType();

	public void register(NOPE plugin) throws OperationNotSupportedException;

	public String getCategory();

	public String getDebugName();

	/**
	 * @deprecated
	 * @return
	 */
	boolean lagBack();
}