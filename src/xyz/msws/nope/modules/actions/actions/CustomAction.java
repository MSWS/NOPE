package xyz.msws.nope.modules.actions.actions;

import org.bukkit.OfflinePlayer;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.actions.AbstractAction;
import xyz.msws.nope.modules.actions.ActionGroup;
import xyz.msws.nope.modules.actions.ActionManager;
import xyz.msws.nope.modules.checks.Check;

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
