package xyz.msws.anticheat.actions.actions;

import org.bukkit.OfflinePlayer;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.actions.AbstractAction;
import xyz.msws.anticheat.checks.Check;
import xyz.msws.anticheat.data.CPlayer;
import xyz.msws.anticheat.utils.MSG;

/**
 * Bans, saves logs, and increments statistics
 * 
 * @author imodm
 *
 */
public class BanAction extends AbstractAction {

	private String reason;
	private long time;

	public BanAction(NOPE plugin, long time, String reason) {
		super(plugin);
		this.reason = reason;
		this.time = time;
	}

	@Override
	public void execute(OfflinePlayer player, Check check) {
		CPlayer cp = plugin.getCPlayer(player);
		String token = cp.saveLog(check);
		String res = MSG.replaceCheckPlaceholder(reason, cp, check).replace("%token%", token);
		cp.clearVls();

		plugin.getStats().addBan();
		plugin.getBanwave().removePlayer(player.getUniqueId());

		plugin.getBanManager().ban(player.getUniqueId(), res, time);
	}

}
