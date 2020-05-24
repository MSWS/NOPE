package xyz.msws.anticheat.actions.actions;

import org.bukkit.OfflinePlayer;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.actions.AbstractAction;
import xyz.msws.anticheat.checks.Check;
import xyz.msws.anticheat.utils.MSG;

/**
 * Kicks the player with the specified reason. Generally should be used with
 * delay.
 * 
 * @author imodm
 *
 */
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
