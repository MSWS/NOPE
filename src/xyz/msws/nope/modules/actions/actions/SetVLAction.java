package xyz.msws.nope.modules.actions.actions;

import org.bukkit.OfflinePlayer;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.actions.AbstractAction;
import xyz.msws.nope.modules.checks.Check;
import xyz.msws.nope.modules.data.CPlayer;

public class SetVLAction extends AbstractAction {

	private int diff;

	public SetVLAction(NOPE plugin, int diff) {
		super(plugin);
		this.diff = diff;
	}

	@Override
	public void execute(OfflinePlayer player, Check check) {
		CPlayer cp = plugin.getCPlayer(player);
		cp.setVL(check.getCategory(), diff);
	}

}
