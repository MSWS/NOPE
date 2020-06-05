package xyz.msws.nope.modules.compatability;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.AbstractModule;

/**
 * Responsible for preventing false flags with specific plugins.
 * 
 * @author imodm
 *
 */
public abstract class AbstractHook extends AbstractModule implements Listener {

	public AbstractHook(NOPE plugin) {
		super(plugin);
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@Override
	public void enable() {
	}

	@Override
	public void disable() {
	}

	public abstract String getName();
}
