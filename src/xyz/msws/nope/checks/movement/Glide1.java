package xyz.msws.nope.checks.movement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
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
 * Checks if a player's fall velocity is less than the player's previous fall
 * velocity
 * 
 * @author imodm
 *
 */
public class Glide1 implements Check, Listener {

	private final int SIZE = 10;

	private NOPE plugin;

	@Override
	public CheckType getType() {
		return CheckType.MOVEMENT;
	}

	private Map<UUID, List<Double>> fallDistances = new HashMap<>();
	private Map<UUID, Double> lastFall = new HashMap<>();

	@Override
	public void register(NOPE plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);

		if (player.isOnGround() || player.isFlying()) {
			fallDistances.remove(player.getUniqueId());
			return;
		}

		if (cp.isInClimbingBlock() || cp.isInWeirdBlock() || player.isFlying() || player.isOnGround())
			return;

		if (cp.hasMovementRelatedPotion())
			return;

		if (player.isSwimming())
			return;

		if (cp.timeSince(Stat.FLYING) < 2000 || cp.timeSince(Stat.RIPTIDE) < 2000)
			return;

		if (cp.timeSince(Stat.ON_GROUND) < 500 || cp.timeSince(Stat.FLIGHT_GROUNDED) < 500)
			return;
		if (cp.timeSince(Stat.COBWEB) < 500)
			return;
		if (cp.timeSince(Stat.ON_SLIMEBLOCK) < 2000)
			return;
		if (cp.timeSince(Stat.IN_LIQUID) < 1000)
			return;
		if (cp.timeSince(Stat.DAMAGE_TAKEN) < 2000)
			return;
		if (cp.timeSince(Stat.SHULKER) < 1000)
			return;

		double fallDist = event.getFrom().getY() - event.getTo().getY();

		if (fallDist == 0) {
			lastFall.remove(player.getUniqueId());
			return;
		}

		if (!lastFall.containsKey(player.getUniqueId())) {
			lastFall.put(player.getUniqueId(), fallDist);
			return;
		}

		double previousFall = lastFall.get(player.getUniqueId());
		double diff = fallDist - previousFall;

		List<Double> fs = fallDistances.getOrDefault(player.getUniqueId(), new ArrayList<>());

		fs.add(0, diff);

		fs = fs.subList(0, Math.min(fs.size(), SIZE));

		fallDistances.put(player.getUniqueId(), fs);

		if (fs.size() < SIZE)
			return;

		double avg = 0;
		for (double d : fs)
			avg += d;

		avg /= fs.size();

		if (avg > 0)
			return;

		cp.flagHack(this, (int) ((2 - avg) * 50) + 10, "&7Avg: &e" + avg);
	}

	@Override
	public String getCategory() {
		return "Glide";
	}

	@Override
	public String getDebugName() {
		return "Glide#1";
	}

	@Override
	public boolean lagBack() {
		return true;
	}
}
