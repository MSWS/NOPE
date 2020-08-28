package xyz.msws.nope.modules.animations;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Evoker;
import org.bukkit.entity.EvokerFangs;
import org.bukkit.entity.Player;
import org.bukkit.entity.Spellcaster.Spell;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.checks.Check;

/**
 * NOPE's custom Ban animation, may or may not change in the future.
 * 
 * @author imodm
 *
 */
public class NOPEAnimation extends AbstractAnimation implements Listener {

	public NOPEAnimation(NOPE plugin, Player player, Check check) {
		super(plugin, player, check);

		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	private BukkitTask task;
	private long maxTime = 7000;

	private Evoker[] evokers = new Evoker[8];

	public void start() {
		if (player == null)
			return;

		player.setGameMode(GameMode.SURVIVAL);
		Location origin = player.getLocation().clone();

		double radius = 5;

		for (int i = 0; i < evokers.length; i++) {
			Location l = origin.clone();
			double xOffset = Math.cos(i * Math.PI / evokers.length * 2) * radius;
			double zOffset = Math.sin(i * Math.PI / evokers.length * 2) * radius;

			l.add(xOffset, 0, zOffset);

			Vector dir = player.getLocation().toVector().subtract(l.toVector());
			l.setDirection(dir);

			Evoker e = (Evoker) player.getWorld().spawnEntity(l, EntityType.EVOKER);
			
			evokers[i] = e;

			e.setAI(false);
			e.setSilent(true);
			e.setInvulnerable(true);
			e.setMetadata("isAnimation", new FixedMetadataValue(plugin, true));
		}

		task = new BukkitRunnable() {
			long damageDelay = 10;
			long damageCounter = 0;

			@Override
			public void run() {
				long diff = System.currentTimeMillis() - startTime;

				double perc = (double) diff / (double) maxTime;

				if (player.getLocation().getBlockX() != origin.getBlockX()
						|| player.getLocation().getBlockZ() != origin.getBlockZ()) {
					Location l = origin.clone();
					l.setY(player.getLocation().getY());
					l.setPitch(player.getLocation().getPitch());
					l.setYaw(player.getLocation().getYaw());
					player.teleport(l);
				}

				if (damageCounter >= damageDelay) {
					damageCounter = 0;
					if (ThreadLocalRandom.current().nextDouble() > .5)
						damageDelay -= 1;

					for (Evoker e : evokers) {
						e.setSpell(Spell.FANGS);
						e.attack(player);

						Location l = e.getLocation().clone();

						l.subtract(e.getLocation().toVector().subtract(player.getLocation().toVector())
								.multiply(perc - .1));

						EvokerFangs fangs = (EvokerFangs) player.getWorld().spawnEntity(l, EntityType.EVOKER_FANGS);
						fangs.setMetadata("isAnimation", new FixedMetadataValue(plugin, true));
					}
					player.setHealth(20);
					player.setNoDamageTicks(0);
					player.setVelocity(new Vector());
				}
				damageCounter++;

				if (System.currentTimeMillis() - startTime > maxTime)
					stop();
			}
		}.runTaskTimer(plugin, 0, 1);

		return;
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		if (event.getEntity().hasMetadata("isAnimation"))
			event.setCancelled(true);
		if (event.getDamager().hasMetadata("isAnimation"))
			event.setCancelled(true);
	}

	@Override
	public void stop() {
		for (World w : Bukkit.getWorlds()) {
			for (Entity ent : w.getEntities()) {
				if (ent.hasMetadata("isAnimation"))
					ent.remove();
			}
		}
		if (evokers != null)
			for (Evoker e : evokers)
				if (e != null && e.isValid())
					e.remove();
		if (task != null && !task.isCancelled())
			task.cancel();
		if (action != null)
			action.activate(player, check);
	}

	@Override
	public boolean completed() {
		return task.isCancelled();
	}

	@Override
	public String getName() {
		return "NOPE";
	}

}
