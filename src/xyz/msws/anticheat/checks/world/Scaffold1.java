package xyz.msws.anticheat.checks.world;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.modules.checks.Check;
import xyz.msws.anticheat.modules.checks.CheckType;
import xyz.msws.anticheat.modules.data.CPlayer;

/**
 * Checks how many blocks a player places right below themselves
 * 
 * @author imodm
 *
 */
public class Scaffold1 implements Check, Listener {

	private NOPE plugin;

	@Override
	public CheckType getType() {
		return CheckType.WORLD;
	}

	private Map<UUID, Integer> blocks = new HashMap<>();

	@Override
	public void register(NOPE plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);

		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
			for (Player p : Bukkit.getOnlinePlayers()) {
				blocks.put(p.getUniqueId(), 0);
			}
		}, 0, 40);
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);

		if (player.isFlying())
			return;

		Block placed = event.getBlockPlaced();

		if (!player.getLocation().subtract(0, 1, 0).getBlock().equals(placed))
			return;

		int blocksPlaced = blocks.getOrDefault(player.getUniqueId(), 0);
		blocks.put(player.getUniqueId(), blocksPlaced + 1);

		if (blocksPlaced <= 5)
			return;

		cp.flagHack(this, (blocksPlaced - 5) * 10, "Blocks: &e" + blocksPlaced + "&7 > &a5");
	}

	@Override
	public String getCategory() {
		return "Scaffold";
	}

	@Override
	public String getDebugName() {
		return "Scaffold#1";
	}

	@Override
	public boolean lagBack() {
		return true;
	}
}
