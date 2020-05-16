package org.mswsplex.anticheat.checks.movement;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.mswsplex.anticheat.checks.Check;
import org.mswsplex.anticheat.checks.CheckType;
import org.mswsplex.anticheat.data.CPlayer;
import org.mswsplex.anticheat.msws.NOPE;

/**
 * Checks if a player moves completely horizontally without being on the ground
 * 
 * @author imodm
 *
 */
public class Spider1 implements Check, Listener {

	private NOPE plugin;

	@Override
	public CheckType getType() {
		return CheckType.MOVEMENT;
	}

	@Override
	public void register(NOPE plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);

		Location to = event.getTo();
		Location from = event.getFrom();

		if (player.isOnGround() || player.isFlying())
			return;

		if (cp.isBlockNearby(Material.COBWEB))
			return;

		if (cp.hasMovementRelatedPotion())
			return;

		if (cp.timeSince("wasFlying") < 200)
			return;

		if (cp.timeSince("lastDamageTaken") < 500)
			return;

		if (cp.isInClimbingBlock())
			return;

		if (to.getY() < from.getY())
			return;

		if (cp.timeSince("lastOnGround") < 1000)
			return;

		if (cp.timeSince("lastLiquid") < 500)
			return;

		if (cp.timeSince("lastBlockPlace") < 1000)
			return;

		if (cp.timeSince("lastInClimbing") < 500)
			return;

		if (player.isInsideVehicle() && player.getVehicle().getType() == EntityType.HORSE)
			return;

		if (player.getLocation().getBlock().isLiquid())
			return;

		double dist = cp.distanceToGround() - 1 + (player.getLocation().getY() % 1);

		double max = 1.2522033402537218;

		if (dist <= max)
			return;

		if ((dist + "").contains("999999999")) {
			cp.flagHack(this, 30, "Dist: " + dist);
		} else {
			cp.flagHack(this, 20, "Dist: " + dist);
		}
	}

	@Override
	public String getCategory() {
		return "Spider";
	}

	@Override
	public String getDebugName() {
		return getCategory() + "#1";
	}

	@Override
	public boolean lagBack() {
		return true;
	}
}
