package xyz.msws.nope.modules;

import xyz.msws.nope.NOPE;

/**
 * Represents a module for NOPE. Modules should cleanup any tasks, listeners,
 * schedules, etc. in the {@link AbstractModule#disable()} method.
 * 
 * @author imodm
 *
 */
public abstract class AbstractModule {
	protected NOPE plugin;

	public AbstractModule(NOPE plugin) {
		this.plugin = plugin;
	}

	public abstract void enable();

	public abstract void disable();

}
