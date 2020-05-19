package xyz.msws.anticheat.checks;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import xyz.msws.anticheat.NOPE;

public class TPSChecker {

	private float tps;
	private float amo;
	private long lastCheck;

	public TPSChecker(NOPE plugin) {

		tps = 20.0f;
		amo = 0.0f;

		int checkEvery = 5000;
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
