package xyz.msws.anticheat.modules.animations;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.protocols.WrapperPlayServerExplosion;

public class GWENAnimation extends AbstractAnimation {

	public GWENAnimation(NOPE plugin, Player player) {
		super(plugin, player);
	}

	private BukkitTask task;

	private Guardian[] guardians = new Guardian[4];
	private Laser[] lasers = new Laser[4];

	private long maxTime = 5000;

	@Override
	public boolean start() {
		super.start();
		this.startTime = System.currentTimeMillis();
		if (player == null || !player.isValid())
			return false;

		for (int i = 0; i < guardians.length; i++) {
			guardians[i] = (Guardian) player.getWorld().spawnEntity(player.getLocation(), EntityType.GUARDIAN);
			try {
				lasers[i] = new Laser(guardians[i].getLocation(), player.getLocation(), -1, 64);
				lasers[i].start(plugin);
			} catch (ReflectiveOperationException e) {
				e.printStackTrace();
			}
			guardians[i].setAI(false);
		}

		Location origin = player.getLocation().clone();

		task = new BukkitRunnable() {

			long ticks = 0;

			@Override
			public void run() {

				long diff = System.currentTimeMillis() - startTime;
				float speed = 2 + (float) ((double) diff / (double) maxTime) * 5;
				float radius = (float) (3 + Math.abs(Math.cos(diff / 1000.0)) * 2.0);
				if (player.getLocation().getBlockX() != origin.getBlockX()
						|| player.getLocation().getBlockZ() != origin.getBlockZ()) {
					Location l = origin.clone();
					l.setY(player.getLocation().getY());
					l.setPitch(player.getLocation().getPitch());
					l.setYaw(player.getLocation().getYaw());
					player.teleport(l);
				}

				for (int i = 0; i < guardians.length; i++) {
					double xOffset = Math.cos(i * Math.PI / 2 + (diff / 1000.0 * speed)) * radius;
					double zOffset = Math.sin(i * Math.PI / 2 + (diff / 1000.0 * speed)) * radius;
					Location loc = player.getLocation().clone();
					loc.add(xOffset, 5, zOffset);
					Guardian guard = guardians[i];
					guard.teleport(loc);

					try {
						lasers[i].moveEnd(player.getLocation());
						lasers[i].moveStart(guard.getLocation());
					} catch (ReflectiveOperationException e) {
						e.printStackTrace();
					}
				}

				if (ticks % 40 == 0)
					player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GUARDIAN_ATTACK, 1, 1);

				if (System.currentTimeMillis() - startTime > maxTime)
					stop(true);
				ticks++;
			}
		}.runTaskTimer(plugin, 0, 1);
		return true;
	}

	@Override
	public void stop(boolean manual) {
		for (Guardian g : guardians)
			g.remove();
		for (Laser laser : lasers) {
			if (laser.isStarted())
				laser.stop();
		}

		WrapperPlayServerExplosion ex = new WrapperPlayServerExplosion();
		ex.setPlayerVelocityX(0);
		ex.setPlayerVelocityY(0);
		ex.setPlayerVelocityZ(0);
		ex.setX(player.getLocation().getX());
		ex.setY(player.getLocation().getY());
		ex.setZ(player.getLocation().getZ());
		ex.setRadius(5);
		ex.broadcastPacket();
		player.stopSound(Sound.ENTITY_GUARDIAN_ATTACK);
		task.cancel();
	}

	@Override
	public boolean completed() {
		return task.isCancelled();
	}

	@Override
	public String getName() {
		return null;
	}

}
