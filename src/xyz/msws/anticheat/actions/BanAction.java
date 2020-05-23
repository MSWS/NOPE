package xyz.msws.anticheat.actions;

import org.bukkit.OfflinePlayer;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.checks.Check;
import xyz.msws.anticheat.checks.Timing;
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
		String token = MSG.genUUID(16);
		CPlayer cp = plugin.getCPlayer(player);
		String res = MSG.replaceCheckPlaceholder(reason, cp, check).replace("%token%", token);

		cp.saveLog(check.getCategory(), Timing.INSTANT, token);
		plugin.getStats().addBan();
		plugin.getBanwave().removePlayer(player.getUniqueId());

		plugin.getBanManager().ban(player.getUniqueId(), res, time);
	}

}
