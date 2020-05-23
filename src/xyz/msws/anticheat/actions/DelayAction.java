package xyz.msws.anticheat.actions;

import org.bukkit.OfflinePlayer;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.checks.Check;

public class DelayAction extends AbstractConditionalAction {

	private long delay, lastActivated;

	public DelayAction(NOPE plugin, long delay) {
		super(plugin);
		this.delay = delay;
		this.lastActivated = 0;
	}

	@Override
	public void execute(OfflinePlayer player, Check check) {
		super.execute(player, check);
		lastActivated = System.currentTimeMillis();
	}

	@Override
	public boolean getValue(OfflinePlayer player, Check check) {
		return System.currentTimeMillis() - lastActivated > delay;
	}

}
