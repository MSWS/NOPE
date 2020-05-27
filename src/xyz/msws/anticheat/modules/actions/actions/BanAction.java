package xyz.msws.anticheat.modules.actions.actions;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.modules.actions.AbstractAction;
import xyz.msws.anticheat.modules.checks.Check;
import xyz.msws.anticheat.modules.data.CPlayer;
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

	private boolean running;

	public BanAction(NOPE plugin, long time, String reason) {
		super(plugin);
		this.reason = reason;
		this.time = time;
	}

	@Override
	public void execute(OfflinePlayer player, Check check) {
		CPlayer cp = plugin.getCPlayer(player);
		if (running)
			return;
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			running = true;
			String token = cp.saveLog(check);
			String res = MSG.replaceCheckPlaceholder(reason, cp, check).replace("%token%", token);
			cp.clearVls();

			plugin.getStats().addBan();
			plugin.getBanwave().removePlayer(player.getUniqueId());

			Bukkit.getScheduler().runTask(plugin, () -> {
				plugin.getBanManager().ban(player.getUniqueId(), res, time);
				running = false;
			});
		});
	}

}
