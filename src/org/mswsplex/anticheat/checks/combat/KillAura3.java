package org.mswsplex.anticheat.checks.combat;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.mswsplex.anticheat.checks.Check;
import org.mswsplex.anticheat.checks.CheckType;
import org.mswsplex.anticheat.data.CPlayer;
import org.mswsplex.anticheat.msws.NOPE;

/**
 * Creates a mini armorstand right behind an entity that the player has hit,
 * this stand SHOULD NOT be hit (legimiately)
 * 
 * @author imodm
 * 
 * @deprecated
 *
 */
public class KillAura3 implements Check, Listener {

	private NOPE plugin;

	private HashMap<Player, Entity> stands;

	@Override
	public CheckType getType() {
		return CheckType.COMBAT;
	}

	@Override
	public void register(NOPE plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;

		stands = new HashMap<>();
	}

	private final double CHECK_EVERY = 10000;
	private final int TICKS_TO_WAIT = 100;

	@EventHandler
	public void onEntityDamgedByEntity(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player))
			return;
		Player player = (Player) event.getDamager();
		CPlayer cp = plugin.getCPlayer(player);

		if (event.getEntity().hasMetadata("antiKillAuraMark")) {
			if (!event.getEntity().getMetadata("antiKillAuraMark").get(0).asString()
					.equals(player.getUniqueId().toString())) {
				event.setCancelled(true);
			} else {
				cp.flagHack(this, 20);
				cp.setTempData("lastKillAuraCheck1", (double) System.currentTimeMillis() - CHECK_EVERY);
				stands.remove(player);
				event.getEntity().remove();
			}
		}

		if (stands.containsKey(player))
			return;

		if (cp.timeSince("lastKillAuraCheck1") < CHECK_EVERY)
			return;

		ThreadLocalRandom rnd = ThreadLocalRandom.current();

		if (rnd.nextDouble() < .20)
			return;

		if (event.getEntity().hasMetadata("antiKillAuraMark"))
			return;

		Location mid = event.getEntity().getLocation();

		ArmorStand stand = (ArmorStand) player.getWorld().spawnEntity(mid, EntityType.ARMOR_STAND);

		stand.setGravity(false);
		stand.setVisible(false);
		stand.setSmall(true);
		stand.setBasePlate(false);

		stand.setMetadata("antiKillAuraMark", new FixedMetadataValue(plugin, player.getUniqueId().toString()));

		stands.put(player, stand);

		BukkitTask task = new BukkitRunnable() {
			@Override
			public void run() {
				teleportArmorStand(player, stand, event.getEntity());
			}
		}.runTaskTimer(plugin, 0, 1);

		new BukkitRunnable() {

			@Override
			public void run() {
				task.cancel();
				if (stands.get(player) == null) {
					stands.remove(player);
					return;
				}
				stands.get(player).remove();
				stands.remove(player);
			}
		}.runTaskLater(plugin, TICKS_TO_WAIT);
		/*
		 * Bukkit.getScheduler().runTaskLater(plugin, () -> {
		 * Bukkit.getScheduler().cancelTask(id);
		 * 
		 * if (stands.get(player) == null) { stands.remove(player); return; }
		 * stands.get(player).remove(); stands.remove(player); }, TICKS_TO_WAIT);
		 */
		cp.setTempData("lastKillAuraCheck1", (double) System.currentTimeMillis());
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);

		cp.setTempData("lastKillAuraCheck1", (double) System.currentTimeMillis() - 20000);
	}

	@Override
	public String getCategory() {
		return "KillAura";
	}

	@Override
	public String getDebugName() {
		return "KillAura#3";
	}

	@Override
	public boolean lagBack() {
		return false;
	}

	public BukkitRunnable teleportArmorStand(Player player, Entity stand, Entity target) {
		return new BukkitRunnable() {
			@Override
			public void run() {
				if (target == null || target.isDead() || !target.isValid() || (stand.getNearbyEntities(5, 5, 5).stream()
						.filter((entity) -> entity instanceof Projectile).collect(Collectors.toList()).size() > 0))
					stand.remove();

				Location m = target.getLocation();

				Vector pToT = player.getLocation().clone().toVector().subtract(target.getLocation().clone().toVector());

				m.subtract(pToT.multiply(.4));

				stand.teleport(m);
			}
		};
	}
}
