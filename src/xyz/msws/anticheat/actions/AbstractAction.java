package xyz.msws.anticheat.actions;

import org.bukkit.OfflinePlayer;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.checks.Check;
/**
 * AbstractAction is the parent class for all actions that can be taken against a player when they flag
 * 
 * Actions must be manually 
 * 
 * @author imodm
 *
 */
public abstract class AbstractAction {

	protected NOPE plugin;

	public AbstractAction(NOPE plugin) {
		this.plugin = plugin;
	}

	public abstract void execute(OfflinePlayer player, Check check);
}
