package org.mswsplex.anticheat.checks.world;

import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.mswsplex.anticheat.checks.Check;
import org.mswsplex.anticheat.checks.CheckType;
import org.mswsplex.anticheat.data.CPlayer;
import org.mswsplex.anticheat.msws.AntiCheat;

/**
 * Checks if the placed blockface is the same as the face interacted iwth
 * 
 * @author imodm
 *
 */
public class Scaffold3 implements Check, Listener {

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
	public void onBlockPlace(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);

		Block target = player.getTargetBlock((Set<Material>) null, 10);

		if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;

		if (!event.isBlockInHand())
			return;

		if (player.isFlying())
			return;

		if (player.getLocation().getY() <= target.getLocation().getY())
			return;

		if (target.isLiquid())
			return;

		List<Block> blocks = player.getLastTwoTargetBlocks((Set<Material>) null, 10);
		BlockFace face = null;
		if (blocks.size() > 1) {
			face = blocks.get(1).getFace(blocks.get(0));
		} else {
			return;
		}

		if (event.getBlockFace() == face)
			return;

		cp.flagHack(this, 5);
	}

	@Override
	public String getCategory() {
		return "Scaffold";
	}

	@Override
	public String getDebugName() {
		return "Scaffold#3";
	}

	@Override
	public boolean lagBack() {
		return true;
	}

	@Override
	public boolean onlyLegacy() {
		// TODO Auto-generated method stub
		return false;
	}

}
