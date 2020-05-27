package xyz.msws.anticheat.modules;

import xyz.msws.anticheat.NOPE;

public abstract class AbstractModule {
	protected NOPE plugin;

	public AbstractModule(NOPE plugin) {
		this.plugin = plugin;
	}

	public abstract void enable();

	public abstract void disable();

}
