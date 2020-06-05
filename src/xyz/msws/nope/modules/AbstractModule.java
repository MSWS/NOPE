package xyz.msws.nope.modules;

import xyz.msws.nope.NOPE;

public abstract class AbstractModule {
	protected NOPE plugin;

	public AbstractModule(NOPE plugin) {
		this.plugin = plugin;
	}

	public abstract void enable();

	public abstract void disable();

}
