package xyz.msws.anticheat.actions;

import org.bukkit.OfflinePlayer;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.checks.Check;

public abstract class AbstractConditionalAction extends AbstractAction {

	public AbstractConditionalAction(NOPE plugin) {
		super(plugin);
	}

	@Override
	public void execute(OfflinePlayer player, Check check) {
		return;
	}

	public abstract boolean getValue(OfflinePlayer player, Check check);

}
