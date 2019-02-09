package org.mswsplex.anticheat.checks.combat;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
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

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onEntityDamgedByEntity(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player))
			return;
		Player player = (Player) event.getDamager();
		CPlayer cp = plugin.getCPlayer(player);

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
			return;
		}

		if (cp.timeSince("lastKillAuraCheck") < 10000)
			return;

		Random rnd = new Random();

		if (rnd.nextDouble() < .20)
			return;

		if (event.getEntity().hasMetadata("killAuraMark"))
			return;

		Location mid = event.getEntity().getLocation().add(player.getLocation()).multiply(.5).add(0, .5, 0);

		ArmorStand stand = (ArmorStand) player.getWorld().spawnEntity(mid, EntityType.ARMOR_STAND);

		stand.setGravity(false);
		stand.setVisible(false);

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

			if (cp.hasTempData("hasHitTarget"))
				cp.flagHack(this, 50);
			cp.removeTempData("hasHitTarget");
		}, 100);

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
				Location m = target.getLocation().add(player.getLocation()).multiply(.5).add(0, .5, 0);
				stand.teleport(m);
			}
		};
	}
}
