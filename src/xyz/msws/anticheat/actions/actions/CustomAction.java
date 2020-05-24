package xyz.msws.anticheat.actions.actions;

import org.bukkit.OfflinePlayer;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.actions.AbstractAction;
import xyz.msws.anticheat.actions.ActionGroup;
import xyz.msws.anticheat.checks.Check;

/**
 * Used to signify a custom action that the config has set, this is extremely
 * useful
 * 
 * @author imodm
 *
 */
public class CustomAction extends AbstractAction {

	private ActionGroup group;

	public CustomAction(NOPE plugin, String data) {
		super(plugin);
		group = new ActionGroup();
		group = plugin.getActionManager().getActions(data).get(0);
	}

	@Override
	public void execute(OfflinePlayer player, Check check) {
		group.activate(player, check);
	}

}
