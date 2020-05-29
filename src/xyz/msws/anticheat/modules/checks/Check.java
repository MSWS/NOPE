package xyz.msws.anticheat.modules.checks;

import javax.naming.OperationNotSupportedException;

import xyz.msws.anticheat.NOPE;

/**
 * This should be an abstract class to support automated event reigstration and
 * plugin encapsulation ProtocolCheck should be an additional abstract class
 * that also extends Check
 * 
 * @author imodm
 *
 */
public interface Check {
	public CheckType getType();

	public void register(NOPE plugin) throws OperationNotSupportedException;

	public String getCategory();

	public String getDebugName();

	/**
	 * @deprecated This is no longer used and instead should be replaced with a void
	 *             cancel method I'm not sure how to implement this so it will
	 *             remain like this for now.
	 * @return
	 */
	boolean lagBack();
}