package xyz.msws.anticheat.actions;

import org.bukkit.OfflinePlayer;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.checks.Check;
import xyz.msws.anticheat.utils.MSG;

public class KickAction extends AbstractAction {

	private String reason;

	public KickAction(NOPE plugin, String reason) {
		super(plugin);
		this.reason = reason;
	}

	@Override
	public void execute(OfflinePlayer player, Check check) {
		if (!player.isOnline())
			return;
		player.getPlayer().kickPlayer(MSG.color(MSG.replaceCheckPlaceholder(reason, plugin.getCPlayer(player), check)));
	}

}
