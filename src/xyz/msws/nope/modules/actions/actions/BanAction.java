package xyz.msws.nope.modules.actions.actions;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.actions.AbstractAction;
import xyz.msws.nope.modules.bans.AbstractBanHook;
import xyz.msws.nope.modules.bans.Banwave;
import xyz.msws.nope.modules.checks.Check;
import xyz.msws.nope.modules.data.CPlayer;
import xyz.msws.nope.modules.data.Stats;
import xyz.msws.nope.utils.MSG;

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

			plugin.getModule(Stats.class).addBan();
			plugin.getModule(Banwave.class).removePlayer(player.getUniqueId());

			Bukkit.getScheduler().runTask(plugin, () -> {
				plugin.getModule(AbstractBanHook.class).ban(player.getUniqueId(), res, time);
				running = false;
			});
		});
	}

}
