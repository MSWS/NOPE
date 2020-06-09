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
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class InteractionRating implements TrustRating, Listener {
	private Map<UUID, List<Long>> times = new HashMap<>();

	public InteractionRating(JavaPlugin plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		addAction(event.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		addAction(event.getWhoClicked().getUniqueId());
	}

	@EventHandler
	public void onConsume(PlayerItemConsumeEvent event) {
		addAction(event.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		addAction(event.getPlayer().getUniqueId());
	}

	private void addAction(UUID player) {
		List<Long> t = times.getOrDefault(player, new ArrayList<>());
		t.add(System.currentTimeMillis());
		times.put(player, t);
	}

	@Override
	public double getTrust(UUID uuid) {
		List<Long> t = times.getOrDefault(uuid, new ArrayList<>());
		Iterator<Long> it = t.iterator();
		while (it.hasNext()) {
			long v = it.next();
			if (System.currentTimeMillis() - v > TimeUnit.MINUTES.toMillis(10))
				it.remove();
		}

		return 1 - Math.min(t.size() / 20000.0, 1);
	}

	@Override
	public float getWeight() {
		return .05f;
	}

}
