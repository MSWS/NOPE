package org.mswsplex.anticheat.checks;

import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;
import org.mswsplex.anticheat.data.CPlayer;
import org.mswsplex.anticheat.msws.NOPE;

public class Banwave {

	private NOPE plugin;

	private long lastBanwave;

	public Banwave(NOPE plugin) {
		this.plugin = plugin;
		runBanwave(false).runTaskTimer(this.plugin, 0, plugin.config.getInt("BanwaveRate"));
	}

	public BukkitRunnable runBanwave(boolean forced) {
		return new BukkitRunnable() {
			@Override
			public void run() {
				for (OfflinePlayer player : plugin.getPlayerManager().getLoadedPlayers()) {
					CPlayer cp = plugin.getCPlayer(player);
					if (!cp.hasSaveData("isBanwaved"))
						continue;
					cp.ban(cp.getSaveString("isBanwaved"), forced ? Timing.MANUAL_BANWAVE : Timing.BANWAVE);
					cp.removeSaveData("isBanwaved");
					cp.saveData();
				}
				if (!forced)
					lastBanwave = System.currentTimeMillis();
			}
		};
	}

	public long timeToNextBanwave() {
		return plugin.config.getInt("BanwaveRate") * 50 + lastBanwave - System.currentTimeMillis();
	}
}
