package xyz.msws.nope.modules.checks;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.AbstractModule;

public class TPSManager extends AbstractModule {

	public TPSManager(NOPE plugin) {
		super(plugin);
	}

	private long delay = 0;

	private BukkitTask task;

	private float tps;
	private float amo;
	private long lastCheck;

	private BukkitTask checker;

	@Override
	public void enable() {
		task = new BukkitRunnable() {
			@Override
			public void run() {
				if (delay > 0)
					try {
						Thread.sleep(delay);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
			}
		}.runTaskTimer(plugin, 0, 1);

		tps = 20.0f;
		amo = 0.0f;

		int checkEvery = 5000;
		lastCheck = System.currentTimeMillis();

		checker = new BukkitRunnable() {
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

	public void setDelay(long delay) {
		this.delay = delay;
	}

	public long getDelay() {
		return delay;
	}

	@Override
	public void disable() {
		if (task != null && !task.isCancelled())
			task.cancel();
		if (checker != null && !checker.isCancelled())
			checker.cancel();
	}

	public float getTPS() {
		return tps;
	}

}
