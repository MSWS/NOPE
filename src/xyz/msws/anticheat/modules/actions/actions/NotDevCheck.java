package xyz.msws.anticheat.modules.actions.actions;

import org.bukkit.OfflinePlayer;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.modules.actions.AbstractConditionalAction;
import xyz.msws.anticheat.modules.checks.Check;

/**
 * An {@link AbstractConditionalAction} that returns false if the plugin is in
 * dev mode. Useful for testing and not wanting to get banned everytime.
 * 
 * @author imodm
 *
 */
public class NotDevCheck extends AbstractConditionalAction {

	public NotDevCheck(NOPE plugin) {
		super(plugin);
	}

	@Override
	public boolean getValue(OfflinePlayer player, Check check) {
		return !plugin.getOption("dev").asBoolean();
	}

}
