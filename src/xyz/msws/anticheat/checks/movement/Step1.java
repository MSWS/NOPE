package xyz.msws.anticheat.checks.movement;

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

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.modules.checks.Check;
import xyz.msws.anticheat.modules.checks.CheckType;
import xyz.msws.anticheat.modules.checks.Global.Stat;
import xyz.msws.anticheat.modules.data.CPlayer;

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

				if (cp.hasMovementRelatedPotion())
					continue;
				if (cp.timeSince(Stat.DAMAGE_TAKEN) < 1000 || cp.timeSince(Stat.TELEPORT) < 2000)
					continue;
				if (player.isFlying() || player.isGliding())
					continue;
				if (cp.timeSince(Stat.FLYING) < 500)
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
				CPlayer cp = plugin.getCPlayer(player);
				cp.flagHack(this, (int) ((avg * 15) + 5 * amo), "Avg: &e" + avg + "\n&7Size: &e" + amo);
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
