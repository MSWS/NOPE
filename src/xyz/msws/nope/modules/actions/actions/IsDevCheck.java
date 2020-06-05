package xyz.msws.nope.modules.actions.actions;

import org.bukkit.OfflinePlayer;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.actions.AbstractConditionalAction;
import xyz.msws.nope.modules.checks.Check;

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
		return plugin.getOption("dev").asBoolean();
	}

}
