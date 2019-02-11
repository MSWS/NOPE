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
import org.mswsplex.anticheat.checks.Check;
import org.mswsplex.anticheat.checks.CheckType;
import org.mswsplex.anticheat.data.CPlayer;
import org.mswsplex.anticheat.msws.AntiCheat;

/**
 * Creates an armorstand IN FRONT of the player, this stand SHOULD be hit if it
 * isn't then flag
 * 
 * @author imodm
 *
 */
public class KillAura1 implements Check, Listener {

	private AntiCheat plugin;

	private HashMap<Player, Entity> stands;

	@Override
	public CheckType getType() {
		return CheckType.COMBAT;
	}

	@Override
	public void register(AntiCheat plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;

		stands = new HashMap<>();
	}

	private final double CHECK_EVERY = 20000;
	private final int TICKS_TO_WAIT = 100;

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onEntityDamgedByEntity(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player))
			return;
		Player player = (Player) event.getDamager();
		CPlayer cp = plugin.getCPlayer(player);

		if (event.getEntity().hasMetadata("killAuraMark")) {
			if (!event.getEntity().getMetadata("killAuraMark").get(0).asString()
					.equals(player.getUniqueId().toString())) {
				event.setCancelled(true);
			}
		}

		if (stands.containsKey(player)) {
			if (!player.hasMetadata("lastEntityHit"))
				return;
			if (!player.getMetadata("lastEntityHit").get(0).asString()
					.equals(event.getEntity().getUniqueId().toString())) {
				stands.get(player).remove();
				stands.remove(player);
				return;
			}
			cp.setTempData("hasHitTarget", true);
			cp.flagHack(this, 5);
			return;
		}

		if (cp.timeSince("lastKillAuraCheck") < CHECK_EVERY)
			return;

		ThreadLocalRandom rnd = ThreadLocalRandom.current();

		if (rnd.nextDouble() < .60)
			return;

		if (event.getEntity().hasMetadata("killAuraMark"))
			return;

		Location mid = event.getEntity().getLocation().add(player.getLocation()).multiply(.5).add(0, .5, 0);

		ArmorStand stand = (ArmorStand) player.getWorld().spawnEntity(mid, EntityType.ARMOR_STAND);

		stand.setGravity(false);
		stand.setVisible(false);
		stand.setBasePlate(false);

		stand.setMetadata("killAuraMark", new FixedMetadataValue(plugin, player.getUniqueId().toString()));
		player.setMetadata("lastEntityHit", new FixedMetadataValue(plugin, event.getEntity().getUniqueId().toString()));

		stands.put(player, stand);

		int id = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin,
				teleportArmorStand(player, stand, event.getEntity()), 0, 1);

		Bukkit.getScheduler().runTaskLater(plugin, () -> {
			Bukkit.getScheduler().cancelTask(id);
			player.removeMetadata("lastEntityHit", plugin);

			if (stands.get(player) == null) {
				stands.remove(player);
				return;
			}

			stands.get(player).remove();
			stands.remove(player);

			if (cp.hasTempData("hasHitTarget")) {
				cp.flagHack(this, 50);
				cp.setTempData("lastKillAuraCheck", (double) System.currentTimeMillis() - CHECK_EVERY);
			}
			cp.removeTempData("hasHitTarget");
		}, TICKS_TO_WAIT);

		cp.setTempData("lastKillAuraCheck", (double) System.currentTimeMillis());
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);

		cp.setTempData("lastKillAuraCheck", (double) System.currentTimeMillis() - 20000);
	}

	@Override
	public String getCategory() {
		return "KillAura";
	}

	@Override
	public String getDebugName() {
		return "KillAura#1";
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

				if (target.getWorld() != player.getWorld()) {
					stand.remove();
					return;
				}

				Location m = target.getLocation().add(player.getLocation()).multiply(.5).add(0, .5, 0);
				stand.teleport(m);
			}
		};
	}
}
