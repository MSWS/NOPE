package xyz.msws.anticheat.modules.checks;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.modules.AbstractModule;

public class TPSManager extends AbstractModule {

	public TPSManager(NOPE plugin) {
		super(plugin);
	}

	private long delay = 0;

	private BukkitTask task;

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
	}

}
