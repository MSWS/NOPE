package xyz.msws.nope.modules.checks;

import javax.naming.OperationNotSupportedException;

import xyz.msws.nope.NOPE;

/**
 * This should be an abstract class to support automated event registration and
 * plugin encapsulation. ProtocolCheck should be an additional abstract class
 * that also extends Check. Keyword being should.
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
	 * @deprecated This is no longer used.
	 * @return
	 */
	boolean lagBack();

	/**
	 * This should be used to unregister events or schedules.
	 */
	public default void disable() {
	}
}