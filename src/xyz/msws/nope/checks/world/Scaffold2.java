package xyz.msws.nope.checks.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.checks.Check;
import xyz.msws.nope.modules.checks.CheckType;
import xyz.msws.nope.modules.data.CPlayer;

/**
 * Checks if a player places a block right below themselves right after quickly
 * changing pitch
 * 
 * @author imodm
 *
 */
public class Scaffold2 implements Check, Listener {

	private NOPE plugin;

	@Override
	public CheckType getType() {
		return CheckType.WORLD;
	}

	private Map<UUID, List<Double>> pitches = new HashMap<>();

	@Override
	public void register(NOPE plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	private final int SIZE = 20;

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		List<Double> avgPitches = pitches.getOrDefault(player.getUniqueId(), new ArrayList<>());

		avgPitches.add(0, (double) player.getLocation().getPitch());

		for (int i = SIZE; i < avgPitches.size(); i++) {
			avgPitches.remove(i);
		}

		pitches.put(player.getUniqueId(), avgPitches);
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);

		List<Double> avgPitches = pitches.getOrDefault(player.getUniqueId(), new ArrayList<>());
		if (avgPitches.size() < SIZE)
			return;

		if (event.getBlock().getRelative(BlockFace.DOWN).getType().isSolid())
			return;

		if (event.getBlock().getType() == Material.SCAFFOLDING)
			return;

		double total = 0;
		for (double d : avgPitches)
			total += d;
		total /= avgPitches.size();

		double diff = Math.abs((player.getLocation().getPitch() - total));

		if (diff < 30)
			return;

		cp.flagHack(this, (int) Math.round((diff - 30) / 5), "Diff: &e" + diff + "&7 >= &a30");
	}

	@Override
	public String getCategory() {
		return "Scaffold";
	}

	@Override
	public String getDebugName() {
		return "Scaffold#2";
	}

	@Override
	public boolean lagBack() {
		return true;
	}
}
