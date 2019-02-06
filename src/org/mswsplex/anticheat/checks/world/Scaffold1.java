package org.mswsplex.anticheat.checks.world;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.mswsplex.anticheat.checks.Check;
import org.mswsplex.anticheat.checks.CheckType;
import org.mswsplex.anticheat.data.CPlayer;
import org.mswsplex.anticheat.msws.AntiCheat;

public class Scaffold1 implements Check, Listener {

	private AntiCheat plugin;

	@Override
	public CheckType getType() {
		return CheckType.WORLD;
	}

	@Override
	public void register(AntiCheat plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);
		Block targetBlock = player.getTargetBlock((Set<Material>) null, 10);
		if (targetBlock.equals(event.getBlockPlaced()))
			return;
		cp.flagHack(this, 5);
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
