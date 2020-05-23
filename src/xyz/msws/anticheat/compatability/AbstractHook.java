package xyz.msws.anticheat.compatability;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import xyz.msws.anticheat.NOPE;

public abstract class AbstractHook implements Listener {

	protected NOPE plugin;

	public AbstractHook(NOPE plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, this.plugin);
	}

	public abstract String getName();
}
