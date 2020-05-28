package xyz.msws.anticheat.modules.checks;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.modules.AbstractModule;

public class TPSChecker extends AbstractModule {

	private float tps;
	private float amo;
	private long lastCheck;

	private BukkitTask task;

	public TPSChecker(NOPE plugin) {
		super(plugin);
	}

	public float getTPS() {
		return this.tps;
	}

	@Override
	public void enable() {
		tps = 20.0f;
		amo = 0.0f;

		int checkEvery = 5000;
		lastCheck = System.currentTimeMillis();

		task = new BukkitRunnable() {
			@Override
			public void run() {
				if (System.currentTimeMillis() - lastCheck > checkEvery) {
					tps = (float) (amo / (checkEvery / 1000.0));
					amo = 0.0f;
					lastCheck = System.currentTimeMillis();
				}
				amo++;
			}
		}.runTaskTimer(plugin, 0, 1);
	}

	@Override
	public void disable() {
		if (task != null && !task.isCancelled())
			task.cancel();
	}
}
