package xyz.msws.nope.checks.movement.flight;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.checks.Check;
import xyz.msws.nope.modules.checks.CheckType;
import xyz.msws.nope.modules.data.CPlayer;

/**
 * 
 * Checks vehicle flight
 * 
 * @author imodm
 * 
 */
public class Flight6 implements Check, Listener {

	private NOPE plugin;

	@Override
	public CheckType getType() {
		return CheckType.MOVEMENT;
	}

	private Map<UUID, Long> vehicleGround = new HashMap<>();

	@Override
	public void register(NOPE plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);

		if (!player.isInsideVehicle())
			return;

		Entity vehicle = player.getVehicle();
		if (vehicle.isOnGround()) {
			vehicleGround.put(player.getUniqueId(), System.currentTimeMillis());
			return;
		}
		if (player.getLocation().getBlock().isLiquid())
			return;
		double yDiff = event.getTo().getY() - event.getFrom().getY();

		if (yDiff < 0) {
			if (System.currentTimeMillis()
					- vehicleGround.getOrDefault(player.getUniqueId(), System.currentTimeMillis()) < 1000)
				return;
			if (yDiff < -.1)
				return;
			cp.flagHack(this, 10);
			return;
		}

		if (player.getLocation().clone().add(0, -1, 0).getBlock().isLiquid())
			return;

		if (cp.distanceToGround() < 5)
			return;

		cp.flagHack(this, 20);
	}

	@Override
	public String getCategory() {
		return "Flight";
	}

	@Override
	public String getDebugName() {
		return getCategory() + "#6";
	}

	@Override
	public boolean lagBack() {
		return false;
	}
}
