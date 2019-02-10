package org.mswsplex.anticheat.checks;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.mswsplex.anticheat.msws.AntiCheat;

public class TPSChecker {

	private float tps;
	private float amo;
	private long lastCheck;

	public TPSChecker(AntiCheat plugin) {

		tps = 20.0f;
		amo = 0.0f;

		int checkEvery = 1000;
		lastCheck = System.currentTimeMillis();
		Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin) plugin, () -> {
			if (System.currentTimeMillis() - lastCheck > checkEvery) {
				tps = (float) (amo / (checkEvery / 1000.0));
				amo = 0.0f;
				lastCheck = System.currentTimeMillis();
			}
			amo++;
		}, 0L, 1L);
	}

	public float getTPS() {
		return this.tps;
	}
}
