package org.mswsplex.anticheat.checks.world;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.mswsplex.anticheat.checks.Check;
import org.mswsplex.anticheat.checks.CheckType;
import org.mswsplex.anticheat.data.CPlayer;
import org.mswsplex.anticheat.msws.AntiCheat;

public class Scaffold2 implements Check, Listener {

	private AntiCheat plugin;

	@Override
	public CheckType getType() {
		return CheckType.WORLD;
	}

	@Override
	public void register(AntiCheat plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);

		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
			for (Player p : Bukkit.getOnlinePlayers()) {
				CPlayer cp = plugin.getCPlayer(p);
				cp.setTempData("scaffoldBlocksPlaced", 0);
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

		int blocksPlaced = cp.getTempInteger("scaffoldBlocksPlaced");
		cp.setTempData("scaffoldBlocksPlaced", blocksPlaced + 1);

		if (blocksPlaced <= 5)
			return;

		cp.flagHack(this, (blocksPlaced - 5) * 10);
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
