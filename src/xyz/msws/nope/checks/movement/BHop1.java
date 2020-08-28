package xyz.msws.nope.checks.movement;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.checks.Check;
import xyz.msws.nope.modules.checks.CheckType;
import xyz.msws.nope.modules.checks.Global.Stat;
import xyz.msws.nope.modules.data.CPlayer;

/**
 * Checks distances of where the player last touched the ground
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

	private Map<UUID, Location> lastGround = new HashMap<>();

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

		if (player.isFlying() || player.isGliding() || player.isRiptiding() || cp.hasMovementRelatedPotion()) {
			lastGround.remove(player.getUniqueId());
			return;
		}

		if (player.getLocation().getBlock().isLiquid()) {
			lastGround.remove(player.getUniqueId());
			return;
		}

		if (!player.isOnGround())
			return;

		if (cp.hasMovementRelatedPotion()) {
			lastGround.remove(player.getUniqueId());
			return;
		}

		if (cp.timeSince(Stat.ON_SLIMEBLOCK) < 2000) {
			lastGround.remove(player.getUniqueId());
			return;
		}

		if (cp.timeSince(Stat.BLOCK_PLACE) < 1000) {
			lastGround.remove(player.getUniqueId());
			return;
		}

		if (cp.timeSince(Stat.DAMAGE_TAKEN) < 1000) {
			lastGround.remove(player.getUniqueId());
			return;
		}

		if (cp.timeSince(Stat.CLIMBING) < 1000) {
			lastGround.remove(player.getUniqueId());
			return;
		}

		if (cp.timeSince(Stat.ON_ICE) < 1000) {
			lastGround.remove(player.getUniqueId());
			return;
		}

		if (cp.isInClimbingBlock()) {
			lastGround.remove(player.getUniqueId());
			return;
		}

		Location to = event.getTo();

		if (!lastGround.containsKey(player.getUniqueId())) {
			lastGround.put(player.getUniqueId(), player.getLocation());
			return;
		}

		Location last = lastGround.get(player.getUniqueId());
		lastGround.put(player.getUniqueId(), player.getLocation());
		if (last.getY() > to.getY())
			return;
		double dist = last.distanceSquared(to);
		if (dist <= 21.07331544322134)
			return;
		cp.flagHack(this, (int) (dist - 21) * 5, "Dist: &e" + dist);

	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		Player player = event.getPlayer();
		lastGround.remove(player.getUniqueId());
	}

	@EventHandler
	public void onDeath(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		lastGround.remove(player.getUniqueId());
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
