package xyz.msws.nope.checks.movement.flight;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.checks.Check;
import xyz.msws.nope.modules.checks.CheckType;
import xyz.msws.nope.modules.checks.Global.Stat;
import xyz.msws.nope.modules.data.CPlayer;

/**
 * 
 * Checks if the player's last ongrond position is too low and too far away
 * <i>conveniently</i> also checks Jesus
 * 
 * @author imodm
 * 
 */
public class Flight4 implements Check, Listener {

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

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);

		if (cp.hasMovementRelatedPotion() || cp.timeSince(Stat.MOVEMENT_POTION) < 3000)
			return;

		if (cp.isBlockNearby(Material.SCAFFOLDING, 4, -2))
			return;

		if (cp.timeSince(Stat.COBWEB) < 500)
			return;

		if (player.isInsideVehicle())
			return;

		if (player.isFlying() || cp.timeSince(Stat.FLYING) < 5000 || player.isOnGround()
				|| cp.timeSince(Stat.FLIGHT_GROUNDED) < 500 || cp.timeSince(Stat.RIPTIDE) < 5000)
			return;

		if (cp.timeSince(Stat.TELEPORT) < 4000)
			return;

		if (cp.timeSince(Stat.CLIMBING) < 4000)
			return;

		if (cp.timeSince(Stat.IN_LIQUID) < 500)
			return;

		if (cp.timeSince(Stat.ON_GROUND) < 1000)
			return;

		if (cp.timeSince(Stat.REDSTONE) < 1000)
			return;

		if (cp.timeSince(Stat.IN_VEHICLE) < 1000)
			return;

		if (cp.timeSince(Stat.DAMAGE_TAKEN) < 2000)
			return;

		if (player.getLocation().getBlock().isLiquid())
			return;

		if (player.getNearbyEntities(2, 3, 2).stream().anyMatch(e -> e.getType() == EntityType.BOAT))
			return;

		Location safe = cp.getLastSafeLocation();
		if (safe == null)
			return;
		if (!safe.getWorld().equals(player.getWorld()))
			return;

		if (!safe.getWorld().equals(player.getLocation().getWorld()))
			return;

		double yDiff = safe.getY() - player.getLocation().getY();

		if (yDiff >= 0)
			return;

		double dist = safe.distanceSquared(player.getLocation());

		if (dist < 10)
			return;

		cp.flagHack(this, Math.max(Math.min((int) Math.round((dist - 10) * 10.0), 50), 10),
				"Dist: &e" + dist + "&7 >= &a10\n&7YDiff: &e" + yDiff + "&7 < 0");
	}

	@Override
	public String getCategory() {
		return "Flight";
	}

	@Override
	public String getDebugName() {
		return "Flight#4";
	}

	@Override
	public boolean lagBack() {
		return true;
	}
}
