package org.mswsplex.anticheat.checks.world;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.mswsplex.anticheat.checks.Check;
import org.mswsplex.anticheat.checks.CheckType;
import org.mswsplex.anticheat.data.CPlayer;
import org.mswsplex.anticheat.msws.NOPE;

/**
 * Checks if block broken if liquid
 * 
 * @author imodm
 *
 */
public class IllegalBlockBreak1 implements Check, Listener {

	private NOPE plugin;

	@Override
	public CheckType getType() {
		return CheckType.WORLD;
	}

	@Override
	public void register(NOPE plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onBlockPlace(BlockBreakEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);
		if (!event.getBlock().isLiquid())
			return;

		cp.flagHack(this, 50);
	}

	@Override
	public String getCategory() {
		return "IllegalBlockBreak";
	}

	@Override
	public String getDebugName() {
		return "IllegalBlockBreak#1";
	}

	@Override
	public boolean lagBack() {
		return false;
	}

	@Override
	public boolean onlyLegacy() {
		return false;
	}
}
