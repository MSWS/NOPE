package xyz.msws.nope.listeners;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.events.actions.ActionExecuteEvent;
import xyz.msws.nope.modules.AbstractModule;
import xyz.msws.nope.modules.actions.actions.BanAction;

/**
 * Listens to any action execution and checks to see if a ban was issued.
 * 
 * @author imodm
 *
 */
public class TokenCreationListener extends AbstractModule implements Listener {

	public TokenCreationListener(NOPE plugin) {
		super(plugin);
	}

	private List<String> tokens = new ArrayList<>();
	private File logs;

	@Override
	public void enable() {
		logs = new File(plugin.getDataFolder(), "logs");
		if (logs == null || !logs.exists() || logs.list() == null)
			return;
		for (String f : logs.list()) {
			tokens.add(f.substring(0, f.length() - 4));
		}

		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@Override
	public void disable() {
	}

	public List<String> getTokens() {
		return tokens;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onAction(ActionExecuteEvent event) {
		if (!event.getAction().getClass().equals(BanAction.class))
			return;
		if (logs == null || !logs.exists() || logs.list() == null)
			return;
		new BukkitRunnable() {
			@Override
			public void run() {
				tokens.clear();
				for (String f : logs.list()) {
					tokens.add(f.substring(0, f.length() - 4));
				}
			}
		}.runTaskLaterAsynchronously(plugin, 20);
	}

}
