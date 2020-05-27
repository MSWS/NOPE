package xyz.msws.anticheat.checks.movement.flight;

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
 * Same as @see Flight1 but different onGround detection
 * 
 * @author imodm
 *
 */
public class Flight2 implements Check, Listener {

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

		if (player.isFlying() || cp.isInWeirdBlock() || player.isInsideVehicle())
			return;

		if (cp.timeSince(Stat.FLYING) < 500)
			return;

		Location to = event.getTo(), from = event.getFrom();

		if (to.getY() != from.getY())
			return;

		if (player.getNearbyEntities(2, 3, 2).stream().anyMatch(e -> e.getType() == EntityType.BOAT))
			return;

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
				if (player.getLocation().clone().add(x, 0, z).getBlock().getType() != Material.AIR) {
					isBlockNearby = true;
					break;
				}
			}
		}

		if (isBlockNearby) {
			return;
		}

		if (cp.timeSince(Stat.FLIGHT_GROUNDED) < 1000)
			return;

		cp.flagHack(this, 20);
	}

	@Override
	public String getCategory() {
		return "Flight";
	}

	@Override
	public String getDebugName() {
		return "Flight#2";
	}

	@Override
	public boolean lagBack() {
		return true;
	}
}
