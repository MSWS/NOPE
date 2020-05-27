package xyz.msws.anticheat.modules.compatability;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.modules.AbstractModule;

public abstract class AbstractHook extends AbstractModule implements Listener {

	protected NOPE plugin;

	public AbstractHook(NOPE plugin) {
		super(plugin);
		Bukkit.getPluginManager().registerEvents(this, this.plugin);
	}

	@Override
	public void enable() {
	}

	@Override
	public void disable() {
	}

	public abstract String getName();
}
