package xyz.msws.anticheat.modules.actions.actions;

import org.bukkit.OfflinePlayer;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.modules.actions.AbstractAction;
import xyz.msws.anticheat.modules.actions.ActionGroup;
import xyz.msws.anticheat.modules.actions.ActionManager;
import xyz.msws.anticheat.modules.checks.Check;

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
		group = plugin.getModule(ActionManager.class).getActions(data).get(0);
	}

	@Override
	public void execute(OfflinePlayer player, Check check) {
		group.activate(player, check);
	}

}
