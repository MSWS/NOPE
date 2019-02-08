package org.mswsplex.anticheat.checks;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.mswsplex.anticheat.data.CPlayer;
import org.mswsplex.anticheat.msws.AntiCheat;

public class Global implements Listener {
	private AntiCheat plugin;

	public Global(AntiCheat plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);

		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {

			for (Player p : Bukkit.getOnlinePlayers()) {
				CPlayer cp = plugin.getCPlayer(p);
				ConfigurationSection vlSection = cp.getDataFile().getConfigurationSection("vls");
				if (vlSection == null)
					continue;
				for (String hack : vlSection.getKeys(false)) {
					cp.setSaveData("vls." + hack, cp.getSaveInteger("vls." + hack) - 5);
					if (cp.getSaveInteger("vls." + hack) < 0) {
						cp.setSaveData("vls." + hack, 0);
					}
				}
			}
		}, 0, 100);
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);

		boolean onGround = cp.isOnGround(), weirdBlock = cp.isInWeirdBlock(), climbing = cp.isInClimbingBlock();

		Location from = event.getFrom(), to = event.getTo();

		if (to.getBlock().isLiquid() || from.getBlock().isLiquid())
			cp.setTempData("lastLiquid", (double) System.currentTimeMillis());

		if (from.getY() != to.getY())
			cp.setTempData("lastYChange", (double) System.currentTimeMillis());

		if (onGround) {
			cp.setTempData("lastOnGround", (double) System.currentTimeMillis());
			if (!weirdBlock && player.getLocation().subtract(0, .1, 0).getBlock().getType().isSolid()) {
				cp.setLastSafeLocation(player.getLocation());
			}
		} else {
			cp.setTempData("lastInAir", (double) System.currentTimeMillis());
		}

		if (climbing)
			cp.setTempData("lastInClimbing", (double) System.currentTimeMillis());

		if (weirdBlock)
			cp.setTempData("lastWeirdBlock", (double) System.currentTimeMillis());

		if (player.isInsideVehicle())
			cp.setTempData("lastVehicle", (double) System.currentTimeMillis());

		if (player.isFlying())
			cp.setTempData("wasFlying", (double) System.currentTimeMillis());

		Location vertLine = player.getLocation();
		while (!vertLine.getBlock().getType().isSolid() && vertLine.getY() > 0) {
			vertLine.subtract(0, 1, 0);
		}

		Block lowestBlock = vertLine.getBlock();

		if (lowestBlock.getType() == Material.SLIME_BLOCK)
			cp.setTempData("lastSlimeBlock", (double) System.currentTimeMillis());
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);
		cp.setTempData("lastBlockPlace", (double) System.currentTimeMillis());
	}

	@EventHandler
	public void onToggleFlight(PlayerToggleFlightEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);

		cp.setTempData("toggleFlight", (double) System.currentTimeMillis());

		if (player.isFlying()) {
			cp.setTempData("disableFlight", (double) System.currentTimeMillis());
		} else {
			cp.setTempData("enableFlight", (double) System.currentTimeMillis());
		}
	}

	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);

		cp.setTempData("lastTeleport", (double) System.currentTimeMillis());
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		Entity ent = event.getEntity();
		if (!(ent instanceof Player))
			return;
		Player player = (Player) ent;
		CPlayer cp = plugin.getCPlayer(player);
		cp.setTempData("lastDamageTaken", (double) System.currentTimeMillis());
	}
}
