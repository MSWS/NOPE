package xyz.msws.nope.modules.trust;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import xyz.msws.nope.events.player.PlayerFlagEvent;

public class VLRating implements TrustRating, Listener {
	private Map<UUID, List<Long>> times = new HashMap<>();

	public VLRating(JavaPlugin plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onFlag(PlayerFlagEvent event) {
		UUID uuid = event.getCPlayer().getUUID();
		List<Long> t = times.getOrDefault(uuid, new ArrayList<>());
		t.add(System.currentTimeMillis());
		times.put(uuid, t);
	}

	@Override
	public double getTrust(UUID uuid) {
		if (!times.containsKey(uuid))
			return 1;

		List<Long> t = times.getOrDefault(uuid, new ArrayList<>());
		Iterator<Long> it = t.iterator();
		while (it.hasNext()) {
			long v = it.next();
			if (System.currentTimeMillis() - v > TimeUnit.MINUTES.toMillis(5))
				it.remove();
		}

		return 1 - Math.min(t.size() / 30.0, 1);
	}

	@Override
	public float getWeight() {
		return .3f;
	}

}
