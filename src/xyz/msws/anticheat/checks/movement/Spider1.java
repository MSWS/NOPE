package xyz.msws.anticheat.checks.movement;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.modules.checks.Check;
import xyz.msws.anticheat.modules.checks.CheckType;
import xyz.msws.anticheat.modules.checks.Global.Stat;
import xyz.msws.anticheat.modules.data.CPlayer;

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

		if (cp.isBlockNearby(Material.COBWEB) || cp.isBlockNearby(Material.SCAFFOLDING))
			return;

		if (cp.hasMovementRelatedPotion())
			return;

		if (cp.timeSince(Stat.FLYING) < 2000)
			return;

		if (cp.timeSince(Stat.DAMAGE_TAKEN) < 500)
			return;

		if (cp.isInClimbingBlock())
			return;

		if (to.getY() < from.getY())
			return;

		if (cp.timeSince(Stat.ON_GROUND) < 1000)
			return;

		if (cp.timeSince(Stat.IN_LIQUID) < 1000)
			return;

		if (cp.timeSince(Stat.BLOCK_PLACE) < 1000)
			return;

		if (cp.timeSince(Stat.CLIMBING) < 500)
			return;

		if (player.isInsideVehicle() && player.getVehicle().getType() == EntityType.HORSE)
			return;

		if (player.getLocation().getBlock().isLiquid())
			return;

		if (player.getNearbyEntities(1, 3, 1).stream().anyMatch(e -> e.getType() == EntityType.BOAT))
			return;

		double dist = cp.distanceToGround() - 1 + (player.getLocation().getY() % 1);

		double max = 1.2595999415636072;
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
