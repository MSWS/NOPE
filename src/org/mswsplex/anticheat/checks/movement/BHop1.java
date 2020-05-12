package org.mswsplex.anticheat.checks.movement;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.mswsplex.anticheat.checks.Check;
import org.mswsplex.anticheat.checks.CheckType;
import org.mswsplex.anticheat.data.CPlayer;
import org.mswsplex.anticheat.msws.NOPE;
import org.mswsplex.anticheat.utils.MSG;

/**
 * Basically {@link NoFall} but when the player hits an entity
 * 
 * @author imodm
 *
 */
public class BHop1 implements Check, Listener {

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

		if (player.isFlying() || player.isGliding()) {
			cp.removeTempData("lastGroundLocation");
			return;
		}

		if (!player.isOnGround())
			return;

		if (cp.hasMovementRelatedPotion()) {
			cp.removeTempData("lastGroundLocation");
			return;
		}

		if (cp.timeSince("lastSlimeBlock") < 2000) {
			cp.removeTempData("lastGroundLocation");
			return;
		}

		if (cp.timeSince("lastDamageTaken") < 500) {
			cp.removeTempData("lastGroundLocation");
			return;
		}

		if (cp.timeSince("lastOnIce") < 1000) {
			cp.removeTempData("lastGroundLocation");
			return;
		}

		if (cp.isInClimbingBlock()) {
			cp.removeTempData("lastGroundLocation");
			return;
		}

		Location to = event.getTo();

		if (!cp.hasTempData("lastGroundLocation")) {
			cp.setTempData("lastGroundLocation", player.getLocation());
			return;
		}

		if (cp.hasTempData("lastGroundLocation")) {
			Location last = (Location) cp.getTempData("lastGroundLocation");
			cp.setTempData("lastGroundLocation", player.getLocation());
			if (last.getY() > to.getY())
				return;
			double dist = last.distanceSquared(to);
			if (dist <= 21.07331544322134)
				return;
			MSG.tell(player, dist + "");
			cp.flagHack(this, (int) (dist - 21) * 5, "Dist: &e" + dist);
		}
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);
		cp.removeTempData("lastGroundLocation");
	}

	@Override
	public String getCategory() {
		return "BHop";
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
