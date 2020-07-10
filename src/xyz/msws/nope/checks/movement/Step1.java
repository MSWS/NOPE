package xyz.msws.nope.checks.movement;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.UUID;

import javax.naming.OperationNotSupportedException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.checks.Check;
import xyz.msws.nope.modules.checks.CheckType;
import xyz.msws.nope.modules.checks.Global.Stat;
import xyz.msws.nope.modules.data.CPlayer;

/**
 * Compares vertical distances and checks if they're too high
 * 
 * @author imodm
 *
 */
public class Step1 implements Check, Listener {

	@Override
	public CheckType getType() {
		return CheckType.MOVEMENT;
	}

	@SuppressWarnings("unused")
	private NOPE plugin;

	private Map<UUID, Double> lastY = new HashMap<>();

	private Map<UUID, TreeMap<Double, Integer>> yVals = new HashMap<>();

	@Override
	public void register(NOPE plugin) throws OperationNotSupportedException {
		this.plugin = plugin;

		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
			for (Player player : Bukkit.getOnlinePlayers()) {
				Location loc = player.getLocation();
				CPlayer cp = plugin.getCPlayer(player);

				if (cp.hasMovementRelatedPotion() || cp.timeSince(Stat.MOVEMENT_POTION) < 200) {
					yVals.put(player.getUniqueId(), new TreeMap<>());
					continue;
				}
				if (cp.timeSince(Stat.DAMAGE_TAKEN) < 1000)
					continue;
				if (cp.timeSince(Stat.TELEPORT) < 1000)
					yVals.put(player.getUniqueId(), new TreeMap<>());
				if (player.isFlying() || player.isGliding())
					continue;
				if (cp.timeSince(Stat.FLYING) < 2000) {
					yVals.put(player.getUniqueId(), new TreeMap<>());
					continue;
				}
				if (cp.timeSince(Stat.RESPAWN) < 5000) {
					yVals.put(player.getUniqueId(), new TreeMap<>());
					continue;
				}
				if (cp.timeSince(Stat.REDSTONE) < 500)
					continue;
				if (player.isRiptiding())
					continue;
				double diff = loc.getY() - lastY.getOrDefault(player.getUniqueId(), loc.getY());
				lastY.put(player.getUniqueId(), loc.getY());

				if (diff == 0)
					continue;

				TreeMap<Double, Integer> vals = yVals.getOrDefault(player.getUniqueId(), new TreeMap<>());
				vals.put(diff, vals.getOrDefault(diff, 0) + 1);
				yVals.put(player.getUniqueId(), vals);
			}
		}, 0, 1);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
			for (Player player : Bukkit.getOnlinePlayers()) {
				CPlayer cp = plugin.getCPlayer(player);
				if (cp.timeSince(Stat.MOVEMENT_POTION) < 3000)
					continue;
				if (cp.timeSince(Stat.FLYING) < 10000)
					continue;
				if (cp.timeSince(Stat.RESPAWN) < 5000)
					continue;
				if (cp.timeSince(Stat.TELEPORT) < 2000)
					continue;
				if (cp.timeSince(Stat.RIPTIDE) < 1000)
					continue;
				double avg = 0;
				int amo = 0;
				TreeMap<Double, Integer> vals = yVals.getOrDefault(player.getUniqueId(), new TreeMap<>());

				for (Entry<Double, Integer> entry : vals.entrySet()) {
					avg += entry.getValue() * entry.getKey();
					amo += entry.getValue();
				}

				avg /= amo;
				if (avg == 0 || amo == 0)
					continue;
				if (avg < 1)
					continue;
				cp.flagHack(this, Math.min((int) ((avg * 10) + 5 * amo), 40), "Avg: &e" + avg + "\n&7Size: &e" + amo);
			}
			yVals.clear();
		}, 0, 20);
	}

	@Override
	public String getCategory() {
		return "Step";
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
