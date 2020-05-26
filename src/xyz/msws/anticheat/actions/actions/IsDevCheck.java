package xyz.msws.anticheat.actions.actions;

import org.bukkit.OfflinePlayer;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.actions.AbstractConditionalAction;
import xyz.msws.anticheat.checks.Check;

/**
 * An {@link AbstractConditionalAction} that returns true if the plugin is in
 * dev mode. Useful for testing.
 * 
 * @author imodm
 *
 */
public class IsDevCheck extends AbstractConditionalAction {

	public IsDevCheck(NOPE plugin) {
		super(plugin);
	}

	@Override
	public boolean getValue(OfflinePlayer player, Check check) {
		return plugin.devMode();
	}

}
