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
import org.bukkit.event.player.PlayerJoinEvent;
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

				double lastFlag = cp.timeSince("lastFlag");

				int diff = 1;

				if (lastFlag > 1.8e+6) {
					diff = 20;
				} else if (lastFlag > 600000) {
					diff = 10;
				} else if (lastFlag > 300000) {
					diff = 5;
				} else if (lastFlag > 50000) {
					diff = 3;
				} else if (lastFlag > 10000) {
					diff = 2;
				}

				for (String hack : vlSection.getKeys(false)) {
					cp.setSaveData("vls." + hack, cp.getSaveInteger("vls." + hack) - diff);
					if (cp.getSaveInteger("vls." + hack) < 0)
						cp.setSaveData("vls." + hack, 0);
				}
			}
		}, 0, 40);
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);

		double time = System.currentTimeMillis();
		boolean onGround = cp.isOnGround(), weirdBlock = cp.isInWeirdBlock(), climbing = cp.isInClimbingBlock();

		cp.setTempData("lastMove", (double) time);

		Location from = event.getFrom(), to = event.getTo();

		if (to.getBlock().isLiquid() || from.getBlock().isLiquid())
			cp.setTempData("lastLiquid", (double) time);

		if (from.getY() != to.getY())
			cp.setTempData("lastYChange", (double) time);

		if (onGround) {
			cp.setTempData("lastOnGround", (double) time);
			if (!weirdBlock && player.getLocation().subtract(0, .1, 0).getBlock().getType().isSolid()) {
				cp.setLastSafeLocation(player.getLocation());
			}
		} else {
			cp.setTempData("lastInAir", (double) time);
		}

		if (player.getLocation().subtract(0, .01, 0).getBlock().getType().toString().contains("TRAP")
				&& cp.isBlockAbove()) {
			cp.setTempData("iceAndTrapdoor", (double) System.currentTimeMillis());
		}

		boolean isBlockNearby = false;
		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				if (player.getLocation().clone().add(x, -.1, z).getBlock().getType().isSolid()) {
					isBlockNearby = true;
					break;
				}
				if (player.getLocation().clone().add(x, -1.5, z).getBlock().getType().isSolid()) {
					isBlockNearby = true;
					break;
				}
			}
		}

		if (isBlockNearby) {
			cp.setTempData("lastFlightGrounded", (double) time);
		}

		if (climbing)
			cp.setTempData("lastInClimbing", (double) time);

		if (weirdBlock)
			cp.setTempData("lastWeirdBlock", (double) time);

		if (player.isInsideVehicle())
			cp.setTempData("lastVehicle", (double) time);

		if (player.isFlying())
			cp.setTempData("wasFlying", (double) time);

		if (from.getBlockX() != to.getBlockX() || from.getBlockZ() != to.getBlockZ())
			cp.setTempData("lastHorizontalBlockChange", (double) System.currentTimeMillis());

		Location vertLine = player.getLocation().clone();
		while (!vertLine.getBlock().getType().isSolid() && vertLine.getY() > 0) {
			vertLine.subtract(0, 1, 0);
		}

		Block lowestBlock = vertLine.getBlock();

		if (lowestBlock.getType() == Material.SLIME_BLOCK)
			cp.setTempData("lastSlimeBlock", (double) time);

		if (cp.isRedstoneNearby())
			cp.setTempData("lastNearbyRedstone", (double) time);
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

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);
		cp.setLastSafeLocation(player.getLocation());
		cp.setTempData("joinTime", (double) System.currentTimeMillis());
	}
}
